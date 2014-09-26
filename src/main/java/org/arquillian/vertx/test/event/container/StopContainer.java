package org.arquillian.vertx.test.event.container;

import org.arquillian.vertx.test.event.BaseEvent;
import org.junit.runner.Result;

public class StopContainer extends BaseEvent<Result> {
    
    public StopContainer() {
        super("container.stop");
    }
    
    public Result get() {
        return null;
    }
}
