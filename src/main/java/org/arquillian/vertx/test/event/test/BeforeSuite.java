package org.arquillian.vertx.test.event.test;

import org.arquillian.vertx.test.event.BaseVoidEvent;

public class BeforeSuite extends BaseVoidEvent {

    public BeforeSuite() {
        super("test.BeforeSuite");
    }
}
