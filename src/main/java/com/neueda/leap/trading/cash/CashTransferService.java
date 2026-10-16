package com.neueda.leap.trading.cash;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;
import com.neueda.leap.trading.account.Account;
import com.neueda.leap.trading.account.AccountRepository;
import com.neueda.leap.trading.api.ResourceNotFoundException;
import com.neueda.leap.trading.client.CurrentClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CashTransferService {
    private final CurrentClient client;
    private final AccountRepository accounts;
    private final CashTransferMapper mapper;
    private final CashWalletService wallet;

    public CashTransferService(CurrentClient client, AccountRepository accounts,
                               CashTransferMapper mapper, CashWalletService wallet) {
        this.client=client; this.accounts=accounts; this.mapper=mapper; this.wallet=wallet;
    }

    @Transactional
    public CashTransferResponse transfer(CashTransferRequest request) {
        if (request == null || request.transferId() == null ||
            request.fromAccountId() == null || request.toAccountId() == null ||
            request.fromAccountId().equals(request.toAccountId()) ||
            request.amount() == null || request.amount().signum() <= 0 ||
            request.amount().scale() > 8)
            throw new IllegalArgumentException("Valid transfer ID, distinct accounts, and positive amount (up to 8 decimals) are required");

        UUID clientId=client.clientId();
        Account from=owned(request.fromAccountId(),clientId);
        Account to=owned(request.toAccountId(),clientId);
        if (!"ACTIVE".equals(from.getStatus()) || !"ACTIVE".equals(to.getStatus()))
            throw new IllegalArgumentException("Both accounts must be active");

        // Lock in deterministic order. A retry waits for the first request to commit,
        // then reads the completed transfer instead of debiting again.
        if (mapper.lockAccounts(from.getAccountId(),to.getAccountId()).size()!=2)
            throw new ResourceNotFoundException("Account not found");
        CashTransferResponse prior=mapper.find(request.transferId(),clientId);
        if (prior!=null) {
            if (!prior.fromAccountId().equals(from.getAccountId()) ||
                !prior.toAccountId().equals(to.getAccountId()) ||
                prior.debitedAmount().compareTo(request.amount())!=0)
                throw new IllegalArgumentException("Transfer ID already used for different details");
            return prior;
        }

        String source=from.getBaseCurrency(), target=to.getBaseCurrency();
        FxRateResponse fx=wallet.rate(source,target);
        if (fx.rate()==null || fx.rate().signum()<=0)
            throw new IllegalArgumentException("Invalid FX rate");
        BigDecimal credit=request.amount().multiply(fx.rate()).setScale(8,RoundingMode.HALF_UP);
        if (credit.signum()<=0) throw new IllegalArgumentException("Converted amount is too small");
        Instant now=Instant.now();

        if (mapper.debit(from.getAccountId(),source,request.amount(),now)!=1)
            throw new IllegalArgumentException("Insufficient available cash in "+source);
        if (mapper.credit(to.getAccountId(),target,credit,now)!=1)
            throw new IllegalStateException("Destination credit failed");
        if (mapper.insert(request.transferId(),clientId,from.getAccountId(),to.getAccountId(),
                source,target,request.amount(),credit,fx.rate(),fx.source(),now)!=1)
            throw new IllegalStateException("Transfer record failed");
        if (mapper.ledger(UUID.randomUUID(),from.getAccountId(),request.transferId(),
                source,request.amount().negate(),"TRANSFER_OUT",now)!=1 ||
            mapper.ledger(UUID.randomUUID(),to.getAccountId(),request.transferId(),
                target,credit,"TRANSFER_IN",now)!=1)
            throw new IllegalStateException("Transfer ledger failed");

        return new CashTransferResponse(request.transferId(),from.getAccountId(),to.getAccountId(),
                source,target,request.amount(),credit,fx.rate(),fx.source(),now);
    }

    private Account owned(UUID id, UUID clientId) {
        return accounts.findByAccountIdAndClientId(id,clientId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }
}
