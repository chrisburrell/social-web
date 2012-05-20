package uk.me.burrell.social.api.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.me.burrell.social.api.models.Programme;
import uk.me.burrell.social.api.models.ProgrammeCategory;
import uk.me.burrell.social.api.models.ProgrammeList;
import uk.me.burrell.social.api.service.SocialService;

/**
 * Some unit tests for the Social REST
 * <p>
 * These are complimented by end to end tests run by the maven failsafe plugin. See
 * {@link SocialRestAPISpringIT} and {@link SocialRestAPIIT} for some examples using 2 different frameworks.
 * 
 * @author chrisburrell
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class SocialRestAPIUnitTest {
    @Mock
    private SocialService service;
    private SocialRestAPIImpl restApiUnderTest;

    /**
     * sets up the test
     */
    @Before
    public void setUp() {
        this.restApiUnderTest = new SocialRestAPIImpl();
        this.restApiUnderTest.setService(this.service);
    }

    /**
     * testing that service is called when adding and that the programme returned is the one from the service
     * layer, not the input
     */
    @Test
    public void testAdd() {
        final Programme input = new Programme();
        final Programme returned = new Programme();

        // when
        when(this.service.addProgramme(input)).thenReturn(returned);

        // do
        final Programme newProgramme = this.restApiUnderTest.addProgramme(input);

        // then
        assertEquals(returned, newProgramme);
    }

    /**
     * testing that service is called appropriately and return value is not the input but the one set up by
     * the test
     */
    @Test
    public void testUpdate() {
        final Programme input = new Programme();
        final Programme returned = new Programme();

        // when
        when(this.service.updateProgramme(input)).thenReturn(returned);

        // do
        final Programme updatedProgramme = this.restApiUnderTest.updateProgramme(1L, input);

        // then
        assertEquals(returned, updatedProgramme);
    }

    /**
     * tests that deletion proxies through to service
     */
    @Test
    public void testDelete() {
        final Long id = 12L;

        // do
        this.restApiUnderTest.deleteProgramme(id);

        // then
        verify(this.service).deleteProgramme(id);
    }

    /**
     * test that find by id returns a programme with the correct programme provided by the underlying service
     * layer
     */
    @Test
    public void testFindById() {
        final Long id = 13L;
        final Programme searchResult = new Programme();

        // when
        when(this.service.findById(id)).thenReturn(searchResult);

        // do
        final Programme testOutput = this.restApiUnderTest.getProgrammeById(id);

        // then
        assertEquals(searchResult, testOutput);
    }

    /**
     * test that find by category calls the right underlying service
     */
    @Test
    public void testFindByCategory() {
        final ProgrammeList searchResult = new ProgrammeList();

        // when
        when(this.service.findByCategory(any(ProgrammeCategory.class))).thenReturn(searchResult);

        // do
        final ProgrammeList testOutput = this.restApiUnderTest
                .getProgrammesByCategory(ProgrammeCategory.COMEDY);

        // then
        assertEquals(searchResult, testOutput);
    }

    /**
     * test that find by availability calls the right underlying service
     */
    @Test
    public void testFindByAvailability() {
        final boolean availability = true;
        final ProgrammeList searchResult = new ProgrammeList();

        // when
        when(this.service.findByAvailability(availability)).thenReturn(searchResult);

        // do
        final ProgrammeList testOutput = this.restApiUnderTest.getProgrammesByAvailability(availability);

        // then
        assertEquals(searchResult, testOutput);
    }

    /**
     * test that find by availability and category calls the right underlying service
     */
    @Test
    public void testFindByCategoryAndAvailability() {
        final ProgrammeCategory category = ProgrammeCategory.DRAMA;
        final boolean availability = true;
        final ProgrammeList searchResult = new ProgrammeList();

        // when
        when(this.service.findByCategoryAndAvailability(availability, category)).thenReturn(searchResult);

        // do
        final ProgrammeList testOutput = this.restApiUnderTest.getProgrammesByAvailabilityAndCategory(
                category, availability);

        // then
        assertEquals(searchResult, testOutput);
    }

    /**
     * testing passing null parameter to add method
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullAdd() {
        this.restApiUnderTest.addProgramme(null);
    }

    /**
     * testing passing null id to update method
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullIdUpdate() {
        this.restApiUnderTest.updateProgramme(null, new Programme());
    }

    /**
     * testing passing null programme to update method
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullProgrammeUpdate() {
        this.restApiUnderTest.updateProgramme(null, new Programme());
    }

    /**
     * testing passing null parameter to delete method
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullDelete() {
        this.restApiUnderTest.deleteProgramme(null);
    }

    /**
     * testing passing null parameter to find by id method
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullFindById() {
        this.restApiUnderTest.getProgrammeById(null);
    }
}
