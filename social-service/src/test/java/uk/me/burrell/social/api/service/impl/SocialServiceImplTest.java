package uk.me.burrell.social.api.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.me.burrell.social.api.models.ProgrammeCategory.DRAMA;
import static uk.me.burrell.social.service.utils.ProgrammeTestUtils.assertEqualsProgrammes;
import static uk.me.burrell.social.service.utils.ProgrammeTestUtils.getCategoriesWithDrama;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.SQLCriterion;
import org.hibernate.criterion.SimpleExpression;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.me.burrell.social.api.models.Programme;
import uk.me.burrell.social.api.service.errors.ProgrammeNotFoundException;
import uk.me.burrell.social.api.service.errors.SocialRuntimeException;

/**
 * Tests for {@link SocialServiceImpl}.
 * 
 * For the purpose of time, we do not test the DAO layer with a memory database. If time allowed, we would
 * have at least some basic tests that validate the Hibernate model.
 * <p>
 * Please note however, that we are testing the models with the end to end tests that run as part of the
 * build.
 * <p>
 * Instead we test that the relevant methods are called against the Hibernate framework
 * 
 * @author chrisburrell
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class SocialServiceImplTest {
    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session currentSession;
    private SocialServiceImpl socialUnderTest;

    /**
     * sets up the social service under test
     */
    @Before
    public void setUp() {
        when(this.sessionFactory.getCurrentSession()).thenReturn(this.currentSession);

        this.socialUnderTest = new SocialServiceImpl();
        this.socialUnderTest.setSessionFactory(this.sessionFactory);
    }

    /**
     * tests that add calls hibernate method with programme p
     */
    @Test
    public void testAdd() {
        final Programme p = new Programme();
        p.setAvailable(true);
        p.setCategory(getCategoriesWithDrama());
        p.setDescription("A description");
        p.setTitle("A title");

        final Programme newProgramme = this.socialUnderTest.addProgramme(p);

        verify(this.currentSession).save(p);
        assertEqualsProgrammes(p, newProgramme);
    }

    /**
     * tests that add with an existing pre-populated id fails
     */
    @Test(expected = SocialRuntimeException.class)
    public void testAddWithId() {
        final Programme p = new Programme();
        p.setId(Long.valueOf(1L));

        this.socialUnderTest.addProgramme(p);
    }

    /**
     * tests that add with an existing pre-populated id fails
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNullProgramme() {
        this.socialUnderTest.addProgramme(null);
    }

    /**
     * tests that update calls save
     */
    @Test
    public void testUpdate() {
        final Programme p = new Programme();
        p.setId(Long.valueOf(1L));

        this.socialUnderTest.updateProgramme(p);
        verify(this.currentSession).update(p);
    }

    /**
     * tests that update calls save
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateNullProgramme() {
        this.socialUnderTest.updateProgramme(null);
    }

    /**
     * tests that deletion - we capture the argument to ensure the right id was passed through
     */
    @Test
    public void testDelete() {
        final ArgumentCaptor<Programme> deletedProgramme = ArgumentCaptor.forClass(Programme.class);

        this.socialUnderTest.deleteProgramme(123L);

        verify(this.currentSession).delete(deletedProgramme.capture());
        assertEquals(Long.valueOf(123L), deletedProgramme.getValue().getId());
    }

    /**
     * tests deletion of a programme without id
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteWithoutId() {
        this.socialUnderTest.deleteProgramme(null);
    }

    /**
     * tests we can locate the programme by id
     */
    @Test
    public void testFindById() {
        final long testProgrammeId = 7L;
        final Programme returnedProgramme = new Programme();

        // return a programme if looking for testProgrammeId
        when(this.currentSession.get(Programme.class, Long.valueOf(testProgrammeId))).thenReturn(
                returnedProgramme);

        final Programme newProgramme = this.socialUnderTest.findById(testProgrammeId);

        // check that we are returning the programme we found
        assertEquals(returnedProgramme, newProgramme);
    }

    /**
     * testing getting no results back
     */
    @Test(expected = ProgrammeNotFoundException.class)
    public void testFindProgrammeById() {
        // we don't set up a "when" on the underlying service, so Mockito guarantees us a null return
        this.socialUnderTest.findById(20L);
    }

    /**
     * we check that we running the correct query
     */
    @Test
    public void testFindAvailableRunsCorrectQuery() {
        // when
        final ArgumentCaptor<Criterion> criterion = findByHelper();

        // do
        this.socialUnderTest.findByAvailability(true);

        // then check
        checkFindByCaptor(SimpleExpression.class, "available=true", criterion);
    }

    /**
     * we check that we running the correct query for find by availability
     */
    @Test
    public void testFindCategoryRunsCorrectQuery() {
        // when
        final ArgumentCaptor<Criterion> criterion = findByHelper();

        // do
        this.socialUnderTest.findByCategory(DRAMA);

        // then check
        checkFindByCaptor(SQLCriterion.class, "c.category = ?", criterion);
    }

    /**
     * we check that we running the correct query for find by availability
     */
    @Test
    public void testFindByBothCategoryAndAvailability() {
        // when
        final ArgumentCaptor<Criterion> criterion = findByHelper();

        // do
        this.socialUnderTest.findByCategoryAndAvailability(false, DRAMA);

        // then check
        // we've already checked the toString values, so simply check we have two expressions
        // of the right type
        final List<Criterion> capturedExpressions = criterion.getAllValues();
        final List<Class<? extends Criterion>> classesOfCaptures = new ArrayList<Class<? extends Criterion>>();
        for (final Criterion c : capturedExpressions) {
            classesOfCaptures.add(c.getClass());
        }

        assertEquals(2, classesOfCaptures.size());
        assertTrue(classesOfCaptures.contains(SimpleExpression.class));
        assertTrue(classesOfCaptures.contains(SQLCriterion.class));
    }

    /**
     * given an expected class, a string and the captured argument, we check the criterion is the one we want
     * 
     * @param expectedClass the class of the Criterion we are expecting
     * @param expectedString the string to be found in the toString() method of the criterion
     * @param criterion the criterion that we are making our assertions against
     */
    private void checkFindByCaptor(final Class<? extends Criterion> expectedClass,
            final String expectedString, final ArgumentCaptor<Criterion> criterion) {
        final Criterion capturedCriterion = criterion.getValue();
        assertEquals(expectedClass, capturedCriterion.getClass());
        assertTrue(capturedCriterion.toString().contains(expectedString));
    }

    /**
     * sets up the when clauses to ensure we capture the criteria and can assert against
     * 
     * @return the captor to be tested
     */
    private ArgumentCaptor<Criterion> findByHelper() {
        // when
        final Criteria criteria = mock(Criteria.class);
        when(this.currentSession.createCriteria(Programme.class)).thenReturn(criteria);

        // all tests require at least one criteria
        return expectCriteria(criteria);
    }

    /**
     * We expect a criteria and therefore set up the 'when' clause
     * 
     * @param criteria the criteria object used to build the query by the underlying service
     * @return the captor to assert against
     */
    private ArgumentCaptor<Criterion> expectCriteria(final Criteria criteria) {
        final ArgumentCaptor<Criterion> criterion = ArgumentCaptor.forClass(Criterion.class);
        when(criteria.add(criterion.capture())).thenReturn(criteria);
        return criterion;
    }
}
