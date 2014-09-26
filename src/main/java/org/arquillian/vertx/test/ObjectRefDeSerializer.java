package org.arquillian.vertx.test;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class ObjectRefDeSerializer extends StdDeserializer<ObjectRef<?>> {

    private static final long serialVersionUID = 1L;
    private ObjectStore store;

    public ObjectRefDeSerializer(ObjectStore store) {
        super(ObjectRef.class);
        this.store = store;
    }

    @Override
    public ObjectRef<?> deserialize(JsonParser jp, DeserializationContext ctxt) 
            throws IOException, JsonProcessingException {

        String id = jp.getValueAsString();
        return new ObjectRef<Object>(id, store.get(id));
    }

}
