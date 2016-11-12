package org.hibernate.beanvalidation.tck.tests.constraints.builtinconstraints;

import static org.hibernate.beanvalidation.tck.util.TestUtil.assertConstraintViolation;
import static org.hibernate.beanvalidation.tck.util.TestUtil.assertCorrectNumberOfViolations;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;

import org.hibernate.beanvalidation.tck.util.TestUtil;
import org.hibernate.beanvalidation.tck.util.shrinkwrap.WebArchiveBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

/**
 * Tests for {@link AssertTrue} and {@link AssertFalse} built-in constraints.
 *
 * @author Hardy Ferentschik
 */
@SpecVersion(spec = "beanvalidation", version = "2.0.0")
public class AssertConstraintsTests {

	@Deployment
	public static WebArchive createTestArchive() {
		return new WebArchiveBuilder()
				.withTestClass( AssertConstraintsTests.class )
				.build();
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "7", id = "a"),
			@SpecAssertion(section = "7", id = "e")
	})
	public void testAssertTrueConstraint() {
		Validator validator = TestUtil.getValidatorUnderTest();
		AssertTrueDummyEntity dummy = new AssertTrueDummyEntity();

		Set<ConstraintViolation<AssertTrueDummyEntity>> constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 1 );
		assertConstraintViolation(
				constraintViolations.iterator().next(), AssertTrueDummyEntity.class, false, "primitiveBoolean"
		);

		dummy.setPrimitiveBoolean( true );
		dummy.setObjectBoolean( Boolean.FALSE );

		constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 1 );
		assertConstraintViolation(
				constraintViolations.iterator().next(), AssertTrueDummyEntity.class, Boolean.FALSE, "objectBoolean"
		);

		dummy.setObjectBoolean( Boolean.TRUE );
		constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 0 );
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "7", id = "a"),
			@SpecAssertion(section = "7", id = "f")
	})
	public void testAssertFalseConstraint() {
		Validator validator = TestUtil.getValidatorUnderTest();
		AssertFalseDummyEntity dummy = new AssertFalseDummyEntity();
		dummy.setPrimitiveBoolean( true );

		Set<ConstraintViolation<AssertFalseDummyEntity>> constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 1 );
		assertConstraintViolation(
				constraintViolations.iterator().next(), AssertFalseDummyEntity.class, true, "primitiveBoolean"
		);

		dummy.setPrimitiveBoolean( false );
		dummy.setObjectBoolean( Boolean.TRUE );

		constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 1 );
		assertConstraintViolation(
				constraintViolations.iterator().next(), AssertFalseDummyEntity.class, Boolean.TRUE, "objectBoolean"
		);

		dummy.setObjectBoolean( Boolean.FALSE );
		constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 0 );
	}

	private static class AssertTrueDummyEntity {
		@AssertTrue
		private boolean primitiveBoolean;

		@AssertTrue
		private Boolean objectBoolean;

		public void setPrimitiveBoolean(boolean primitiveBoolean) {
			this.primitiveBoolean = primitiveBoolean;
		}

		public void setObjectBoolean(Boolean objectBoolean) {
			this.objectBoolean = objectBoolean;
		}
	}

	private static class AssertFalseDummyEntity {
		@AssertFalse
		private boolean primitiveBoolean;

		@AssertFalse
		private Boolean objectBoolean;

		public void setPrimitiveBoolean(boolean primitiveBoolean) {
			this.primitiveBoolean = primitiveBoolean;
		}

		public void setObjectBoolean(Boolean objectBoolean) {
			this.objectBoolean = objectBoolean;
		}
	}

}
