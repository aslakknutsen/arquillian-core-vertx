package org.arquillian.vertx.test;

import java.util.UUID;

public class ObjectRef<T> {

    private String id;
    private transient T value;

    public ObjectRef(T value) {
        this(UUID.randomUUID().toString(), value);
    }

    ObjectRef(String id, T value) {
        this.id = id;
        this.value = value;
    }
    
    public T getValue() {
        return value;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ObjectRef [id=" + id + ", value=" + value + "]";
    }
}
