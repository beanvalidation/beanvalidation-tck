/**
 * Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.xmlconfiguration;

import java.lang.annotation.ElementType;

import javax.validation.Path;
import javax.validation.TraversableResolver;

/**
 * @author Hardy Ferentschik
 */
public class XmlDefinedTraversableResolver implements TraversableResolver {
	public static int numberOfIsReachableCalls = 0;

	public boolean isReachable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
		numberOfIsReachableCalls++;
		return true;
	}

	public boolean isCascadable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
		return true;
	}

	public class NoDefaultConstructorResolver extends XmlDefinedTraversableResolver {
		public NoDefaultConstructorResolver(String foo) {

		}
	}
}
