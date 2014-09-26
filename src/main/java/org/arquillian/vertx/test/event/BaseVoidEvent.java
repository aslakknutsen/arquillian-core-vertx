package org.arquillian.vertx.test.event;

public class BaseVoidEvent extends BaseEvent<Void> {

    public BaseVoidEvent(String address) {
        super(address);
    }
    
    public Void get() {
        return null;
    }
}
