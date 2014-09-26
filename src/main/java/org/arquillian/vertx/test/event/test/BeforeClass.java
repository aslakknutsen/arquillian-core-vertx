package org.arquillian.vertx.test.event.test;

import org.arquillian.vertx.test.event.BaseEvent;

public class BeforeClass extends BaseEvent<Boolean> {
    
    private String testClass;
    
    public BeforeClass(String testClass) {
        super("test.BeforeClass");
        this.testClass = testClass;
    }

    public String getTestClass() {
        return testClass;
    }

    public Boolean get() {
        return true;
    }
}
