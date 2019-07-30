/**
 * Jakarta Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.validation;

import javax.validation.constraints.NotNull;

/**
 * @author Gunnar Morling
 */
public class Movie {

	@NotNull
	private String title;

	public Movie() {
	}

	public Movie(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
}
