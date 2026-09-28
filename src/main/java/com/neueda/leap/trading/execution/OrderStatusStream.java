package com.neueda.leap.trading.execution;
import java.io.IOException;import java.util.concurrent.CopyOnWriteArrayList;import org.springframework.stereotype.Component;import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
@Component public class OrderStatusStream{
 private final CopyOnWriteArrayList<SseEmitter> clients=new CopyOnWriteArrayList<>();
 public SseEmitter subscribe(){SseEmitter e=new SseEmitter(0L);clients.add(e);e.onCompletion(()->clients.remove(e));e.onTimeout(()->clients.remove(e));e.onError(x->clients.remove(e));return e;}
 public void emit(OrderStatusMessage message){for(SseEmitter e:clients)try{e.send(SseEmitter.event().name("order-status").data(message));}catch(IOException ex){clients.remove(e);}}
}
