package org.arquillian.vertx.test.modulespi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.arquillian.vertx.test.ArquillianMessageHandler;
import org.arquillian.vertx.test.Event;
import org.arquillian.vertx.test.core.ArquillianBus;
import org.arquillian.vertx.test.event.CoreEvents;
import org.arquillian.vertx.test.event.core.RegisterExtension;
import org.arquillian.vertx.test.spi.Observes;
import org.vertx.java.platform.Verticle;

public abstract class ExtensionMain extends Verticle {

    @Override
    public final void start() {
        RegisterExtension event = createRegisterEvent();
        ArquillianBus bus = new ArquillianBus(getVertx().eventBus(), null);
        
        bus.send(event);
    }

    private RegisterExtension createRegisterEvent() {
        final Set<String> names = new HashSet<String>();
        final Set<Observer> addresses = new HashSet<Observer>();
        register(new Extension() {
            public void register(String name, Class<?>... observers) { 
                names.add(name);
                try {
                    for(Class<?> observer : observers) {
                        Method[] methods = observer.getDeclaredMethods();
                        for(final Method method : methods) {
                            if(method.getParameterAnnotations().length == 1) {
                                if(method.getParameterAnnotations()[0][0].annotationType() == Observes.class) {
                                    Observes metaData = (Observes)method.getParameterAnnotations()[0][0];
                                    final Object instance = observer.newInstance();
                                    Class<Object> type = (Class<Object>)method.getParameterTypes()[0];
                                    Constructor<?> typeConst = type.getDeclaredConstructor();
                                    typeConst.setAccessible(true);
                                    Event<?> typeInst = (Event<?>)typeConst.newInstance();
                                    final String address = UUID.randomUUID().toString();
                                    addresses.add(new Observer(address, typeInst.getAddress()));
                                    vertx.eventBus().registerLocalHandler(
                                            address,
                                            new ArquillianMessageHandler<Object, Object>(type, Object.class) {
                                                public Object message(Object event) {
                                                    //System.out.println("Extension received " + address);
                                                    try {
                                                        method.invoke(instance, event);
                                                    } catch (Exception e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                    return null;
                                                }
                                            });
                                }
                            }
                        }
                    }
                } catch(Exception e) {
                    System.out.println(e);
                }
            }
        });
        return CoreEvents.register(names.iterator().next(), addresses.toArray(new Observer[0]));
    }

    @Override
    public final void stop() {
        super.stop();
    }
 
    public abstract void register(Extension extension);

    
}
