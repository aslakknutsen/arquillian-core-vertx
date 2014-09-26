package org.arquillian.vertx.test.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.arquillian.vertx.test.ArquillianMessageHandler;
import org.arquillian.vertx.test.Event;
import org.arquillian.vertx.test.event.core.RegisterExtension;
import org.arquillian.vertx.test.modulespi.Observer;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.PlatformLocator;
import org.vertx.java.platform.PlatformManager;

public class ArquillianAdapter {

    private PlatformManager manager;
    private ArquillianBus bus;
    private final ModuleRegistry registry;
    
    protected ArquillianAdapter(PlatformManager manager) {
        this.manager = manager;
        this.registry = new ModuleRegistry();
        this.bus = new ArquillianBus(manager.vertx().eventBus(), this.registry);
    }
    
    public ArquillianAdapter start() {
        final CountDownLatch latch = new CountDownLatch(1);
        manager.vertx().eventBus().registerLocalHandler("core.extension.register",
                new ArquillianMessageHandler<RegisterExtension, Void>(RegisterExtension.class, Void.class) {
                    public Void message(RegisterExtension event) {
                        System.out.println("Registered: " + event);
                        registry.addObserver(event.getName(), event.getObserves());
                        latch.countDown();
                        return null;
                    }
                });
        URL[] classpath = new URL[1];
        try {
            classpath[0] = new File("src/main/java").toURI().toURL();
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }

        manager.deployModuleFromClasspath("org.arquillian~core~1.0", new JsonObject(), 1, classpath, new Handler<AsyncResult<String>>() {
            public void handle(AsyncResult<String> event) {
                if(event.failed()) {
                    System.out.println("Module registration failed: " + event);
                }
            }
        });
        try {
            if(!latch.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Failed waiting for all modules to start");
            }
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
        for(final String event : registry.events()) {
            manager.vertx().eventBus().registerLocalHandler(event, new Handler<Message<String>>() {
                public void handle(final Message<String> message) {
                    Observer[] handlers = registry.listensTo(event);
                    final AtomicInteger countDown = new AtomicInteger(handlers.length);
                    for(Observer handler : handlers) {
                        System.out.println("\t Forward to " + handler.getAddress());
                        manager.vertx().eventBus().send(handler.getAddress(), message.body(), new Handler<Message<Boolean>>() {
                            public void handle(Message<Boolean> event) {
                                int current = countDown.decrementAndGet();
                                if(current == 0) {
                                    message.reply(true);
                                }
                            }
                        });
                    }
                }
            });
        }
        try {
            // Figure out how to wait for all Extensions to Register
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return this;
    }
    
    public static ArquillianAdapter build() {
        PlatformManager manager = PlatformLocator.factory.createPlatformManager();
        final ArquillianAdapter adapter = new ArquillianAdapter(manager);
        return adapter.start();
    }
    
    public <T extends Event<X>, X> X send(T event) {
        System.out.println("Sending event... " + event.getAddress());
        bus.send(event);
        return event.get();
    }
    
    public void shutdown() {
        manager.stop();
    }
}
