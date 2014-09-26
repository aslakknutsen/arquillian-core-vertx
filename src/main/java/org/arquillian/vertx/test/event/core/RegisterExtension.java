package org.arquillian.vertx.test.event.core;

import java.util.Arrays;

import org.arquillian.vertx.test.event.BaseEvent;
import org.arquillian.vertx.test.modulespi.Observer;
import org.junit.runner.Result;

public class RegisterExtension extends BaseEvent<Result> {
    private String name;
    private Observer[] observes;

    public RegisterExtension(String name, Observer... observers) {
        this();
        this.name = name;
        this.observes = observers;
    }

    protected RegisterExtension() {
        super("core.extension.register");
    }
    
    public String getName() {
        return name;
    }

    public Observer[] getObserves() {
        return observes;
    }
    
    public Result get() {
        return null;
    }

    @Override
    public String toString() {
        return "RegisterExtension [name=" + name + ", observes="
                + Arrays.toString(observes) + "]";
    }
}
