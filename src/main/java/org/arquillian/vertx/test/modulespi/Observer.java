package org.arquillian.vertx.test.modulespi;

public class Observer {
    private String address;

    private String event;

    public Observer(String address, String event) {
        this();
        this.address = address;
        this.event = event;
    }
    
    protected Observer() {}

    public String getAddress() {
        return address;
    }

    public String getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return "Observer [address=" + address + ", event=" + event + "]";
    }
}
