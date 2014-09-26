package org.arquillian.vertx.test.event.test;

import org.arquillian.vertx.test.event.BaseEvent;

public class Test extends BaseEvent<Boolean> {
    
    private String testClass;
    
    private String testMethod;
    
    public Test(String testClass, String testMethod) {
        super("test.Test");
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
