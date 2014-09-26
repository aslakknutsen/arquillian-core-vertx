package org.arquillian.vertx.test.event.container;

import org.arquillian.vertx.test.event.BaseEvent;
import org.junit.runner.Result;

public class DeployToContainer extends BaseEvent<Result> {
    
    public DeployToContainer() {
        super("container.deployToContainer");
    }
    
    public Result get() {
        return null;
    }
}
