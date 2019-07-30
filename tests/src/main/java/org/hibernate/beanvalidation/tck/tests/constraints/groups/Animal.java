/**
 * Jakarta Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.constraints.groups;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Hardy Ferentschik
 */
public class Animal {
	public enum Domain {
		PROKARYOTA, EUKARYOTA
	}

	@Size(min= 1, groups = { First.class, Second.class })
	private String name;

	@NotNull(groups = First.class)
	private Domain domain;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}
}
