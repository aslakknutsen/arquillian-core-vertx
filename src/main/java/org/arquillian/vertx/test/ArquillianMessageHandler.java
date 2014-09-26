package org.arquillian.vertx.test;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;

public abstract class ArquillianMessageHandler<T, X> implements Handler<Message<String>> {

    private Class<T> eventType;
    private Class<X> responseType;

    public ArquillianMessageHandler(Class<T> eventType, Class<X> responseType) {
        this.eventType = eventType;
        this.responseType = responseType;
    }
    
    public void handle(Message<String> event) {
        T eventParsed = CoreUtil.read(event.body(), eventType);
        Object obj = message(eventParsed);
        event.reply(true);
//        if(responseType != Void.class && obj != null) {
//            event.reply(CoreUtil.write(obj));
//        } 
    }

    public abstract X message(T event);

}
