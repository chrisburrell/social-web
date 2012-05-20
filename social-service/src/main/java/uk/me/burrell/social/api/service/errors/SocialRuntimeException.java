package uk.me.burrell.social.api.service.errors;

/**
 * We use runtime exceptions mostly to prevent having try/catch everywhere in the code base
 * 
 * @author chrisburrell
 * 
 */
public class SocialRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1608576292648833234L;

    /**
     * @param message a default message for the exception
     */
    public SocialRuntimeException(final String message) {
        super(message);
    }
}
