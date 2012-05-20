package uk.me.burrell.social.api.impl;

import static org.apache.commons.lang.Validate.notNull;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.me.burrell.social.api.SocialRestAPI;
import uk.me.burrell.social.api.models.Programme;
import uk.me.burrell.social.api.models.ProgrammeCategory;
import uk.me.burrell.social.api.models.ProgrammeList;
import uk.me.burrell.social.api.service.SocialService;
import uk.me.burrell.social.api.service.errors.ProgrammeNotFoundException;
import uk.me.burrell.social.api.service.errors.SocialRuntimeException;

/**
 * Default controller, using the Dispatcher model, whereby all requests are routed through here onto the
 * relevant portions of the module
 * 
 * @author chrisburrell
 */
@Controller
@RequestMapping("/programme")
public class SocialRestAPIImpl implements SocialRestAPI {
    private static final Logger LOG = LoggerFactory.getLogger(SocialRestAPIImpl.class);

    @Resource(name = "socialService")
    private SocialService service;

    @RequestMapping(method = RequestMethod.POST)
    @Override
    public Programme addProgramme(@RequestBody final Programme p) {
        LOG.debug("Received request to add programme [{}]", p);

        notNull(p, "A programme must be provided");
        return this.service.addProgramme(p);
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Programme updateProgramme(@PathVariable("id") final Long id, @RequestBody final Programme p) {
        LOG.debug("Received request to update programme with id=[{}] and values=[{}]", id, p);

        // if the request has been addressed, then we use the id
        notNull(id, "Unable to update programme. Please provide an id to locate the relevant programme.");
        notNull(p, "Programme information must be provided");

        p.setId(id);

        return this.service.updateProgramme(p);
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteProgramme(@PathVariable("id") final Long id) {
        LOG.debug("Received request to delete programme with id [{}]", id);

        notNull(id, "Unable to delete programme. Please provide an id to locate the relevant programme.");
        this.service.deleteProgramme(id);
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    // @ResponseBody
    public Programme getProgrammeById(@PathVariable final Long id) {
        LOG.debug("Retrieving all programmes by id [{}]", id);

        notNull(id, "Unable to retrieve programme. Please provide an id to locate the relevant programme.");
        return this.service.findById(id);

    }

    @Override
    @RequestMapping(value = "/category/{category}", method = RequestMethod.GET)
    public ProgrammeList getProgrammesByCategory(@PathVariable final ProgrammeCategory category) {
        LOG.debug("Retrieving all programmes by category [{}]", category);
        notNull(category, "The category provided was null");
        return this.service.findByCategory(category);
    }

    @RequestMapping(value = "/availability/{availability}", method = RequestMethod.GET)
    @Override
    public ProgrammeList getProgrammesByAvailability(@PathVariable final boolean availability) {
        LOG.debug("Retrieving all programmes by availability [{}]", availability);

        return this.service.findByAvailability(availability);
    }

    @RequestMapping(value = "/category/{category}/availability/{availability}", method = RequestMethod.GET)
    @Override
    public ProgrammeList getProgrammesByAvailabilityAndCategory(
            @PathVariable final ProgrammeCategory category, @PathVariable final boolean availability) {
        LOG.debug("Retrieving all programmes by availability [{}] and category [{}]", availability, category);

        notNull(category, "You must specify a non-null category");

        return this.service.findByCategoryAndAvailability(availability, category);
    }

    /**
     * Add some error handling for request not found
     * 
     * @param ex the exception that is caught
     */
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "The programme was not found")
    @ExceptionHandler(ProgrammeNotFoundException.class)
    public void handleException(final ProgrammeNotFoundException ex) {
        // Log for debug purposes
        LOG.debug(ex.getMessage(), ex);
    }

    /**
     * All {@link SocialRuntimeException} will be returned as HTTP 400 BAD_REQUEST
     * 
     * @param ex the exception that is caught
     */
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SocialRuntimeException.class)
    public void handleException(final SocialRuntimeException ex) {
        LOG.error(ex.getMessage(), ex);
    }

    /**
     * Handle illegal arguments as HTTP 400 BAD_REQUEST
     * 
     * @param ex the exception that is caught
     * @param response the response to be written to set the write code
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public void handleException(final IllegalArgumentException ex, final HttpServletResponse response) {
        LOG.debug(ex.getMessage(), ex);
        try {
            response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        } catch (final IOException e) {
            LOG.error("Unable to send response to client");
        }
    }

    /**
     * Handle all remaining exceptions as Internal Server error (HTTP 500)
     * 
     * @param ex the exception that is caught
     */
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public void handleException(final Exception ex) {
        LOG.error(ex.getMessage(), ex);
    }

    /**
     * @param service the service to set
     */
    public void setService(final SocialService service) {
        this.service = service;
    }
}
