/**
 * Jakarta Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.methodvalidation.model;

import javax.validation.constraints.Past;

/**
 * @author Gunnar Morling
 */
public class Address {

	public Address() {
	}

	public void setName(@Past String name) {
	}

	public Address(@Past String name) {
	}
}
