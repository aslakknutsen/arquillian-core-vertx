package org.arquillian.vertx.test.event.test;

import org.arquillian.vertx.test.event.BaseEvent;

public class After extends BaseEvent<Boolean> {
    
    private String testClass;
    
    private String testMethod;
    
    public After(String testClass, String testMethod) {
        super("test.After");
        this.testClass = testClass;
        this.testMethod = testMethod;
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
