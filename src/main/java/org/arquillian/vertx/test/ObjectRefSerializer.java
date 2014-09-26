package org.arquillian.vertx.test;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ObjectRefSerializer extends StdSerializer<ObjectRef<?>> {

    private ObjectStore store;

    public ObjectRefSerializer(ObjectStore store) {
        super(ObjectRef.class, true);
        this.store = store;
    }

    @Override
    public void serialize(ObjectRef<?> value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        
        store.put(value.getId(), value.getValue());
        jgen.writeString(value.getId());
    }

}
