/**
 * Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.constraints.builtinconstraints;

import static org.hibernate.beanvalidation.tck.util.TestUtil.assertConstraintViolation;
import static org.hibernate.beanvalidation.tck.util.TestUtil.assertCorrectNumberOfViolations;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.Pattern;

import org.hibernate.beanvalidation.tck.beanvalidation.Sections;
import org.hibernate.beanvalidation.tck.util.TestUtil;
import org.hibernate.beanvalidation.tck.util.shrinkwrap.WebArchiveBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

/**
 * Tests for {@link Pattern} built-in constraint.
 *
 * @author Hardy Ferentschik
 */
@SpecVersion(spec = "beanvalidation", version = "2.0.0")
public class PatternConstraintTest {

	@Deployment
	public static WebArchive createTestArchive() {
		return new WebArchiveBuilder()
				.withTestClass( PatternConstraintTest.class )
				.build();
	}

	@Test
	@SpecAssertion(section = Sections.BUILTINCONSTRAINTS, id = "a")
	@SpecAssertion(section = Sections.BUILTINCONSTRAINTS, id = "q")
	public void testPatternConstraint() {
		Validator validator = TestUtil.getValidatorUnderTest();
		PatternDummyEntity dummy = new PatternDummyEntity();

		Set<ConstraintViolation<PatternDummyEntity>> constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 0 );

		dummy.pattern = "ab cd";
		constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 1 );
		assertConstraintViolation(
				constraintViolations.iterator().next(), PatternDummyEntity.class, "ab cd", "pattern"
		);

		dummy.pattern = "wc 00";
		constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 0 );
	}

	private static class PatternDummyEntity {
		@Pattern(regexp = "[a-z][a-z] \\d\\d")
		private String pattern;
	}

}
