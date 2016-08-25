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
package org.hibernate.beanvalidation.tck.tests.integration.cdi.executable.types;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

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
@SpecVersion(spec = "beanvalidation", version = "2.0.0")
public class ExecutableTypesTest extends Arquillian {

	@Inject
	private CalendarService calendar;

	@Inject
	private Instance<OnlineCalendarService> onlineCalendar;

	@Inject
	private Instance<OfflineCalendarService> offlineCalendar;

	@Inject
	private Instance<AnotherCalendarService> anotherCalendar;

	@Inject
	private Instance<YetAnotherCalendarService> yetAnotherCalendar;

	@Inject
	private DeliveryService deliveryService;

	@Inject
	private Instance<AnotherDeliveryService> anotherDeliveryService;

	@Deployment
	public static WebArchive createTestArchive() {
		return new WebArchiveBuilder()
				.withTestClassPackage( ExecutableTypesTest.class )
				.withEmptyBeansXml()
				.build();
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "i")
	public void testValidationOfConstrainedMethodWithExecutableTypeNONE() {
		Event event = calendar.createEvent( null );
		assertNotNull( event );

		// success; the constraint is invalid, but no violation exception is
		// expected since the executable type is not given in @ValidateOnExecution
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "i")
	public void testValidationOfConstrainedMethodWithEmptyExecutableTypes() {
		Event event = calendar.createEvent( -10 );
		assertNotNull( event );

		// success; the constraint is invalid, but no violation exception is
		// expected since the executable type is not given in @ValidateOnExecution
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "i")
	public void testValidationOfConstrainedMethodWithExecutableTypeNONEAndOther() {
		try {
			calendar.createEvent( (long) -10 );
			fail( "Method invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes(
					e.getConstraintViolations(),
					Min.class
			);
		}
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "j")
	public void testValidationOfConstrainedConstructorParametersWithExecutableTypeCONSTRUCTORS() {
		try {
			onlineCalendar.get();
			fail( "Constructor invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes(
					e.getConstraintViolations(),
					Size.class
			);
		}
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "j")
	public void testValidationOfConstrainedConstructorReturnValueWithExecutableTypeCONSTRUCTORS() {
		try {
			offlineCalendar.get();
			fail( "Constructor invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes(
					e.getConstraintViolations(),
					ValidObject.class
			);
		}
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "j")
	public void testValidationOfConstrainedConstructorWithoutExecutableTypeCONSTRUCTORS() {
		AnotherCalendarService calendar = anotherCalendar.get();
		assertNotNull( calendar );

		// success; the constraint is invalid, but no violation exception is
		// expected since the executable type is not given in @ValidateOnExecution
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "k")
	public void testValidationOfConstrainedMethodParametersWithExecutableTypeNON_GETTER_METHODS() {
		try {
			calendar.createEvent( (short) -10 );
			fail( "Method invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes(
					e.getConstraintViolations(),
					Min.class
			);
		}
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "k")
	public void testValidationOfConstrainedMethodReturnValueWithExecutableTypeNON_GETTER_METHODS() {
		try {
			calendar.createEvent( (byte) -10 );
			fail( "Method invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes(
					e.getConstraintViolations(),
					ValidObject.class
			);
		}
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "k")
	public void testValidationOfConstrainedGetterWithExecutableTypeNON_GETTER_METHODS() {
		Event event = calendar.getEvent();
		assertNotNull( event );

		// success; the constraint is invalid, but no violation exception is
		// expected since the executable type is not given in @ValidateOnExecution
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "l")
	public void testValidationOfConstrainedGetterReturnValueWithExecutableTypeGETTER_METHODS() {
		try {
			calendar.getSpecialEvent();
			fail( "Method invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes(
					e.getConstraintViolations(),
					ValidObject.class
			);
		}
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "l")
	public void testValidationOfConstrainedMethodWithExecutableTypeGETTER_METHODS() {
		Event event = calendar.getSpecialEvent( 0 );
		assertNotNull( event );

		// success; the constraint is invalid, but no violation exception is
		// expected since the executable type is not given in @ValidateOnExecution
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "m")
	public void testValidationOfConstrainedMethodWithExecutableTypeALL() {
		try {
			calendar.createEvent( -10.0 );
			fail( "Method invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes(
					e.getConstraintViolations(),
					Min.class
			);
		}
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "m")
	public void testValidationOfConstrainedGetterWithExecutableTypeALL() {
		try {
			calendar.getVerySpecialEvent();
			fail( "Method invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes(
					e.getConstraintViolations(),
					ValidObject.class
			);
		}
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "m")
	public void testValidationOfConstrainedConstructorWithExecutableTypeALL() {
		try {
			yetAnotherCalendar.get();
			fail( "Constructor invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes(
					e.getConstraintViolations(),
					Size.class
			);
		}
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "m")
	public void testValidationOfConstrainedMethodWithExecutableTypesALLAndNONE() {
		try {
			calendar.createEvent( (float) -10.0 );
			fail( "Method invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes(
					e.getConstraintViolations(),
					Min.class
			);
		}
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "n")
	public void testValidationOfConstrainedMethodWithExecutableTypeIMPLICIT() {
		try {
			deliveryService.findDelivery( null );
			fail( "Method invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes(
					e.getConstraintViolations(),
					NotNull.class
			);
		}
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "n")
	public void testValidationOfConstrainedGetterWithExecutableTypeIMPLICIT() {
		try {
			deliveryService.getDelivery();
			fail( "Getter invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes(
					e.getConstraintViolations(),
					NotNull.class
			);
		}
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "n")
	public void testValidationOfConstrainedGetterWithExecutableTypeIMPLICITOnTypeLevel() {
		Delivery delivery = deliveryService.getAnotherDelivery();
		assertNull( delivery );

		// success; the constraint is invalid, but no violation exception is
		// expected since @ValidateOnExecution(type=IMPLICIT) on the type-level
		// should have no effect and thus the default settings apply
	}

	@Test
	@SpecAssertion(section = "10.1.2", id = "n")
	public void testValidationOfConstrainedConstructorWithExecutableTypeIMPLICIT() {
		try {
			anotherDeliveryService.get();
			fail( "Constructor invocation should have caused a ConstraintViolationException" );
		}
		catch ( ConstraintViolationException e ) {
			assertCorrectConstraintTypes(
					e.getConstraintViolations(),
					Size.class
			);
		}
	}
}
