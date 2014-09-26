package org.arquillian.vertx.test.event;

import java.lang.reflect.Method;

import org.arquillian.vertx.test.event.test.After;
import org.arquillian.vertx.test.event.test.AfterClass;
import org.arquillian.vertx.test.event.test.AfterSuite;
import org.arquillian.vertx.test.event.test.Before;
import org.arquillian.vertx.test.event.test.BeforeClass;
import org.arquillian.vertx.test.event.test.BeforeSuite;
import org.arquillian.vertx.test.event.test.Test;

public final class TestEvents {

    public static BeforeSuite beforeSuite() {
        return new BeforeSuite();
    }

    public static BeforeClass beforeClass(Class<?> test) {
        return new BeforeClass(test.getName());
    }

    public static Before beforeTest(Class<?> test, Method method) {
        return new Before(test.getName(), method.getName());
    }

    public static Test test(Class<?> test, Method method) {
        return new Test(test.getName(), method.getName());
    }

    public static After afterTest(Class<?> test, Method method) {
        return new After(test.getName(), method.getName());
    }

    public static AfterClass afterClass(Class<?> test) {
        return new AfterClass(test.getName());
    }

    public static AfterSuite afterSuite() {
        return new AfterSuite();
    }
}
