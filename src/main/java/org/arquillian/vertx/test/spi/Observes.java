package org.arquillian.vertx.test.spi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Observes {

    public enum Phase {
        BEFORE,
        EVENT,
        AFTER
    }
    
    Phase phase() default Phase.EVENT;
}
