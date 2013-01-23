/*
* JBoss, Home of Professional Open Source
* Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.hibernate.beanvalidation.tck.tests.methodvalidation;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.MethodValidator;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.metadata.ElementDescriptor.Kind;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.hibernate.beanvalidation.tck.tests.methodvalidation.User.Basic;
import org.hibernate.beanvalidation.tck.tests.methodvalidation.User.Extended;
import org.hibernate.beanvalidation.tck.tests.methodvalidation.constraint.MyCrossParameterConstraint;
import org.hibernate.beanvalidation.tck.util.TestUtil;
import org.hibernate.beanvalidation.tck.util.shrinkwrap.WebArchiveBuilder;

import static org.hibernate.beanvalidation.tck.util.TestUtil.assertCorrectConstraintTypes;
import static org.hibernate.beanvalidation.tck.util.TestUtil.assertCorrectNumberOfViolations;
import static org.hibernate.beanvalidation.tck.util.TestUtil.assertCorrectPathDescriptorKinds;
import static org.hibernate.beanvalidation.tck.util.TestUtil.assertCorrectPathNodeNames;
import static org.hibernate.beanvalidation.tck.util.TestUtil.kinds;
import static org.hibernate.beanvalidation.tck.util.TestUtil.names;
import static org.testng.Assert.assertEquals;

/**
 * @author Gunnar Morling
 */
@SpecVersion(spec = "beanvalidation", version = "1.1.0")
public class ValidateConstructorParametersTest extends Arquillian {

	private MethodValidator executableValidator;

	@Deployment
	public static WebArchive createTestArchive() {
		return new WebArchiveBuilder()
				.withTestClass( ValidateConstructorParametersTest.class )
				.withPackage( MyCrossParameterConstraint.class.getPackage() )
				.withClass( Address.class )
				.withClass( User.class )
				.build();
	}

	@BeforeMethod
	public void setupValidator() {
		executableValidator = TestUtil.getValidatorUnderTest().forMethods();
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "5.1.2", id = "e"),
			@SpecAssertion(section = "5.1.2", id = "f")
	})
	public void testOneViolation() throws Exception {
		Constructor<User> constructor = User.class.getConstructor( String.class );
		Object[] parameterValues = new Object[] { null };

		Set<ConstraintViolation<User>> violations = executableValidator.validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertCorrectNumberOfViolations( violations, 1 );

		assertCorrectConstraintTypes( violations, NotNull.class );
		assertCorrectPathNodeNames( violations, names( "User", "arg0" ) );
		assertCorrectPathDescriptorKinds( violations, kinds( Kind.CONSTRUCTOR, Kind.PARAMETER ) );
	}

	@Test
	@SpecAssertion(section = "5.1.2", id = "f")
	public void testOneViolationFromCrossParameterConstraint() throws Exception {
		Constructor<User> constructor = User.class.getConstructor( String.class, String.class );
		Object[] parameterValues = new Object[] { null, null };

		Set<ConstraintViolation<User>> violations = executableValidator.validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertCorrectNumberOfViolations( violations, 1 );

		assertCorrectConstraintTypes( violations, MyCrossParameterConstraint.class );
		assertCorrectPathNodeNames( violations, names( "User" ) );
		assertCorrectPathDescriptorKinds( violations, kinds( Kind.CONSTRUCTOR ) );

		assertEquals( violations.iterator().next().getInvalidValue(), parameterValues );
	}

	@Test
	@SpecAssertion(section = "5.1.2", id = "f")
	public void testTwoViolations() throws Exception {
		Constructor<User> constructor = User.class.getConstructor(
				String.class,
				CharSequence.class
		);
		Object[] parameterValues = new Object[] { null, "S" };

		Set<ConstraintViolation<User>> violations = executableValidator.validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertCorrectNumberOfViolations( violations, 2 );

		assertCorrectConstraintTypes( violations, NotNull.class, Size.class );
		assertCorrectPathNodeNames(
				violations,
				names( "User", "arg0" ),
				names( "User", "arg1" )
		);
		assertCorrectPathDescriptorKinds(
				violations,
				kinds( Kind.CONSTRUCTOR, Kind.PARAMETER ),
				kinds( Kind.CONSTRUCTOR, Kind.PARAMETER )
		);
	}

	@Test
	@SpecAssertion(section = "5.1.2", id = "f")
	public void testTwoViolationsOnSameParameter() throws Exception {
		Constructor<User> constructor = User.class.getConstructor( String.class, int.class );
		Object[] parameterValues = new Object[] { "S" };

		Set<ConstraintViolation<User>> violations = executableValidator.validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertCorrectNumberOfViolations( violations, 2 );

		assertCorrectConstraintTypes( violations, Pattern.class, Size.class );
		assertCorrectPathNodeNames(
				violations,
				names( "User", "arg0" ),
				names( "User", "arg0" )
		);
		assertCorrectPathDescriptorKinds(
				violations,
				kinds( Kind.CONSTRUCTOR, Kind.PARAMETER ),
				kinds( Kind.CONSTRUCTOR, Kind.PARAMETER )
		);
	}

	@Test
	@SpecAssertion(section = "5.1.2", id = "f")
	public void testTwoConstraintsOfSameType() throws Exception {
		Constructor<User> constructor = User.class.getConstructor( CharSequence.class );
		Object[] parameterValues = new Object[] { "S" };

		Set<ConstraintViolation<User>> violations = executableValidator.validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertCorrectNumberOfViolations( violations, 2 );

		assertCorrectConstraintTypes( violations, Size.class, Size.class );
		assertCorrectPathNodeNames(
				violations,
				names( "User", "arg0" ),
				names( "User", "arg0" )
		);
		assertCorrectPathDescriptorKinds(
				violations,
				kinds( Kind.CONSTRUCTOR, Kind.PARAMETER ),
				kinds( Kind.CONSTRUCTOR, Kind.PARAMETER )
		);
	}

	@Test
	@SpecAssertion(section = "5.1.2", id = "f")
	public void testCrossParameterConstraintGivenSeveralTimes() throws Exception {
		Constructor<User> constructor = User.class.getConstructor(
				String.class,
				String.class,
				String.class
		);
		Object[] parameterValues = new Object[] { null, null, null };

		Set<ConstraintViolation<User>> violations = executableValidator.validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertCorrectNumberOfViolations( violations, 2 );

		assertCorrectConstraintTypes(
				violations,
				MyCrossParameterConstraint.class,
				MyCrossParameterConstraint.class
		);
		assertCorrectPathNodeNames( violations, names( "User" ), names( "User" ) );
		assertCorrectPathDescriptorKinds(
				violations,
				kinds( Kind.CONSTRUCTOR ),
				kinds( Kind.CONSTRUCTOR )
		);
	}

	@Test
	@SpecAssertion(section = "5.1.2", id = "f")
	public void testNoViolations() throws Exception {
		Constructor<User> constructor = User.class.getConstructor(
				String.class,
				CharSequence.class
		);
		Object[] parameterValues = new Object[] { "Bob", "Smith" };

		Set<ConstraintViolation<User>> violations = executableValidator.validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertCorrectNumberOfViolations( violations, 0 );
	}

	@Test
	@SpecAssertion(section = "5.1.2", id = "f")
	public void testValidationWithGroup() throws Exception {
		Constructor<User> constructor = User.class.getConstructor( String.class, long.class );
		Object[] parameterValues = new Object[] { "S" };

		Set<ConstraintViolation<User>> violations = executableValidator.validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertCorrectNumberOfViolations( violations, 0 );

		violations = executableValidator.validateConstructorParameters(
				constructor,
				parameterValues,
				Extended.class
		);

		assertCorrectConstraintTypes( violations, Size.class );
		assertCorrectPathNodeNames( violations, names( "User", "arg0" ) );
		assertCorrectPathDescriptorKinds( violations, kinds( Kind.CONSTRUCTOR, Kind.PARAMETER ) );
	}

	@Test
	@SpecAssertion(section = "5.1.2", id = "f")
	public void testCrossParameterConstraintValidationWithGroup() throws Exception {
		Constructor<User> constructor = User.class.getConstructor(
				CharSequence.class,
				String.class
		);
		Object[] parameterValues = new Object[] { null, null };

		Set<ConstraintViolation<User>> violations = executableValidator.validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertCorrectNumberOfViolations( violations, 0 );

		violations = executableValidator.validateConstructorParameters(
				constructor,
				parameterValues,
				Extended.class
		);

		assertCorrectConstraintTypes( violations, MyCrossParameterConstraint.class );
		assertCorrectPathNodeNames( violations, names( "User" ) );
		assertCorrectPathDescriptorKinds( violations, kinds( Kind.CONSTRUCTOR ) );
	}

	@Test
	@SpecAssertion(section = "5.1.2", id = "f")
	public void testValidationWithSeveralGroups() throws Exception {
		Constructor<User> constructor = User.class.getConstructor(
				String.class,
				String.class,
				Date.class
		);
		Object[] parameterValues = new Object[] { null, "S", null };

		Set<ConstraintViolation<User>> violations = executableValidator.validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertCorrectNumberOfViolations( violations, 0 );

		violations = executableValidator.validateConstructorParameters(
				constructor,
				parameterValues,
				Basic.class,
				Extended.class
		);

		assertCorrectConstraintTypes( violations, NotNull.class, Size.class, NotNull.class );
		assertCorrectPathNodeNames(
				violations,
				names( "User", "arg0" ),
				names( "User", "arg1" ),
				names( "User", "arg2" )
		);
		assertCorrectPathDescriptorKinds(
				violations,
				kinds( Kind.CONSTRUCTOR, Kind.PARAMETER ),
				kinds( Kind.CONSTRUCTOR, Kind.PARAMETER ),
				kinds( Kind.CONSTRUCTOR, Kind.PARAMETER )
		);
	}

	@Test(expectedExceptions = ValidationException.class)
	@SpecAssertion(section = "5.1.2", id = "e")
	public void testUnexpectedType() throws Exception {
		Constructor<Address> constructor = Address.class.getConstructor( String.class );
		Object[] parameterValues = new Object[] { "S" };

		executableValidator.validateConstructorParameters( constructor, parameterValues );
	}

	@Test
	@SpecAssertion(section = "5.2", id = "e")
	public void testGetInvalidValueForCrossParameterConstraint() throws Exception {
		Constructor<User> constructor = User.class.getConstructor( String.class, String.class );
		Object[] parameterValues = new Object[] { "Bob", "Alice" };

		Set<ConstraintViolation<User>> violations = executableValidator.validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertCorrectNumberOfViolations( violations, 1 );
		assertEquals( violations.iterator().next().getInvalidValue(), parameterValues );
	}

	@Test
	@SpecAssertion(section = "5.2", id = "e")
	public void testGetInvalidValueForCrossParameterConstraintOnParameterlessMethod()
			throws Exception {
		Constructor<User> constructor = User.class.getConstructor();
		Object[] parameterValues = new Object[] { };

		Set<ConstraintViolation<User>> violations = executableValidator.validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertCorrectNumberOfViolations( violations, 1 );
		assertEquals( violations.iterator().next().getInvalidValue(), parameterValues );
	}
}
