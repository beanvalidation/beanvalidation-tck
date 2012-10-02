/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat, Inc. and/or its affiliates, and individual contributors
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
package org.hibernate.beanvalidation.tck.tests.validation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Payload;
import javax.validation.Valid;
import javax.validation.Validator;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

import org.hibernate.beanvalidation.tck.util.TestUtil;
import org.hibernate.beanvalidation.tck.util.shrinkwrap.WebArchiveBuilder;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.hibernate.beanvalidation.tck.util.TestUtil.assertCorrectNumberOfViolations;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests for the implementation of <code>Validator</code>.
 *
 * @author Hardy Ferentschik
 */
@SpecVersion(spec = "beanvalidation", version = "1.1.0")
public class PropertyPathTest extends Arquillian {

	@Deployment
	public static WebArchive createTestArchive() {
		return new WebArchiveBuilder()
				.withTestClass( PropertyPathTest.class )
				.withClasses(
						Actor.class,
						ActorArrayBased.class,
						ActorListBased.class,
						PlayedWith.class,
						Person.class,
						VerySpecialClass.class,
						Customer.class,
						Engine.class,
						Order.class
				)
				.build();
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "4.2", id = "f"),
			@SpecAssertion(section = "4.2", id = "g"),
			@SpecAssertion(section = "4.2", id = "m")
	})
	public void testPropertyPathWithConstraintViolationForRootObject() {
		Validator validator = TestUtil.getValidatorUnderTest();
		Set<ConstraintViolation<VerySpecialClass>> constraintViolations = validator.validate( new VerySpecialClass() );
		assertCorrectNumberOfViolations( constraintViolations, 1 );
		ConstraintViolation<VerySpecialClass> constraintViolation = constraintViolations.iterator().next();

		Iterator<Path.Node> nodeIter = constraintViolation.getPropertyPath().iterator();
		assertTrue( nodeIter.hasNext() );
		Path.Node node = nodeIter.next();
		assertEquals( node.getName(), null );
		assertFalse( node.isInIterable() );
		assertFalse( nodeIter.hasNext() );
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "4.2", id = "f"),
			@SpecAssertion(section = "4.2", id = "h"),
			@SpecAssertion(section = "4.2", id = "l")
	})
	public void testPropertyPathTraversedObject() {
		Validator validator = TestUtil.getValidatorUnderTest();

		Engine engine = new Engine();
		engine.setSerialNumber( "ABCDEFGH1234" );
		Set<ConstraintViolation<Engine>> constraintViolations = validator.validate( engine );
		assertCorrectNumberOfViolations( constraintViolations, 1 );

		ConstraintViolation<Engine> constraintViolation = constraintViolations.iterator().next();

		Iterator<Path.Node> nodeIter = constraintViolation.getPropertyPath().iterator();
		assertTrue( nodeIter.hasNext() );
		Path.Node node = nodeIter.next();
		assertEquals( node.getName(), "serialNumber" );
		assertFalse( node.isInIterable() );
		assertFalse( nodeIter.hasNext() );
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "4.2", id = "f"),
			@SpecAssertion(section = "4.2", id = "i"),
			@SpecAssertion(section = "4.2", id = "k")
	})
	public void testPropertyPathWithList() {
		Validator validator = TestUtil.getValidatorUnderTest();

		Actor clint = new ActorListBased( "Clint", "Eastwood" );
		Actor morgan = new ActorListBased( "Morgan", null );
		Actor charlie = new ActorListBased( "Charlie", "Sheen" );

		clint.addPlayedWith( charlie );
		charlie.addPlayedWith( clint );
		charlie.addPlayedWith( morgan );
		morgan.addPlayedWith( charlie );

		Set<ConstraintViolation<Actor>> constraintViolations = validator.validate( clint );
		checkActorViolations( constraintViolations );
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "4.2", id = "f"),
			@SpecAssertion(section = "4.2", id = "i"),
			@SpecAssertion(section = "4.2", id = "k")
	})
	public void testPropertyPathWithArray() {
		Validator validator = TestUtil.getValidatorUnderTest();

		Actor clint = new ActorArrayBased( "Clint", "Eastwood" );
		Actor morgan = new ActorArrayBased( "Morgan", null );
		Actor charlie = new ActorArrayBased( "Charlie", "Sheen" );

		clint.addPlayedWith( charlie );
		charlie.addPlayedWith( clint );
		charlie.addPlayedWith( morgan );
		morgan.addPlayedWith( charlie );

		Set<ConstraintViolation<Actor>> constraintViolations = validator.validate( clint );
		checkActorViolations( constraintViolations );
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "4.2", id = "f"),
			@SpecAssertion(section = "4.2", id = "j"),
			@SpecAssertion(section = "4.2", id = "k")
	})
	public void testPropertyPathWithMap() {
		Validator validator = TestUtil.getValidatorUnderTest();

		ActorDB db = new ActorDB();
		Actor morgan = new ActorArrayBased( "Morgan", null );
		Integer id = db.addActor( morgan );

		Set<ConstraintViolation<ActorDB>> constraintViolations = validator.validate( db );
		assertCorrectNumberOfViolations( constraintViolations, 1 );

		ConstraintViolation<ActorDB> constraintViolation = constraintViolations.iterator().next();
		Iterator<Path.Node> nodeIter = constraintViolation.getPropertyPath().iterator();
		assertTrue( nodeIter.hasNext() );
		Path.Node node = nodeIter.next();
		assertEquals( node.getName(), "actors" );
		assertFalse( node.isInIterable() );

		node = nodeIter.next();
		assertEquals( node.getName(), "lastName" );
		assertTrue( node.isInIterable() );
		assertEquals( node.getKey(), id );

		assertFalse( nodeIter.hasNext() );
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "4.2", id = "f"),
			@SpecAssertion(section = "4.2", id = "k")
	})
	public void testPropertyPathSet() {
		Validator validator = TestUtil.getValidatorUnderTest();

		Customer customer = new Customer();
		customer.setFirstName( "John" );
		customer.setLastName( "Doe" );
		Order order = new Order();
		customer.addOrder( order );

		Set<ConstraintViolation<Customer>> constraintViolations = validator.validate( customer );
		assertCorrectNumberOfViolations( constraintViolations, 1 );

		ConstraintViolation<Customer> constraintViolation = constraintViolations.iterator().next();
		Iterator<Path.Node> nodeIter = constraintViolation.getPropertyPath().iterator();
		assertTrue( nodeIter.hasNext() );
		Path.Node node = nodeIter.next();
		assertEquals( node.getName(), "orders" );
		assertFalse( node.isInIterable() );

		node = nodeIter.next();
		assertEquals( node.getName(), "orderNumber" );
		assertTrue( node.isInIterable() );

		assertFalse( nodeIter.hasNext() );
	}

	private void checkActorViolations(Set<ConstraintViolation<Actor>> constraintViolations) {
		assertCorrectNumberOfViolations( constraintViolations, 1 );

		ConstraintViolation<Actor> constraintViolation = constraintViolations.iterator().next();

		Iterator<Path.Node> nodeIter = constraintViolation.getPropertyPath().iterator();
		assertTrue( nodeIter.hasNext() );
		Path.Node node = nodeIter.next();
		assertEquals( node.getName(), "playedWith" );
		assertFalse( node.isInIterable() );

		node = nodeIter.next();
		assertEquals( node.getName(), "playedWith" );
		assertTrue( node.isInIterable() );
		assertEquals( node.getIndex(), new Integer( 0 ) );

		node = nodeIter.next();
		assertEquals( node.getName(), "lastName" );
		assertTrue( node.isInIterable() );
		assertEquals( node.getIndex(), new Integer( 1 ) );

		assertFalse( nodeIter.hasNext() );
	}

	@Special()
	class VerySpecialClass {
	}

	@Constraint(validatedBy = { SpecialValidator.class })
	@Target({ TYPE })
	@Retention(RUNTIME)
	public @interface Special {
		public abstract String message() default "special validation failed";

		public abstract Class<?>[] groups() default { };

		public abstract Class<? extends Payload>[] payload() default { };
	}

	public static class SpecialValidator implements ConstraintValidator<Special, VerySpecialClass> {
		public void initialize(Special constraintAnnotation) {
		}

		public boolean isValid(VerySpecialClass clazz, ConstraintValidatorContext constraintValidatorContext) {
			return false;
		}
	}

	class ActorDB {
		private int idGen = 0;

		@Valid
		Map<Integer, Actor> actors = new HashMap<Integer, Actor>();

		public Integer addActor(Actor actor) {
			Integer id = idGen++;
			actors.put( id, actor );
			return id;
		}
	}
}
