/**
 * Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.methodvalidation;

import static org.hibernate.beanvalidation.tck.util.ConstraintViolationAssert.assertCorrectConstraintTypes;
import static org.hibernate.beanvalidation.tck.util.ConstraintViolationAssert.assertNumberOfViolations;
import static org.hibernate.beanvalidation.tck.util.ConstraintViolationAssert.assertThat;
import static org.hibernate.beanvalidation.tck.util.ConstraintViolationAssert.pathWith;
import static org.hibernate.beanvalidation.tck.util.ConstraintViolationAssert.violationOf;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.beanvalidation.tck.beanvalidation.Sections;
import org.hibernate.beanvalidation.tck.tests.AbstractTCKTest;
import org.hibernate.beanvalidation.tck.tests.methodvalidation.constraint.MyCrossParameterConstraint;
import org.hibernate.beanvalidation.tck.tests.methodvalidation.model.Address;
import org.hibernate.beanvalidation.tck.tests.methodvalidation.model.ContainerElementsOrder;
import org.hibernate.beanvalidation.tck.tests.methodvalidation.model.Item;
import org.hibernate.beanvalidation.tck.tests.methodvalidation.model.OrderLine;
import org.hibernate.beanvalidation.tck.tests.methodvalidation.model.ProductCategory;
import org.hibernate.beanvalidation.tck.tests.methodvalidation.model.User;
import org.hibernate.beanvalidation.tck.tests.methodvalidation.model.User.Basic;
import org.hibernate.beanvalidation.tck.tests.methodvalidation.model.User.Extended;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

/**
 * @author Gunnar Morling
 * @author Guillaume Smet
 */
@SpecVersion(spec = "beanvalidation", version = "2.0.0")
public class ValidateConstructorParametersTest extends AbstractTCKTest {

	@Deployment
	public static WebArchive createTestArchive() {
		return webArchiveBuilder()
				.withTestClass( ValidateConstructorParametersTest.class )
				.withPackage( MyCrossParameterConstraint.class.getPackage() )
				.withClass( Address.class )
				.withClass( Item.class )
				.withClass( OrderLine.class )
				.withClass( User.class )
				.withClass( ContainerElementsOrder.class )
				.withClass( ProductCategory.class )
				.build();
	}

	@Test
	@SpecAssertion(section = Sections.VALIDATIONAPI_VALIDATORAPI_METHODLEVELVALIDATIONMETHODS, id = "g")
	@SpecAssertion(section = Sections.VALIDATIONAPI_VALIDATORAPI_METHODLEVELVALIDATIONMETHODS, id = "h")
	@SpecAssertion(section = Sections.VALIDATIONAPI_CONSTRAINTVIOLATION, id = "d")
	@SpecAssertion(section = Sections.VALIDATIONAPI_CONSTRAINTVIOLATION, id = "e")
	@SpecAssertion(section = Sections.VALIDATIONAPI_CONSTRAINTVIOLATION, id = "f")
	@SpecAssertion(section = Sections.VALIDATIONAPI_CONSTRAINTVIOLATION, id = "g")
	@SpecAssertion(section = Sections.VALIDATIONAPI_CONSTRAINTVIOLATION, id = "h")
	@SpecAssertion(section = Sections.VALIDATIONAPI_CONSTRAINTVIOLATION, id = "i")
	public void testOneViolation() throws Exception {
		Constructor<User> constructor = User.class.getConstructor( String.class );
		String arg0 = "B";
		Object[] parameterValues = new Object[] { arg0 };

		Set<ConstraintViolation<User>> violations = getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertNumberOfViolations( violations, 1 );

		assertCorrectConstraintTypes( violations, Size.class );
		assertThat( violations ).containsOnlyPaths(
				pathWith()
						.constructor( User.class )
						.parameter( "firstName", 0 )
		);

		ConstraintViolation<User> violation = violations.iterator().next();
		assertNull( violation.getRootBean() );
		assertEquals( violation.getRootBeanClass(), User.class );
		assertNull( violation.getLeafBean() );
		assertEquals( violation.getInvalidValue(), arg0 );
		assertEquals( violation.getExecutableParameters(), parameterValues );
		assertNull( violation.getExecutableReturnValue() );
	}

	@Test
	@SpecAssertion(section = Sections.VALIDATIONAPI_VALIDATORAPI_METHODLEVELVALIDATIONMETHODS, id = "h")
	@SpecAssertion(section = Sections.VALIDATIONAPI_CONSTRAINTVIOLATION, id = "f")
	@SpecAssertion(section = Sections.VALIDATIONAPI_CONSTRAINTVIOLATION, id = "j")
	public void testOneViolationFromCrossParameterConstraint() throws Exception {
		Constructor<User> constructor = User.class.getConstructor( String.class, String.class );
		Object[] parameterValues = new Object[] { null, null };

		Set<ConstraintViolation<User>> violations = getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertNumberOfViolations( violations, 1 );

		assertCorrectConstraintTypes( violations, MyCrossParameterConstraint.class );
		assertThat( violations ).containsOnlyPaths(
				pathWith()
						.constructor( User.class )
						.crossParameter()
		);

		ConstraintViolation<User> violation = violations.iterator().next();

		assertEquals( violation.getInvalidValue(), parameterValues );
		assertNull( violation.getLeafBean() );
	}

	@Test
	@SpecAssertion(section = Sections.VALIDATIONAPI_VALIDATORAPI_METHODLEVELVALIDATIONMETHODS, id = "h")
	public void testTwoViolations() throws Exception {
		Constructor<User> constructor = User.class.getConstructor(
				String.class,
				CharSequence.class
		);
		Object[] parameterValues = new Object[] { null, "S" };

		Set<ConstraintViolation<User>> violations = getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertNumberOfViolations( violations, 2 );

		assertCorrectConstraintTypes( violations, NotNull.class, Size.class );
		assertThat( violations ).containsOnlyPaths(
				pathWith()
						.constructor( User.class )
						.parameter( "firstName", 0 ),
				pathWith()
						.constructor( User.class )
						.parameter( "lastName", 1 )
		);
	}

	@Test
	@SpecAssertion(section = Sections.VALIDATIONAPI_VALIDATORAPI_METHODLEVELVALIDATIONMETHODS, id = "h")
	public void testTwoViolationsOnSameParameter() throws Exception {
		Constructor<User> constructor = User.class.getConstructor( String.class, int.class );
		Object[] parameterValues = new Object[] { "S", 0 };

		Set<ConstraintViolation<User>> violations = getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertNumberOfViolations( violations, 2 );

		assertCorrectConstraintTypes( violations, Pattern.class, Size.class );
		assertThat( violations ).containsOnlyPaths(
				pathWith()
						.constructor( User.class )
						.parameter( "firstName", 0 ),
				pathWith()
						.constructor( User.class )
						.parameter( "firstName", 0 )
		);
	}

	@Test
	@SpecAssertion(section = Sections.VALIDATIONAPI_VALIDATORAPI_METHODLEVELVALIDATIONMETHODS, id = "h")
	public void testTwoConstraintsOfSameType() throws Exception {
		Constructor<User> constructor = User.class.getConstructor( CharSequence.class );
		Object[] parameterValues = new Object[] { "S" };

		Set<ConstraintViolation<User>> violations = getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertNumberOfViolations( violations, 2 );

		assertCorrectConstraintTypes( violations, Size.class, Size.class );
		assertThat( violations ).containsOnlyPaths(
				pathWith()
						.constructor( User.class )
						.parameter( "lastName", 0 ),
				pathWith()
						.constructor( User.class )
						.parameter( "lastName", 0 )
		);
	}

	@Test
	@SpecAssertion(section = Sections.VALIDATIONAPI_VALIDATORAPI_METHODLEVELVALIDATIONMETHODS, id = "h")
	public void testCrossParameterConstraintGivenSeveralTimes() throws Exception {
		Constructor<User> constructor = User.class.getConstructor(
				String.class,
				String.class,
				String.class
		);
		Object[] parameterValues = new Object[] { null, null, null };

		Set<ConstraintViolation<User>> violations = getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertNumberOfViolations( violations, 2 );

		assertCorrectConstraintTypes(
				violations,
				MyCrossParameterConstraint.class,
				MyCrossParameterConstraint.class
		);
		assertThat( violations ).containsOnlyPaths(
				pathWith()
						.constructor( User.class )
						.crossParameter(),
				pathWith()
						.constructor( User.class )
						.crossParameter()
		);
	}

	@Test
	@SpecAssertion(section = Sections.VALIDATIONAPI_VALIDATORAPI_METHODLEVELVALIDATIONMETHODS, id = "h")
	public void testNoViolations() throws Exception {
		Constructor<User> constructor = User.class.getConstructor(
				String.class,
				CharSequence.class
		);
		Object[] parameterValues = new Object[] { "Bob", "Smith" };

		Set<ConstraintViolation<User>> violations = getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertNumberOfViolations( violations, 0 );
	}

	@Test
	@SpecAssertion(section = Sections.VALIDATIONAPI_VALIDATORAPI_METHODLEVELVALIDATIONMETHODS, id = "h")
	public void testValidationWithGroup() throws Exception {
		Constructor<User> constructor = User.class.getConstructor( String.class, long.class );
		Object[] parameterValues = new Object[] { "S", 0l };

		Set<ConstraintViolation<User>> violations = getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertNumberOfViolations( violations, 0 );

		violations = getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues,
				Extended.class
		);

		assertCorrectConstraintTypes( violations, Size.class );
		assertThat( violations ).containsOnlyPaths(
				pathWith()
						.constructor( User.class )
						.parameter( "lastName", 0 )
		);
	}

	@Test
	@SpecAssertion(section = Sections.VALIDATIONAPI_VALIDATORAPI_METHODLEVELVALIDATIONMETHODS, id = "h")
	public void testCrossParameterConstraintValidationWithGroup() throws Exception {
		Constructor<User> constructor = User.class.getConstructor(
				CharSequence.class,
				String.class
		);
		Object[] parameterValues = new Object[] { null, null };

		Set<ConstraintViolation<User>> violations = getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertNumberOfViolations( violations, 0 );

		violations = getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues,
				Extended.class
		);

		assertCorrectConstraintTypes( violations, MyCrossParameterConstraint.class );
		assertThat( violations ).containsOnlyPaths(
				pathWith()
						.constructor( User.class )
						.crossParameter()
		);
	}

	@Test
	@SpecAssertion(section = Sections.VALIDATIONAPI_VALIDATORAPI_METHODLEVELVALIDATIONMETHODS, id = "h")
	public void testValidationWithSeveralGroups() throws Exception {
		Constructor<User> constructor = User.class.getConstructor(
				String.class,
				String.class,
				Date.class
		);
		Object[] parameterValues = new Object[] { null, "S", null };

		Set<ConstraintViolation<User>> violations = getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertNumberOfViolations( violations, 0 );

		violations = getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues,
				Basic.class,
				Extended.class
		);

		assertCorrectConstraintTypes( violations, NotNull.class, Size.class, NotNull.class );
		assertThat( violations ).containsOnlyPaths(
				pathWith()
						.constructor( User.class )
						.parameter( "firstName", 0 ),
				pathWith()
						.constructor( User.class )
						.parameter( "lastName", 1 ),
				pathWith()
						.constructor( User.class )
						.parameter( "dateOfBirth", 2 )
		);
	}

	@Test(expectedExceptions = ValidationException.class)
	@SpecAssertion(section = Sections.VALIDATIONAPI_VALIDATORAPI_METHODLEVELVALIDATIONMETHODS, id = "g")
	public void testUnexpectedType() throws Exception {
		Constructor<Address> constructor = Address.class.getConstructor( String.class );
		Object[] parameterValues = new Object[] { "S" };

		getExecutableValidator().validateConstructorParameters( constructor, parameterValues );
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	@SpecAssertion(section = Sections.VALIDATIONAPI_VALIDATORAPI_METHODLEVELVALIDATIONMETHODS, id = "i")
	public void testNullPassedForConstructorCausesException() throws Exception {
		Constructor<User> constructor = null;
		Object[] parameterValues = new Object[] { null };

		getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues
		);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	@SpecAssertion(section = Sections.VALIDATIONAPI_VALIDATORAPI_METHODLEVELVALIDATIONMETHODS, id = "i")
	public void testNullPassedForParameterValuesCausesException() throws Exception {
		Constructor<User> constructor = User.class.getConstructor( String.class );
		Object[] parameterValues = null;

		getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues
		);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	@SpecAssertion(section = Sections.VALIDATIONAPI_VALIDATORAPI_METHODLEVELVALIDATIONMETHODS, id = "i")
	public void testNullPassedForGroupsCausesException() throws Exception {
		Constructor<User> constructor = User.class.getConstructor( String.class );
		Object[] parameterValues = new Object[] { null };

		getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues,
				(Class<?>[]) null
		);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	@SpecAssertion(section = Sections.VALIDATIONAPI_VALIDATORAPI_METHODLEVELVALIDATIONMETHODS, id = "i")
	public void testNullPassedAsSingleGroupCausesException() throws Exception {
		Constructor<User> constructor = User.class.getConstructor( String.class );
		Object[] parameterValues = new Object[] { null };

		getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues,
				(Class<?>) null
		);
	}

	@Test
	@SpecAssertion(section = Sections.VALIDATIONAPI_CONSTRAINTVIOLATION, id = "f")
	@SpecAssertion(section = Sections.VALIDATIONAPI_CONSTRAINTVIOLATION, id = "g")
	@SpecAssertion(section = Sections.VALIDATIONAPI_CONSTRAINTVIOLATION, id = "h")
	@SpecAssertion(section = Sections.VALIDATIONAPI_CONSTRAINTVIOLATION, id = "i")
	public void testOneViolationForCascadedValidation() throws Exception {
		Item leaf = new Item( "foo" );
		Constructor<OrderLine> constructor = OrderLine.class.getConstructor( Item.class );
		Object[] parameterValues = new Object[] { leaf };

		Set<ConstraintViolation<OrderLine>> violations = getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertNumberOfViolations( violations, 1 );

		ConstraintViolation<OrderLine> violation = violations.iterator().next();

		assertEquals( violation.getLeafBean(), leaf );
		assertEquals( violation.getInvalidValue(), "foo" );
		assertEquals( violation.getExecutableParameters(), parameterValues );
		assertNull( violation.getExecutableReturnValue() );
	}

	@Test
	@SpecAssertion(section = Sections.VALIDATIONAPI_CONSTRAINTVIOLATION, id = "f")
	public void testContainerElementLeafBean() throws NoSuchMethodException, SecurityException {
		Constructor<ContainerElementsOrder> constructor = ContainerElementsOrder.class.getConstructor( String.class, Map.class );

		Item invalidItem = new Item( "s" );

		Map<ProductCategory, List<OrderLine>> invalidLines = new HashMap<>();
		invalidLines.put( null, Arrays.asList( new OrderLine( new Item( "item name" ) ) ) );
		invalidLines.put( ProductCategory.MUSIC, Arrays.asList( new OrderLine( invalidItem ) ) );

		Object[] parameterValues = new Object[] { "name", invalidLines };

		Set<ConstraintViolation<ContainerElementsOrder>> violations = getExecutableValidator().validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertThat( violations ).containsOnlyViolations(
				violationOf( NotNull.class ).withLeafBean( null ),
				violationOf( Size.class ).withLeafBean( invalidItem )
		);
	}
}
