package uk.me.burrell.social.api.models;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A simple POJO to represent a Programme instance.
 * 
 * <h2>REST representations</h2> The service offers several REST representations
 * for a Programme. The user is able to switch representations by either providing a 
 * format parameter (e.g. ?format=json) or by setting the content type in the ACCEPT
 * header 
 * 
 * <h3>JSON</h3>
 * 
 * <pre>
 * {
 *		"id": 1,
 *		"title": "Lord Sugar's career",
 *		"description": "An biographical documentary of Lord Sugar's career",
 *		"category": [ "DRAMA"],
 *		"is_available": false
 *		}
 * </pre>
 * 
 * <h3>XML</h3>
 * 
 * <pre>
 * &lt;programme&gt;
 * 		&lt;available&gt;false&lt;/available&gt;
 * 		&lt;category&gt;DRAMA&lt;/category&gt;
 *	  	&lt;description&gt;An biographical documentary of Lord Sugar's career&lt;/description&gt;
 *	  	&lt;id&gt;1&lt;/id&gt;
 *	  	&lt;title&gt;Lord Sugar's career&lt;/title&gt;
 * &lt;/programme&gt;
 * </pre>
 * 
 * 
 * <h2>Categories</h2>
 * Please see {@link ProgrammeCategory} for the supported categories. Note these are provided in as UPPER_CASE values.
 * @author chrisburrell
 * 
 */
@XmlRootElement(name = "programme")
@Entity
@Table(name = "programme")
public class Programme implements Serializable {
	private static final long serialVersionUID = -5119865198605524660L;

	@Id
	@GeneratedValue
	private Long id;
	private String title;
	@Lob
	private String description;

	@ElementCollection(fetch = FetchType.EAGER, targetClass = ProgrammeCategory.class)
	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	private Set<ProgrammeCategory> category;
	@JsonProperty("is_available")
	private boolean available;

	/**
	 * @return the id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final Long id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return the category
	 */
	// @OneToMany(fetch = FetchType.EAGER, mappedBy = "programme_category",
	// targetEntity =
	// ProgrammeCategory.class)
	// @JoinTable(name = "programme_category", joinColumns = { @JoinColumn(name
	// = "programme_category_id") })
	public Set<ProgrammeCategory> getCategory() {
		return this.category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(final Set<ProgrammeCategory> category) {
		this.category = category;
	}

	/**
	 * @return the available
	 */
	public boolean isAvailable() {
		return this.available;
	}

	/**
	 * @param available
	 *            the available to set
	 */
	public void setAvailable(final boolean available) {
		this.available = available;
	}

	@Override
	public String toString() {
		// this is mostly called through the logging framework, and therefore
		// almost never gets evaluated.
		// we will use a StringBuilder because of the better performance and
		// good practice. However, the JVM
		// 1.6 and above often optimize this either way.
		return ToStringBuilder.reflectionToString(this).toString();
	}

}
