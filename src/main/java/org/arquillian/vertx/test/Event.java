package org.arquillian.vertx.test;

public interface Event<RESPONSE> {

    String getAddress();
    
    RESPONSE get();
}
