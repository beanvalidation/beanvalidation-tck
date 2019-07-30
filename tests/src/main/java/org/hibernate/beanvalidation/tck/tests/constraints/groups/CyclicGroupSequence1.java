/**
 * Jakarta Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.constraints.groups;

import javax.validation.GroupSequence;

/**
 * @author Hardy Ferentschik
 */
@GroupSequence(value = CyclicGroupSequence2.class)
public interface CyclicGroupSequence1 {
}
