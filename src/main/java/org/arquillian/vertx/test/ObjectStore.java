package org.arquillian.vertx.test;

import java.util.concurrent.ConcurrentHashMap;

public class ObjectStore {

    private ConcurrentHashMap<String, Object> map;
    
    public ObjectStore() {
        this.map = new ConcurrentHashMap<String, Object>();
    }
    
    public void put(String id, Object value) {
        this.map.put(id, value);
    }
    
    public Object get(String id) {
        return map.get(id);
    }
}
