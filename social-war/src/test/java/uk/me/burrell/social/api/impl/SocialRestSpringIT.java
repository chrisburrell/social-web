package uk.me.burrell.social.api.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.me.burrell.social.service.utils.ProgrammeTestUtils.getCategoriesWithDrama;

import java.net.URI;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import uk.me.burrell.social.api.models.Programme;

/**
 * This test demonstrates knowledge of wiring Spring Test and execute a normal "add" flow.
 * 
 * An integration test which will do end to end scenarios.
 * 
 * 
 * @author chrisburrell
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class SocialRestSpringIT {
    @Resource(name = "restTemplate")
    private RestTemplate template;

    @Resource(name = "testUrl")
    private URI url;

    /**
     * adds a person and checks that the person has been persisted
     */
    @Test
    public void testAddProgramme() {
        final Programme p = new Programme();
        p.setCategory(getCategoriesWithDrama());
        p.setTitle("The test show");
        p.setDescription("Some test drama");

        final Programme response = this.template.postForObject(this.url, p, Programme.class);
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(p.getTitle(), response.getTitle());
        assertEquals(p.getDescription(), response.getDescription());
    }

    /**
     * @param template the template to set
     */
    public void setTemplate(final RestTemplate template) {
        this.template = template;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(final URI url) {
        this.url = url;
    }
}
