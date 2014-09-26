package org.arquillian.vertx.test.event;

import org.arquillian.vertx.test.event.core.RegisterExtension;
import org.arquillian.vertx.test.modulespi.Observer;

public class CoreEvents {

    public static RegisterExtension register(String name, Observer[] observes) {
        return new RegisterExtension(name, observes);
    }
}
