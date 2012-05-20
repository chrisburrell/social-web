package uk.me.burrell.social.api.service;

import uk.me.burrell.social.api.models.Programme;
import uk.me.burrell.social.api.models.ProgrammeCategory;
import uk.me.burrell.social.api.models.ProgrammeList;

/**
 * This defines the contract with the rest of the world
 * 
 * @author chrisburrell
 * 
 */
public interface SocialService {

    /**
     * Adds a programme to the data store
     * 
     * @param p the programme to be added. Throws an error if Id is specified.
     * @return the new programme
     */
    Programme addProgramme(Programme p);

    /**
     * updates the programme with the new version provided in parameter 'p'
     * 
     * @param p the updated version of the programme
     * @return the updated programme
     */
    Programme updateProgramme(Programme p);

    /**
     * Deletes a programme from the data store
     * 
     * @param id the id of the programme to be deleted
     * @return true if successfully deleted
     */
    boolean deleteProgramme(Long id);

    /**
     * Finds a programme by id
     * 
     * @param id the id of the programme
     * @return the Programme that matches the specified input Id
     */
    Programme findById(Long id);

    /**
     * Finds all programme with a particular availability
     * 
     * @param availability the availability of the programme
     * @return a list of all available programmes
     */
    ProgrammeList findByAvailability(boolean availability);

    /**
     * Finds all programmes that are in a particular category
     * 
     * @param category the category to be filtered by
     * @return a list of all available programmes
     */
    ProgrammeList findByCategory(ProgrammeCategory category);

    /**
     * A search both by availability and category
     * 
     * @param availability true if we want programmes that are currently available
     * @param category the specific category to filter on
     * @return a list of programmes
     */
    ProgrammeList findByCategoryAndAvailability(boolean availability, ProgrammeCategory category);
}
