package org.arquillian.vertx.test.event.test;

import org.arquillian.vertx.test.event.BaseEvent;

public class AfterClass extends BaseEvent<Boolean> {
    
    private String testClass;
    
    public AfterClass(String testClass) {
        super("test.AfterClass");
        this.testClass = testClass;
    }

    public String getTestClass() {
        return testClass;
    }

    public Boolean get() {
        return true;
    }
}
