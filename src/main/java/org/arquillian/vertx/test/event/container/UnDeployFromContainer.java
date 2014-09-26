package org.arquillian.vertx.test.event.container;

import org.arquillian.vertx.test.event.BaseEvent;
import org.junit.runner.Result;

public class UnDeployFromContainer extends BaseEvent<Result> {
    
    public UnDeployFromContainer() {
        super("container.undeployFromContainer");
    }
    
    public Result get() {
        return null;
    }
}
