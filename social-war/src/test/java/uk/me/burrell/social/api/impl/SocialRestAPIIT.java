package uk.me.burrell.social.api.impl;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static com.jayway.restassured.mapper.ObjectMapper.JACKSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertTrue;
import static uk.me.burrell.social.service.utils.ProgrammeTestUtils.assertEqualsProgrammes;
import static uk.me.burrell.social.service.utils.ProgrammeTestUtils.getCategories;
import static uk.me.burrell.social.service.utils.ProgrammeTestUtils.getCategoriesWithDrama;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.me.burrell.social.api.models.Programme;
import uk.me.burrell.social.api.models.ProgrammeCategory;

import com.jayway.restassured.mapper.ObjectMapper;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.specification.RequestSpecification;

/**
 * This class demonstrates knowledge of using REST Assured, and thereby the hamcrest notation, using the
 * behaviour driven paradigm: given X, when Y, then X
 * 
 * @author chrisburrell
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class SocialRestAPIIT implements InitializingBean {
    private static final String ID_PARAM = "id";
    private static final String CATEGORY_PARAM = "category";
    private static final String AVAILABILITY_PARAM = "availability";
    private static int testIteration = 1;
    private static final String HTTP_ACCEPT = "Accept";
    private static final String TITLE = "Lord Sugar's career";
    private static final String DESCRIPTION = "An biographical documentary of Lord Sugar's career";
    private static final int HTTP_OK = 200;
    private static final int HTTP_NOT_FOUND = 404;

    @Resource(name = "testUrl")
    private URI uri;
    private String url;
    private String programmeIdUrl;
    private String findByCategory;
    private String findByAvailability;
    private String findByBoth;

    /**
     * tests adding a programme. This test is verbose because we explicitly test at the JSON level
     */
    @Test
    public void testAdd() {
        final Programme p = new Programme();
        p.setCategory(getCategoriesWithDrama());
        p.setTitle(TITLE);
        p.setDescription(DESCRIPTION);

        giveJsonRequest().body(p, JACKSON).expect().statusCode(HTTP_OK).contentType(JSON)
                .body(ID_PARAM, notNullValue()).body("title", equalTo(TITLE))
                .body("description", equalTo(DESCRIPTION)).post(this.url);
    }

    /**
     * tests retrieving a programme
     */
    @Test
    public void testRetrieveByProgrammeId() {
        // setup
        final Programme storedProgramme = addTestProgramme();

        // given + run test
        final Programme returnValue = giveJsonRequest().pathParam(ID_PARAM, storedProgramme.getId())
                .get(this.programmeIdUrl).as(Programme.class);

        // then check
        assertEqualsProgrammes(storedProgramme, returnValue);
    }

    /**
     * tests retrieving a programme, note other tests will interefere with this, so we check all programmes
     * that all returned are available
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRetrieveByProgrammesByAvailability() {
        // setup 2 available programmes and 1 not available
        addTestProgramme(true);
        addTestProgramme(true);
        addTestProgramme(false);

        // given + run test
        final String json = giveJsonRequest().pathParam(AVAILABILITY_PARAM, true)
                .get(this.findByAvailability).body().asString();

        assertProgrammeIsAvailable(json);
    }

    /**
     * tests retrieving a programme
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testRetrieveProgrammesByCategory() {
        // setup various combinations of programmes, the first 2 of which should return in our search
        addTestProgramme(ProgrammeCategory.FILMS, ProgrammeCategory.CRIME);
        addTestProgramme(ProgrammeCategory.DRAMA, ProgrammeCategory.FILMS, ProgrammeCategory.LIFE_STORIES);
        addTestProgramme(ProgrammeCategory.ENTERTAINMENT);
        addTestProgramme(ProgrammeCategory.MONEY, ProgrammeCategory.COMEDY, ProgrammeCategory.LIFE_STORIES);

        // given + run test
        final String json = giveJsonRequest().pathParam(CATEGORY_PARAM, ProgrammeCategory.FILMS)
                .get(this.findByCategory).body().asString();

        assertProgrammesWithCategory(json, ProgrammeCategory.FILMS);
    }

    /**
     * tests retrieving a programme
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRetrieveByCategoryAndAvailability() {
        // setup various combinations of programmes, so that only one returns
        addTestProgramme(true, getCategoriesWithDrama());
        addTestProgramme(false, getCategories(ProgrammeCategory.DRAMA));
        addTestProgramme(true, getCategories(ProgrammeCategory.FILMS));

        // given + run test
        final String json = giveJsonRequest().pathParam(CATEGORY_PARAM, ProgrammeCategory.DRAMA)
                .pathParam(AVAILABILITY_PARAM, true).get(this.findByBoth).body().asString();

        assertProgrammesWithCategory(json, ProgrammeCategory.DRAMA);
        assertProgrammeIsAvailable(json);
    }

    /**
     * tests retrieving a programme
     */
    @Test
    public void testUpdateProgramme() {
        // setup
        final Programme storedProgramme = addTestProgramme();

        // we change our copy of the stored programme
        storedProgramme.setTitle("The title has changed");
        storedProgramme.setDescription("The description has changed");

        // given + run test (no parameter name since we are just referencing the identifier)
        final Programme returnValue = giveJsonRequest().pathParam(ID_PARAM, storedProgramme.getId())
                .body(storedProgramme).put(this.programmeIdUrl).as(Programme.class);

        // then check the return value is equal
        assertEqualsProgrammes(storedProgramme, returnValue);

        // now check that we persisted the update by doing another GET
        assertEqualsProgrammes(storedProgramme, getProgrammeById(storedProgramme.getId()));
    }

    /**
     * tests retrieving a programme
     */
    @Test
    public void testDeleteProgramme() {
        // setup
        final Programme storedProgramme = addTestProgramme();

        // given + run test (no parameter name since we are just referencing the identifier)
        giveJsonRequest().pathParam(ID_PARAM, storedProgramme.getId()).expect().statusCode(HTTP_OK).when()
                .delete(this.programmeIdUrl);

        // check for 404 - i.e page/programme does not exist
        assertProgrammeDoesNotExist(storedProgramme.getId());
    }

    /**
     * Does a GET to ensure we get a 404 back indicating the programme does not exists
     * 
     * @param id the id
     */
    private void assertProgrammeDoesNotExist(final Long id) {
        given().contentType(JSON).pathParam(ID_PARAM, id).expect().statusCode(HTTP_NOT_FOUND).when()
                .get(this.programmeIdUrl);
    }

    /**
     * We check that all programme have the right category
     * 
     * @param json the json representation
     * @param category the category we are checking for every programme
     */
    @SuppressWarnings("rawtypes")
    private void assertProgrammesWithCategory(final String json, final ProgrammeCategory category) {
        final List<List> listOfAllCategories = JsonPath.given(json).getList("programme.category", List.class);

        for (final List categoriesForProgramme : listOfAllCategories) {
            // each category representation may represent multiple categories
            assertTrue(categoriesForProgramme.contains(category.name()));
        }
    }

    /**
     * Asserts that the json fragment identifies all programmes as available
     * 
     * @param json the json presentation
     */
    private void assertProgrammeIsAvailable(final String json) {
        // we follow the brief and check for the is_available field
        final List<Boolean> availabilities = JsonPath.with(json).getList("programme.is_available",
                Boolean.class);
        for (final Boolean b : availabilities) {
            assertTrue(b);
        }
    }

    /**
     * Helper method that adds a test programme of availability true and category drama
     * 
     * @return the test the test programme set with constants defined at the top of the file
     * 
     */
    private Programme addTestProgramme() {
        return addTestProgramme(true, getCategoriesWithDrama());
    }

    /**
     * Helper method that adds a test programme
     * 
     * @param availability with a specified availability
     * @return the test the test programme set with constants defined at the top of the file
     * 
     */
    private Programme addTestProgramme(final boolean availability) {
        return addTestProgramme(availability, getCategoriesWithDrama());
    }

    /**
     * Helper method that adds a test programme getCategoriesWithDrama()
     * 
     * @param categories a number of categories
     * @return the test the test programme set with constants defined at the top of the file
     * 
     */
    private Programme addTestProgramme(final ProgrammeCategory... categories) {
        final Set<ProgrammeCategory> categorySet = new HashSet<ProgrammeCategory>();

        for (final ProgrammeCategory c : categories) {
            categorySet.add(c);
        }

        return addTestProgramme(true, categorySet);
    }

    /**
     * Helper method that adds a test programme
     * 
     * @param availability with a specified availability
     * @param categories a set of categories to put in the programme under test
     * 
     * @return the test the test programme set with constants defined at the top of the file
     * 
     */
    private Programme addTestProgramme(final boolean availability, final Set<ProgrammeCategory> categories) {
        final Programme p = new Programme();
        p.setAvailable(availability);
        p.setCategory(categories);

        // to ensure tests are segregated we ensure titles are different every time
        p.setTitle(String.format("%s-%d", TITLE, testIteration++));
        p.setDescription(DESCRIPTION);

        final Programme storedProgramme = giveJsonRequest().body(p, ObjectMapper.JACKSON).expect()
                .statusCode(HTTP_OK).post(this.url).as(Programme.class);
        return storedProgramme;
    }

    /**
     * Helper method to get a programme by id
     * 
     * @param id the id that identifies the programme
     * @return the programme from the server under test
     */
    private Programme getProgrammeById(final Long id) {
        return given().contentType(JSON).pathParam(ID_PARAM, id).expect().statusCode(HTTP_OK).when()
                .get(this.programmeIdUrl).as(Programme.class);
    }

    /**
     * Sets up a hamcrest given with a json input and output
     * 
     * @return the request specification for a json request (sending and accepting json)
     */
    private RequestSpecification giveJsonRequest() {
        return given().contentType(JSON).header(HTTP_ACCEPT, JSON.getAcceptHeader());
    }

    @Override
    public void afterPropertiesSet() {
        this.url = this.uri.toString();
        this.programmeIdUrl = this.url.concat("{id}");
        this.findByAvailability = this.url.concat("availability/{availability}");
        this.findByCategory = this.url.concat("category/{category}");
        this.findByBoth = this.url.concat("category/{category}/availability/{availability}");
    }
}
