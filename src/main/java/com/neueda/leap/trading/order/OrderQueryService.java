package com.neueda.leap.trading.order;

import java.util.List; import java.util.Set; import java.util.UUID; import java.util.stream.Collectors;
import com.neueda.leap.trading.account.Account; import com.neueda.leap.trading.account.AccountRepository; import com.neueda.leap.trading.api.ResourceNotFoundException; import com.neueda.leap.trading.client.CurrentClient;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
@Service @Transactional(readOnly=true)
public class OrderQueryService {
 private final CurrentClient currentClient; private final AccountRepository accounts; private final OrderRepository orders; private final OrderEventRepository events;
 public OrderQueryService(CurrentClient currentClient,AccountRepository accounts,OrderRepository orders,OrderEventRepository events){this.currentClient=currentClient;this.accounts=accounts;this.orders=orders;this.events=events;}
 public List<OrderResponse> findAll(){Set<UUID> ids=ownedAccountIds();if(ids.isEmpty())return List.of();return orders.findByAccountIdInOrderBySubmittedAtDesc(ids).stream().map(OrderResponse::from).toList();}
 public OrderResponse findOne(UUID orderId){return OrderResponse.from(ownedOrder(orderId));}
 public List<OrderEventResponse> lifecycle(UUID orderId){ownedOrder(orderId);return events.findByOrderIdOrderByEventTimeAscOrderEventIdAsc(orderId).stream().map(OrderEventResponse::from).toList();}
 private Order ownedOrder(UUID id){Set<UUID> ids=ownedAccountIds();return orders.findByOrderIdAndAccountIdIn(id,ids).orElseThrow(() -> new ResourceNotFoundException("Order not found"));}
 private Set<UUID> ownedAccountIds(){return accounts.findByClientIdOrderByAccountNumber(currentClient.clientId()).stream().map(Account::getAccountId).collect(Collectors.toSet());}
}
