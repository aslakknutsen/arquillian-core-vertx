package org.arquillian.vertx.test.event;

import org.arquillian.vertx.test.Event;

public abstract class BaseEvent<X> implements Event<X> {

    private String address;

    public BaseEvent(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
