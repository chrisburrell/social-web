package uk.me.burrell.social.api;

import uk.me.burrell.social.api.models.Programme;
import uk.me.burrell.social.api.models.ProgrammeCategory;
import uk.me.burrell.social.api.models.ProgrammeList;

/**
 * <h1>Social and Personalisation REST API</h1> This page describes the actions supported by the service. The
 * service supports all CRUD (Create Read Update, Delete) operations as well as some search services for the
 * 'programme' entity.
 * <p />
 * 
 * <h2>API Definition</h2>
 * In this documentation set it assumed that the application is deployed under /social-war on a standard Java
 * container such as Tomcat or Jetty listening on port 8080 and host localhost.
 * 
 * <h2>Response definitions</h2>
 * All responses are returned in a {@link Programme}. Please refer to {@link Programme} page for further
 * information about the various supported representations.
 * 
 * Please note that all methods returning a response, support the 'format' parameter to override the browser
 * default ACCEPT HTTP header. By default, common browsers such as Chrome add a application/xml header. This
 * means that the default view without any parameter is browser-specific and may yield a XML representation.
 * 
 * e.g. ?format=xml will yield a XML representation.
 * 
 * <h2>Sample URLs</h2>
 * 
 * After having created some programmes, you may some of the sample URLs below:
 * <ul>
 * <li><a href="http://localhost:8080/social-war/social/programme/1">http://localhost:8080/social-war/social/
 * programme/1 (result depends on browser HTTP Accept header)</a></li>
 * <li><a
 * href="http://localhost:8080/social-war/social/programme/1?format=json">http://localhost:8080/social-war
 * /social/programme/1?format=json</a></li>
 * <li><a
 * href="http://localhost:8080/social-war/social/programme/1?format=xml">http://localhost:8080/social-war
 * /social/programme/1?format=xml</a></li>
 * <li><a
 * href="http://localhost:8080/social-war/social/programme/availability/false">http://localhost:8080/social
 * -war/social/programme/availability/false</a></li>
 * <li><a
 * href="http://localhost:8080/social-war/social/programme/availability/false?format=json">http://localhost
 * :8080/social-war/social/programme/availability/false?format=json</a></li>
 * </ul>
 * 
 * @author chrisburrell
 */
public interface SocialRestAPI {
    /**
     * Adds a programme to the data store.
     * 
     * <table>
     * <tr>
     * <th>Operation</th>
     * <th>Sample URL</th>
     * <th>HTTP Method</th>
     * <th>Notes</th>
     * </tr>
     * <tr>
     * <td>Creates a programme</td>
     * <td><a
     * href="http://localhost:8080/social-war/social/programme">http://localhost:8080/social-war/social/
     * programme</a></td>
     * <td>POST</td>
     * <td>The body of the request contains the information, in the format of a {@link Programme}. The id
     * field must not be populated</td>
     * </tr>
     * </table>
     * 
     * @param p the programme to be added. Throws an exception if [id] field is provided.
     * @return the programme that was successfully created
     */
    Programme addProgramme(Programme p);

    /**
     * Finds a programme by id
     * 
     * <table>
     * <tr>
     * <th>Operation</th>
     * <th>Sample URL</th>
     * <th>HTTP Method</th>
     * <th>Notes</th>
     * </tr>
     * <tr>
     * <td>Read a programme from the data store</td>
     * <td><a
     * href="http://localhost:8080/social-war/social/programme/12?format=json">http://localhost:8080/social
     * -war/social /programme/12?format=json</a></td>
     * <td>GET</td>
     * <td>The body of the request is ignored. The response is given in the format of a {@link Programme}.</td>
     * </tr>
     * </table>
     * 
     * @param id the id of the programme
     * @return the Programme that matches the specified input Id
     */
    Programme getProgrammeById(Long id);

    /**
     * Updates the programme identified by parameter 'id' and makes the updates contained in parameter 'p'
     * 
     * <table>
     * <tr>
     * <th>Operation</th>
     * <th>Sample URL</th>
     * <th>HTTP Method</th>
     * <th>Notes</th>
     * </tr>
     * <tr>
     * <td>Updates a programme</td>
     * <td><a
     * href="http://localhost:8080/social-war/social/programme/11">http://localhost:8080/social-war/social
     * /programme/11</a></td>
     * <td>PUT</td>
     * <td>The body of the request contains the information updating information in the format of a
     * {@link Programme} The id field must in the response body is ignored. All information is updated, so the
     * user is required to retrieve the programme first in order to avoid resetting fields to null values</td>
     * </tr>
     * </table>
     * 
     * @param id the id used to locate the relevant programme
     * @param p the programme POJO containing the updates. The id of the received request is ignored in favour
     *            of the path variable. This is to match the REST specification.
     * 
     * @return the updated programme with currently persisted values
     */
    Programme updateProgramme(Long id, Programme p);

    /**
     * Deletes a programme from the data store
     * 
     * <table>
     * <tr>
     * <th>Operation</th>
     * <th>Sample URL</th>
     * <th>HTTP Method</th>
     * <th>Notes</th>
     * </tr>
     * <tr>
     * <td>Deletes a programme</td>
     * <td><a
     * href="http://localhost:8080/social-war/social/programme/11">http://localhost:8080/social-war/social
     * /programme/11</a></td>
     * <td>DELETE</td>
     * <td>Deletes a programme. The request body is ignored.</td>
     * </tr>
     * </table>
     * 
     * @param id the id must be provided and is used to locate the correct programme
     */
    void deleteProgramme(Long id);

    /**
     * Lists all programs that are available (or not) based on the 'available' parameter
     * 
     * 
     * <table>
     * <tr>
     * <th>Operation</th>
     * <th>Sample URL</th>
     * <th>HTTP Method</th>
     * <th>Notes</th>
     * </tr>
     * <tr>
     * <td>Gets all programmes according to the availability parameter</td>
     * <td><a href="http://localhost:8080/social-war/social/programme/availability/true?format=xml">http://
     * localhost:8080/ social-war/social/programme/availability/true?format=xml</a></td>
     * <td>GET</td>
     * <td>Results are returned in a list of {@link Programme}</td>
     * </tr>
     * </table>
     * 
     * @param available true to indicate available programmes should be returned
     * @return a list (potentially empty) of all available programmes
     */
    ProgrammeList getProgrammesByAvailability(boolean available);

    /**
     * Lists all programs that are in a particular category based on the 'category' parameter
     * <table>
     * <tr>
     * <th>Operation</th>
     * <th>Sample URL</th>
     * <th>HTTP Method</th>
     * <th>Notes</th>
     * </tr>
     * <tr>
     * <td>Gets all programmes according to the category parameter</td>
     * <td><a
     * href="http://localhost:8080/social-war/social/programme/category/DRAMA?format=json">http://localhost
     * :8080/social -war/social/programme/category/DRAMA?format=json</a></td>
     * <td>GET</td>
     * <td>Results are returned in a list of {@link Programme}</td>
     * </tr>
     * </table>
     * 
     * @param category the category of choice to filter the programmes returned
     * @return a list (potentially empty) of all available programmes
     */
    ProgrammeList getProgrammesByCategory(ProgrammeCategory category);

    /**
     * Searches for all programmes by availability and category
     * 
     * 
     * <table>
     * <tr>
     * <th>Operation</th>
     * <th>Sample URL</th>
     * <th>HTTP Method</th>
     * <th>Notes</th>
     * </tr>
     * <tr>
     * <td>Gets all programmes according to the category and availability parameter</td>
     * <td><a
     * href="http://localhost:8080/social-war/social/programme/category/DRAMA/availability/true">http://
     * localhost:8080/social-war/social/programme/category/DRAMA/availability/true</a></td>
     * <td>GET</td>
     * <td>Results are returned in a list of {@link Programme}</td>
     * </tr>
     * </table>
     * 
     * @param available available programmes
     * @param category category of the programme
     * @return the list of matching programmes
     */
    ProgrammeList getProgrammesByAvailabilityAndCategory(ProgrammeCategory category, boolean available);

}
