package uk.me.burrell.social.api.views;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Testing the JSON view to ensure that map of 1 object yields the object itself!
 * 
 * @author chrisburrell
 * 
 */
public class JsonViewTest {
    /**
     * testing the filter method with one element
     */
    @Test
    public void testFilterOneElement() {
        final JsonView jsonView = new JsonView();
        final Map<String, Object> map = new HashMap<String, Object>();
        final Object value = new Object();
        map.put("key", value);

        assertEquals(value, jsonView.filterModel(map));
    }

    /**
     * testing the filter method with multiple elements (future-proofing)
     */
    @Test
    public void testFilterMultipleElement() {
        final JsonView jsonView = new JsonView();
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", new Object());
        map.put("key2", new Object());

        // assert that we get a map back out

        assertEquals(map, jsonView.filterModel(map));
    }
}
