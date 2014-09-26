package org.arquillian.vertx.test.core;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.arquillian.vertx.test.CoreUtil;
import org.arquillian.vertx.test.Event;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;

public class ArquillianBus {

    private ModuleRegistry registry;
    private EventBus bus;

    public ArquillianBus(EventBus bus, ModuleRegistry registry) {
        this.bus = bus;
        this.registry = registry;
    }
    
    public <X, T extends Event<X>> X send(T event) {
        final CountDownLatch latch = new CountDownLatch(1);
        bus.send(event.getAddress(), CoreUtil.write(event), new Handler<Message<Boolean>>() {
            
            public void handle(Message<Boolean> event) {
                latch.countDown();
            }
        });
        try {
            if(registry != null && registry.listensTo(event.getAddress()).length > 0) {
                if(!latch.await(10, TimeUnit.SECONDS)) {
                    throw new RuntimeException("Timeout waiting for reply");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return event.get();
    }
}
