package uk.me.burrell.social.api.service.errors;


/**
 * Indicates that the programme was not found
 * 
 * @author chrisburrell
 * 
 */
public class ProgrammeNotFoundException extends SocialRuntimeException {
    private static final long serialVersionUID = 6939924026898144419L;

    /**
     * An exception meaning a programme lookup was not found
     */
    public ProgrammeNotFoundException() {
        super("The requested programme was not found");
    }
}
