package org.arquillian.vertx.test.modulex;

import org.arquillian.vertx.test.event.test.Before;
import org.arquillian.vertx.test.spi.Observes;

public class TestObserver {

    public void before(@Observes Before event) {
        System.out.println("\t\tBefore Observer 1");
    }

    public void before2(@Observes Before event) {
        System.out.println("\t\tBefore Observer 2");
    }
}
