/**
 * Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.constraints.constraintcomposition;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

/**
 * @author Gunnar Morling
 */
@Documented
@Constraint(validatedBy = ComposingConstraintSupportingAnnotatedElement.Validator.class)
@Target({ TYPE, METHOD, FIELD })
@Retention(RUNTIME)
public @interface ComposingConstraintSupportingAnnotatedElement {
	String message() default "{constraint.message}";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

	@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
	public class Validator
			implements ConstraintValidator<ComposingConstraintSupportingAnnotatedElement, Object> {

		@Override
		public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
			return true;
		}
	}
}
