package org.arquillian.vertx.test.event.test;

import org.arquillian.vertx.test.event.BaseEvent;

public class Before extends BaseEvent<Boolean> {
    
    private String testClass;
    
    private String testMethod;
    
    public Before(String testClass, String testMethod) {
        this();
        this.testClass = testClass;
        this.testMethod = testMethod;
    }
    
    protected Before() {
        super("test.Before");
    }

    public String getTestClass() {
        return testClass;
    }

    public String getTestMethod() {
        return testMethod;
    }

    public Boolean get() {
        return true;
    }
}
