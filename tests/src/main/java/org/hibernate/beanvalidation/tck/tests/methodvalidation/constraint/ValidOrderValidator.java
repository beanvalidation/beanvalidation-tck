/**
 * Jakarta Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.methodvalidation.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.beanvalidation.tck.tests.methodvalidation.model.Order;

/**
 * @author Gunnar Morling
 */
public class ValidOrderValidator
		implements ConstraintValidator<ValidOrder, Order> {

	@Override
	public boolean isValid(Order order, ConstraintValidatorContext context) {
		return order == null ? true : order.getName().length() >= 5;
	}
}
