package uk.me.burrell.social.api.service.impl;

import static org.apache.commons.lang.Validate.notNull;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.me.burrell.social.api.models.Programme;
import uk.me.burrell.social.api.models.ProgrammeCategory;
import uk.me.burrell.social.api.models.ProgrammeList;
import uk.me.burrell.social.api.service.SocialService;
import uk.me.burrell.social.api.service.errors.ProgrammeNotFoundException;
import uk.me.burrell.social.api.service.errors.SocialRuntimeException;

/**
 * Concrete implementation of the backend service
 * 
 * @author chrisburrell
 * 
 */
@Service
@Transactional
public class SocialServiceImpl implements SocialService {
    /* package */static final String AVAILABLE_PARAM = "available";
    /* package */static final String CATEGORY_PARAM = "category";
    private static final Logger LOGGER = LoggerFactory.getLogger(SocialServiceImpl.class);

    /*
     * Non-javadoc note on the underlying implementation The underlying implementation is currently
     * implemented as a work around see {@link https://hibernate.onjira.com/browse/HHH-7211} and { @link
     * https://hibernate.onjira.com/browse/HHH-869 }for more information
     */
    private static final String IN_CATEGORY_FRAGMENT = "{alias}.id in "
            + "(select p.id from Programme p, Programme_category c "
            + "where p.id = c.Programme_id and c.category = ?)";

    @Resource(name = "sessionFactory")
    private SessionFactory sessionFactory;

    @Override
    public Programme addProgramme(final Programme p) {
        notNull(p);

        if (p.getId() != null) {
            throw new SocialRuntimeException("Id should not be provided in the Programme input data");
        }

        LOGGER.debug("Adding programme [{}]", p);
        this.sessionFactory.getCurrentSession().save(p);
        LOGGER.trace("Added programme [{}]", p);

        return p;
    }

    @Override
    public Programme updateProgramme(final Programme p) {
        notNull(p);
        notNull(p.getId());

        LOGGER.debug("Updating programme title=[{}]", p.getTitle());
        this.sessionFactory.getCurrentSession().update(p);
        LOGGER.trace("Updated programme title=[{}] with id=[{}]", p.getTitle(), p.getId());

        return p;
    }

    @Override
    public boolean deleteProgramme(final Long id) {
        notNull(id);
        LOGGER.debug("Deleting programme with id  [{}]", id);

        final Programme p = new Programme();
        p.setId(id);

        this.sessionFactory.getCurrentSession().delete(p);
        LOGGER.debug("Deletion was succesful");
        return true;
    }

    @Override
    public Programme findById(final Long id) {
        notNull(id);
        LOGGER.debug("Finding programme with id=[{}]", id);
        final Programme programme = (Programme) this.sessionFactory.getCurrentSession().get(Programme.class,
                id);

        if (programme == null) {
            throw new ProgrammeNotFoundException();
        }

        return programme;
    }

    /*
     * This is a non-javadoc comment, for implementation clarity we suppress warnings here because Hibernate
     * does not provide us with generics
     */
    @SuppressWarnings(value = "unchecked")
    @Override
    public ProgrammeList findByAvailability(final boolean availability) {
        LOGGER.debug("Finding all programmes that have availibility = [{}]", availability);
        return new ProgrammeList(addAvailabilityRestriction(getFilteredQuery(), availability).list());
    }

    /*
     * This is a non-javadoc comment, for implementation clarity we suppress warnings here because Hibernate
     * does not provide us with generics
     */
    @SuppressWarnings(value = "unchecked")
    @Override
    public ProgrammeList findByCategory(final ProgrammeCategory category) {
        return new ProgrammeList(addCategoryRestriction(getFilteredQuery(), category).list());
    }

    /*
     * This is a non-javadoc comment, for implementation clarity we suppress warnings here because Hibernate
     * does not provide us with generics
     */
    @SuppressWarnings(value = "unchecked")
    @Override
    public ProgrammeList findByCategoryAndAvailability(final boolean availability,
            final ProgrammeCategory category) {
        LOGGER.debug("Finding all programmes that have availibility = [{}]", availability);
        return new ProgrammeList(addCategoryRestriction(
                addAvailabilityRestriction(getFilteredQuery(), availability), category).list());
    }

    /**
     * Adds a restriction to filter on categories
     * 
     * @param filteredQuery the query
     * @param available true to indicate we want all programmes that are available, false for the opposite
     * @return a query that can be executed against the current sessions
     */
    private Criteria addAvailabilityRestriction(final Criteria filteredQuery, final boolean available) {
        return filteredQuery.add(Restrictions.eq(AVAILABLE_PARAM, available));
    }

    /**
     * Adds a restriction to filter on categories.
     * 
     * @param filteredQuery the query
     * @param category the category to be filtered upon
     * @return a query that can be executed against the current sessions
     */
    private Criteria addCategoryRestriction(final Criteria filteredQuery, final ProgrammeCategory category) {
        return filteredQuery.add(Restrictions.sqlRestriction(IN_CATEGORY_FRAGMENT, category.ordinal(),
                IntegerType.INSTANCE));
    }

    /**
     * constructs a criteria based on the current hibernate session.
     * 
     * @return the criteria for retrieving Programmes
     */
    private Criteria getFilteredQuery() {
        final Session session = this.sessionFactory.getCurrentSession();
        return session.createCriteria(Programme.class);

    }

    /**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
