package org.arquillian.vertx.test.event;

import junit.framework.Assert;

import org.arquillian.vertx.test.CoreUtil;
import org.arquillian.vertx.test.ObjectRef;
import org.junit.Test;

public class ObjectRefSerializationTestCase {

    @Test
    public void shouldNotSerializeObjectRefValue() {
        String refValue = "_VALUE_NOT_SERIALIZED_";
        String json = CoreUtil.write(new DummyEvent(refValue));
        System.out.println(json);
        
        Assert.assertTrue(!json.contains(refValue));

        DummyEvent event = CoreUtil.read(json, DummyEvent.class);
        System.out.println(event);
        
        Assert.assertEquals(refValue, event.getTest());
    }
    
    private static class DummyEvent extends BaseVoidEvent {
        
        private ObjectRef<String> test;
        
        public DummyEvent(String test) {
            this();
            this.test = new ObjectRef<String>(test);
        }

        protected DummyEvent() {
            super("dummy");
        }
        
        public String getTest() {
            return test.getValue();
        }

        @Override
        public String toString() {
            return "DummyEvent [test=" + test + "]";
        }
    }
}
