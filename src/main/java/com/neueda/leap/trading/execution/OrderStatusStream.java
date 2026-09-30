package com.neueda.leap.trading.execution;
import java.io.IOException;import java.util.*;import java.util.concurrent.ConcurrentHashMap;import java.util.concurrent.CopyOnWriteArrayList;import org.springframework.stereotype.Component;import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
@Component public class OrderStatusStream{
 private final Map<UUID,CopyOnWriteArrayList<SseEmitter>> clients=new ConcurrentHashMap<>();
 public SseEmitter subscribe(UUID clientId){SseEmitter e=new SseEmitter(0L);var list=clients.computeIfAbsent(clientId,k->new CopyOnWriteArrayList<>());list.add(e);Runnable remove=()->{list.remove(e);if(list.isEmpty())clients.remove(clientId,list);};e.onCompletion(remove);e.onTimeout(remove);e.onError(x->remove.run());return e;}
 public void emit(UUID clientId,OrderStatusMessage message){var list=clients.get(clientId);if(list==null)return;for(SseEmitter e:list)try{e.send(SseEmitter.event().name("order-status").data(message));}catch(IOException ex){list.remove(e);}}
}
