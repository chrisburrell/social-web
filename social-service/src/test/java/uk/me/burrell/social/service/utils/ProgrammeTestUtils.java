package uk.me.burrell.social.service.utils;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import uk.me.burrell.social.api.models.Programme;
import uk.me.burrell.social.api.models.ProgrammeCategory;

/**
 * A utility class
 * 
 * @author chrisburrell
 * 
 */
public final class ProgrammeTestUtils {

    /**
     * Prevent instantiation
     */
    private ProgrammeTestUtils() {
        // no-op
    }

    /**
     * Test helper to create a set with the DRAMA Category in it
     * 
     * @return the Drama category in a set
     */
    public static Set<ProgrammeCategory> getCategoriesWithDrama() {
        return getCategories(ProgrammeCategory.DRAMA);
    }

    /**
     * Test helper to create a set with the DRAMA Category in it
     * 
     * @param categories the set of categories to put in a set
     * @return the Drama category in a set
     */
    public static Set<ProgrammeCategory> getCategories(final ProgrammeCategory... categories) {
        final Set<ProgrammeCategory> categorySet = new HashSet<ProgrammeCategory>();
        for (final ProgrammeCategory c : categories) {
            categorySet.add(c);
        }

        return categorySet;
    }

    /**
     * Takes 2 programmes and compares each property in turn.
     * 
     * @param storedProgramme the stored programme
     * @param returnValue the return value
     */
    public static void assertEqualsProgrammes(final Programme storedProgramme, final Programme returnValue) {
        assertEquals(storedProgramme.getId(), returnValue.getId());
        assertEquals(storedProgramme.getCategory(), returnValue.getCategory());
        assertEquals(storedProgramme.getDescription(), returnValue.getDescription());
        assertEquals(storedProgramme.getTitle(), returnValue.getTitle());
    }
}
