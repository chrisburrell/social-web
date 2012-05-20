package uk.me.burrell.social.api.models;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Provided for serialisation only.
 * 
 *  * <h2>REST representations</h2> The service offers several REST representations
 * for a Programme. The user is able to switch representations by either providing a 
 * format parameter (e.g. ?format=json) or by setting the content type in the ACCEPT
 * header 
 * 
 * <h3>JSON</h3>
 * 
 * <pre>
 *  * {
 * 	"programme": [{
 * 		"id": 1,
 * 		"title": "The test show",
 * 		"description": "Some test drama",
 * 		"category": ["DRAMA"],
 * 		"is_available": false
 * 	},
 * 	{
 * 		"id": 2,
 * 		"title": "The test show",
 * 		"description": "Some test drama",
 * 		"category": ["DRAMA"],
 * 		"is_available": false
 * 	}]
 * }
 * </pre>
 * 
 * <h3>XML</h3>
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;
 * &lt;programmes&gt;
 * 	&lt;programme&gt;
 * 		&lt;available&gt;false&lt;/available&gt;
 * 		&lt;category&gt;DRAMA&lt;/category&gt;
 * 		&lt;description&gt;Some test drama&lt;/description&gt;
 * 		&lt;id&gt;1&lt;/id&gt;
 * 		&lt;title&gt;The test show&lt;/title&gt;
 * 	&lt;/programme&gt;
 * 	&lt;programme&gt;
 * 		&lt;available&gt;false&lt;/available&gt;
 * 		&lt;category&gt;DRAMA&lt;/category&gt;
 * 		&lt;description&gt;Some test drama&lt;/description&gt;
 * 		&lt;id&gt;2&lt;/id&gt;
 * 		&lt;title&gt;The test show&lt;/title&gt;
 * 	&lt;/programme&gt;
 * &lt;/programmes&gt;
 * </pre>
 * @author chrisburrell
 *
 */
@XmlRootElement(name="programmes")
public class ProgrammeList {
	
	private List<Programme> programmes;

	public ProgrammeList() {
		//provided for serialisation
	}
	
	public ProgrammeList(List<Programme> programmes) {
		this.programmes = programmes;
	}

	/**
	 * @return the programme
	 */
	public List<Programme> getProgramme() {
		return programmes;
	}

	/**
	 * @param programme the programme to set
	 */
	public void setProgramme(List<Programme> programmes) {
		this.programmes = programmes;
	}

	/**
	 * Adds a programme to the list
	 * @param p the programme to be added
	 */
	public void add(Programme p) {
		this.programmes.add(p);
	}
}
