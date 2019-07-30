/**
 * Jakarta Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.constraints.application;

/**
 * @author Hardy Ferentschik
 */
public class Woman extends Person {
	public Gender getGender() {
		return Gender.FEMALE;
	}
}
