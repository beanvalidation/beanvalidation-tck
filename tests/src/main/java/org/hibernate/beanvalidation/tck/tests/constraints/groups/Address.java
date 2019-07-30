/**
 * Jakarta Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.constraints.groups;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

/**
 * @author Emmanuel Bernard
 */
@GroupSequence({ Address.class, Address.HighLevelCoherence.class })
@ZipCodeCoherenceChecker(groups = Address.HighLevelCoherence.class)
public class Address {
	@NotNull(groups = Default.class)
	@Size(max = 50, message = "Street names cannot have more than {max} characters.")
	private String street;

	@NotNull(groups = Default.class, message = "Zipcode may not be null")
	@Size(max = 5, message = "Zipcode cannot have more than {max} characters.")
	private String zipcode;

	@NotNull(groups = Default.class)
	@Size(max = 30, message = "City cannot have more than {max} characters.")
	private String city;

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Check coherence on the overall object
	 * Needs basic checking to be green first
	 */
	public interface HighLevelCoherence {
	}

	/**
	 * Check both basic constraints and high level ones.
	 * High level constraints are not checked if basic constraints fail.
	 */
	@GroupSequence(value = { Default.class, HighLevelCoherence.class })
	public interface Complete {
	}
}
