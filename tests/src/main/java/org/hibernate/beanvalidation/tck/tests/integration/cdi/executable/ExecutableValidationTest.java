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
package org.hibernate.beanvalidation.tck.tests.integration.cdi.executable;

import java.util.Calendar;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

import org.hibernate.beanvalidation.tck.util.Groups;
import org.hibernate.beanvalidation.tck.util.IntegrationTest;
import org.hibernate.beanvalidation.tck.util.shrinkwrap.WebArchiveBuilder;

import static org.hibernate.beanvalidation.tck.util.TestUtil.assertCorrectConstraintTypes;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

/**
 * @author Gunnar Morling
 */
@IntegrationTest
@SpecVersion(spec = "beanvalidation", version = "1.1.0")
public class ExecutableValidationTest extends Arquillian {

	@Inject
	private CalendarService calendar;

	@Inject
	private AnnotatedCalendarService annotatedCalendar;

	@Inject
	private ClassLevelAnnotatedCalendarService classsLevelAnnotatedCalendar;

	@Inject
	private Instance<UserService> userServiceInstance;

	@Inject
	private Instance<PersonService> personServiceInstance;

	@Inject
	private OrderService orderService;

	@Deployment
	public static WebArchive createTestArchive() {
		return new WebArchiveBuilder()
				.withTestClassPackage( ExecutableValidationTest.class )
				.withEmptyBeansXml()
				.build();
	}

	@Test(groups = Groups.FAILING_ON_AS)
	@SpecAssertions({
			@SpecAssertion(section = "10.1.2", id = "a"),
			@SpecAssertion(section = "10.1.2", id = "b"),
			@SpecAssertion(section = "10.1.2", id = "c"),
			@SpecAssertion(section = "10.3", id = "a")
	})
	public void testParameterValidationOfConstrainedMethod() {
		try {
			Calendar endDate = Calendar.getInstance();
			endDate.set( 1980, 1, 1 );
			calendar.createEvent( null, endDate.getTime() );
			fail( "Method invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes(
					e.getConstraintViolations(),
					NotNull.class,
					Future.class,
					CrossParameterConstraint.class
			);
		}
	}

	@Test(groups = Groups.FAILING_ON_AS)
	@SpecAssertions({
			@SpecAssertion(section = "10.1.2", id = "a"),
			@SpecAssertion(section = "10.1.2", id = "b"),
			@SpecAssertion(section = "10.1.2", id = "c"),
			@SpecAssertion(section = "10.3", id = "a")
	})
	public void testReturnValueValidationOfConstrainedMethod() {
		try {
			calendar.createEvent();
			fail( "Method invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes( e.getConstraintViolations(), NotNull.class );
		}
	}

	@Test(groups = Groups.FAILING_ON_AS)
	@SpecAssertions({
			@SpecAssertion(section = "10.1.2", id = "a"),
			@SpecAssertion(section = "10.1.2", id = "b"),
			@SpecAssertion(section = "10.1.2", id = "c"),
			@SpecAssertion(section = "10.3", id = "a")
	})
	public void testGettersAreNotValidatedByDefault() {
		Event event = calendar.getEvent();
		assertNull( event, "The event should be null, since getters are not validated by default." );
	}

	@Test(groups = Groups.FAILING_ON_AS)
	@SpecAssertions({
			@SpecAssertion(section = "10.1.2", id = "a"),
			@SpecAssertion(section = "10.1.2", id = "b"),
			@SpecAssertion(section = "10.1.2", id = "c"),
			@SpecAssertion(section = "10.3", id = "a")
	})
	public void testParameterValidationOfConstrainedConstructor() {
		try {
			userServiceInstance.get();
			fail( "Constructor invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes( e.getConstraintViolations(), Size.class, CrossParameterConstraint.class );
		}
	}

	@Test(groups = Groups.FAILING_ON_AS)
	@SpecAssertions({
			@SpecAssertion(section = "10.1.2", id = "a"),
			@SpecAssertion(section = "10.1.2", id = "b"),
			@SpecAssertion(section = "10.1.2", id = "c"),
			@SpecAssertion(section = "10.3", id = "a")
	})
	public void testReturnValueValidationOfConstrainedConstructor() {
		try {
			personServiceInstance.get();
			fail( "Constructor invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes( e.getConstraintViolations(), ValidPersonService.class );
		}
	}

	@Test(groups = Groups.FAILING_ON_AS)
	@SpecAssertion(section = "10.1.2", id = "e")
	public void testValidationOfConstrainedMethodAnnotatedWithValidateOnExecutionContainingExecutableType() {
		try {
			annotatedCalendar.createEvent( null );
			fail( "Method invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes( e.getConstraintViolations(), NotNull.class );
		}
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "e")
	public void testValidationOfConstrainedMethodAnnotatedWithValidateOnExecutionNotContainingExecutableType() {
		Event event = annotatedCalendar.createEvent( -10 );
		assertNotNull( event );

		// success; the constraint is invalid, but no violation exception is
		// expected since the executable type is not given in @ValidateOnExecution
	}

	@Test(groups = Groups.FAILING_ON_AS)
	@SpecAssertion(section = "10.1.2", id = "f")
	public void testValidationOfConstrainedMethodOnClassAnnotatedWithValidateOnExecutionContainingExecutableType() {
		try {
			classsLevelAnnotatedCalendar.getEvent();
			fail( "Method invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes( e.getConstraintViolations(), NotNull.class );
		}
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "f")
	public void testValidationOfConstrainedMethodOnClassAnnotatedWithValidateOnExecutionNotContainingExecutableType() {
		Event event = classsLevelAnnotatedCalendar.createEvent( null );
		assertNotNull( event );

		// success; the constraint is invalid, but no violation exception is
		// expected since the executable type is not given in @ValidateOnExecution
	}

	//TODO Fails due to HV-755
	@Test(groups = Groups.FAILING_IN_RI)
	@SpecAssertion(section = "10.1.2", id = "f")
	public void testValidationOfConstrainedMethodOnInterfaceAnnotatedWithValidateOnExecutionContainingExecutableType() {
		try {
			orderService.getOrder();
			fail( "Method invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes( e.getConstraintViolations(), NotNull.class );
		}
	}

	//TODO Fails due to HV-755
	@Test(groups = Groups.FAILING_IN_RI)
	@SpecAssertion(section = "10.1.2", id = "f")
	public void testValidationOfConstrainedMethodOnInterfaceAnnotatedWithValidateOnExecutionNotContainingExecutableType() {
		Order order = orderService.placeOrder( null );
		assertNotNull( order );

		// success; the constraint is invalid, but no violation exception is
		// expected since the executable type is not given in @ValidateOnExecution
	}
}
