package uk.me.burrell.social.api.views;

import java.util.Map;

import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

/**
 * 
 * @author chrisburrell
 * 
 */
public class JsonView extends MappingJacksonJsonView {

    /*
     * we override here because otherwise, Spring's Content negotiator adds its own wrapper around our
     * elements!
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Object filterModel(final Map<String, Object> model) {
        final Map<String, Object> filteredModel = (Map<String, Object>) super.filterModel(model);
        if (filteredModel.size() != 1) {
            return filteredModel;
        }
        return filteredModel.entrySet().iterator().next().getValue();
    }
}
