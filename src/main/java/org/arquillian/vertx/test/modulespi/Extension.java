package org.arquillian.vertx.test.modulespi;

public interface Extension {

    public void register(String name, Class<?>... observers);
}
