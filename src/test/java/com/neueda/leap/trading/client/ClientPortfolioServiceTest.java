package com.neueda.leap.trading.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ClientPortfolioServiceTest {
    @Test void computesMarketValueGainAndPercentageWithoutFloatingPoint() {
        CurrentClient client=mock(CurrentClient.class);
        ClientPortfolioMapper mapper=mock(ClientPortfolioMapper.class);
        UUID clientId=UUID.randomUUID();
        when(client.clientId()).thenReturn(clientId);
        PositionRow p=position(new BigDecimal("5"),new BigDecimal("510"),new BigDecimal("2000"));
        when(mapper.positions(clientId)).thenReturn(List.of(p));
        var result=new ClientPortfolioService(client,mapper).positions().get(0);
        assertEquals(0,new BigDecimal("2550").compareTo(result.marketValue()));
        assertEquals(0,new BigDecimal("550").compareTo(result.unrealizedGainLoss()));
        assertEquals(0,new BigDecimal("27.5").compareTo(result.unrealizedGainLossPercent()));
    }

    @Test void zeroCostBasisDoesNotDivideByZero() {
        CurrentClient client=mock(CurrentClient.class);
        ClientPortfolioMapper mapper=mock(ClientPortfolioMapper.class);
        UUID clientId=UUID.randomUUID();
        when(client.clientId()).thenReturn(clientId);
        when(mapper.positions(clientId)).thenReturn(List.of(position(BigDecimal.ONE,new BigDecimal("100"),BigDecimal.ZERO)));
        var result=new ClientPortfolioService(client,mapper).positions().get(0);
        assertEquals(0,BigDecimal.ZERO.compareTo(result.unrealizedGainLossPercent()));
    }

    private PositionRow position(BigDecimal quantity,BigDecimal price,BigDecimal basis) {
        PositionRow p=new PositionRow();
        p.setAccountId(UUID.randomUUID());
        p.setInstrumentId(UUID.randomUUID());
        p.setSymbol("MSFT");
        p.setInstrumentType("EQUITY");
        p.setCurrency("USD");
        p.setQuantity(quantity);
        p.setCurrentPrice(price);
        p.setCostBasis(basis);
        p.setUpdatedAt(Instant.now());
        return p;
    }
}
