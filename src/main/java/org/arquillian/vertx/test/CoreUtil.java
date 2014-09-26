package org.arquillian.vertx.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public final class CoreUtil {

    private static final CoreUtil INSTANCE = new CoreUtil();
    
    private ObjectMapper mapper;

    // TODO: Externalize, back by Vert.x SharedStore
    private ObjectStore objectStore;
    
    private CoreUtil() {
        objectStore = new ObjectStore();
        
        mapper = new ObjectMapper();
        mapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
        mapper.configure(MapperFeature.AUTO_DETECT_GETTERS, false);
        mapper.configure(MapperFeature.AUTO_DETECT_SETTERS, false);
        mapper.setVisibilityChecker(
                mapper.getVisibilityChecker()
                    .withFieldVisibility(Visibility.ANY));
        SimpleModule module = new SimpleModule("Arquillian");
        module.addSerializer(new ObjectRefSerializer(objectStore));
        module.addDeserializer(ObjectRef.class, new ObjectRefDeSerializer(objectStore));
        mapper.registerModule(module);
    }
    
    
    public static String write(Object event) {
        try {
            return INSTANCE.mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T> T read(String data, Class<T> type) {
        try {
            return INSTANCE.mapper.readValue(data, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
