package org.arquillian.vertx.test.modulex;

import org.arquillian.vertx.test.modulespi.Extension;
import org.arquillian.vertx.test.modulespi.ExtensionMain;

public class ModuleXExtension extends ExtensionMain {

    @Override
    public void register(Extension extension) {
        extension.register("ModuleX", new Class<?>[] {TestObserver.class});
    }
}
