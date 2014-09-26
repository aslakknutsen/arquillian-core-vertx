package org.arquillian.vertx.test.event.test;

import org.arquillian.vertx.test.event.BaseVoidEvent;

public class AfterSuite extends BaseVoidEvent {

    public AfterSuite() {
        super("test.AfterSuite");
    }
}
