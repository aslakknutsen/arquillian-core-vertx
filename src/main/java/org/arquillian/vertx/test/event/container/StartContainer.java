package org.arquillian.vertx.test.event.container;

import org.arquillian.vertx.test.event.BaseEvent;
import org.junit.runner.Result;

public class StartContainer extends BaseEvent<Result> {
    
    public StartContainer() {
        super("container.start");
    }
    
    public Result get() {
        return null;
    }
}
