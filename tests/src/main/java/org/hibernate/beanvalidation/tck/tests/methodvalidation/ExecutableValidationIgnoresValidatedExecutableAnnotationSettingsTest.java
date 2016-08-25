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
import java.lang.reflect.Method;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.constraints.NotNull;
import javax.validation.executable.ExecutableValidator;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.hibernate.beanvalidation.tck.tests.methodvalidation.constraint.ValidLineItem;
import org.hibernate.beanvalidation.tck.tests.methodvalidation.constraint.ValidWarehouseItem;
import org.hibernate.beanvalidation.tck.tests.methodvalidation.model.LineItem;
import org.hibernate.beanvalidation.tck.tests.methodvalidation.model.WarehouseItem;
import org.hibernate.beanvalidation.tck.util.TestUtil;
import org.hibernate.beanvalidation.tck.util.shrinkwrap.WebArchiveBuilder;

import static org.hibernate.beanvalidation.tck.util.TestUtil.assertCorrectConstraintTypes;
import static org.hibernate.beanvalidation.tck.util.TestUtil.assertCorrectPathNodeKinds;
import static org.hibernate.beanvalidation.tck.util.TestUtil.assertCorrectPathNodeNames;
import static org.hibernate.beanvalidation.tck.util.TestUtil.kinds;
import static org.hibernate.beanvalidation.tck.util.TestUtil.names;

/**
 * @author Gunnar Morling
 */
@SpecVersion(spec = "beanvalidation", version = "2.0.0")
public class ExecutableValidationIgnoresValidatedExecutableAnnotationSettingsTest extends Arquillian {

	private ExecutableValidator executableValidator;

	@Deployment
	public static WebArchive createTestArchive() {
		return new WebArchiveBuilder()
				.withTestClass( ExecutableValidationIgnoresValidatedExecutableAnnotationSettingsTest.class )
				.withClass( LineItem.class )
				.withClass( WarehouseItem.class )
				.withClass( ValidLineItem.class )
				.withClass( ValidWarehouseItem.class )
				.build();
	}

	@BeforeMethod
	public void setupValidator() {
		executableValidator = TestUtil.getValidatorUnderTest().forExecutables();
	}

	@Test
	@SpecAssertion(section = "5.1.2", id = "m")
	public void testValidateParametersYieldsConstraintViolationIfValidateExecutableIsSetToNONEOnTypeLevel()
			throws Exception {
		Object object = new LineItem( null );
		String methodName = "setName";
		Method method = LineItem.class.getMethod( methodName, String.class );
		Object[] parameterValues = new Object[] { null };

		Set<ConstraintViolation<Object>> violations = executableValidator.validateParameters(
				object,
				method,
				parameterValues
		);

		assertCorrectConstraintTypes( violations, NotNull.class );
		assertCorrectPathNodeNames( violations, names( methodName, "arg0" ) );
		assertCorrectPathNodeKinds( violations, kinds( ElementKind.METHOD, ElementKind.PARAMETER ) );
	}

	@Test
	@SpecAssertion(section = "5.1.2", id = "m")
	public void testValidateConstructorParametersYieldsConstraintViolationIfValidateExecutableIsSetToNONEOnTypeLevel()
			throws Exception {
		Constructor<LineItem> constructor = LineItem.class.getConstructor( String.class );
		Object[] parameterValues = new Object[] { null };

		Set<ConstraintViolation<LineItem>> violations = executableValidator.validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertCorrectConstraintTypes( violations, NotNull.class );
		assertCorrectPathNodeNames( violations, names( LineItem.class.getSimpleName(), "arg0" ) );
		assertCorrectPathNodeKinds( violations, kinds( ElementKind.CONSTRUCTOR, ElementKind.PARAMETER ) );
	}

	@Test
	@SpecAssertion(section = "5.1.2", id = "m")
	public void testValidateReturnValueYieldsConstraintViolationIfValidateExecutableIsSetToNONEOnTypeLevel()
			throws Exception {
		Object object = new LineItem( null );
		String methodName = "setName";
		Method method = LineItem.class.getMethod( methodName, String.class );
		Object returnValue = null;

		Set<ConstraintViolation<Object>> violations = executableValidator.validateReturnValue(
				object,
				method,
				returnValue
		);

		assertCorrectConstraintTypes( violations, NotNull.class );
		assertCorrectPathNodeNames( violations, names( methodName, TestUtil.RETURN_VALUE_NODE_NAME ) );
		assertCorrectPathNodeKinds( violations, kinds( ElementKind.METHOD, ElementKind.RETURN_VALUE ) );
	}

	@Test
	@SpecAssertion(section = "5.1.2", id = "m")
	public void testValidateConstructorReturnValueYieldsConstraintViolationIfValidateExecutableIsSetToNONEOnTypeLevel()
			throws Exception {
		Constructor<LineItem> constructor = LineItem.class.getConstructor( String.class );
		LineItem createdObject = new LineItem( null );

		Set<ConstraintViolation<LineItem>> violations = executableValidator.validateConstructorReturnValue(
				constructor,
				createdObject
		);

		assertCorrectConstraintTypes( violations, ValidLineItem.class );
		assertCorrectPathNodeNames( violations, names( LineItem.class.getSimpleName(), TestUtil.RETURN_VALUE_NODE_NAME ) );
		assertCorrectPathNodeKinds( violations, kinds( ElementKind.CONSTRUCTOR, ElementKind.RETURN_VALUE ) );
	}

	@Test
	@SpecAssertion(section = "5.1.2", id = "m")
	public void testValidateParametersYieldsConstraintViolationIfValidateExecutableIsSetToNONEOnExecutableLevel()
			throws Exception {
		Object object = new WarehouseItem( null );
		String methodName = "setName";
		Method method = WarehouseItem.class.getMethod( methodName, String.class );
		Object[] parameterValues = new Object[] { null };

		Set<ConstraintViolation<Object>> violations = executableValidator.validateParameters(
				object,
				method,
				parameterValues
		);

		assertCorrectConstraintTypes( violations, NotNull.class );
		assertCorrectPathNodeNames( violations, names( methodName, "arg0" ) );
		assertCorrectPathNodeKinds( violations, kinds( ElementKind.METHOD, ElementKind.PARAMETER ) );
	}

	@Test
	@SpecAssertion(section = "5.1.2", id = "m")
	public void testValidateConstructorParametersYieldsConstraintViolationIfValidateExecutableIsSetToNONEOnExecutableLevel()
			throws Exception {
		Constructor<WarehouseItem> constructor = WarehouseItem.class.getConstructor( String.class );
		Object[] parameterValues = new Object[] { null };

		Set<ConstraintViolation<WarehouseItem>> violations = executableValidator.validateConstructorParameters(
				constructor,
				parameterValues
		);

		assertCorrectConstraintTypes( violations, NotNull.class );
		assertCorrectPathNodeNames( violations, names( WarehouseItem.class.getSimpleName(), "arg0" ) );
		assertCorrectPathNodeKinds( violations, kinds( ElementKind.CONSTRUCTOR, ElementKind.PARAMETER ) );
	}

	@Test
	@SpecAssertion(section = "5.1.2", id = "m")
	public void testValidateReturnValueYieldsConstraintViolationIfValidateExecutableIsSetToNONEOnExecutableLevel()
			throws Exception {
		Object object = new WarehouseItem( null );
		String methodName = "setName";
		Method method = WarehouseItem.class.getMethod( methodName, String.class );
		Object returnValue = null;

		Set<ConstraintViolation<Object>> violations = executableValidator.validateReturnValue(
				object,
				method,
				returnValue
		);

		assertCorrectConstraintTypes( violations, NotNull.class );
		assertCorrectPathNodeNames( violations, names( methodName, TestUtil.RETURN_VALUE_NODE_NAME ) );
		assertCorrectPathNodeKinds( violations, kinds( ElementKind.METHOD, ElementKind.RETURN_VALUE ) );
	}

	@Test
	@SpecAssertion(section = "5.1.2", id = "m")
	public void testValidateConstructorReturnValueYieldsConstraintViolationIfValidateExecutableIsSetToNONEOnExecutableLevel()
			throws Exception {
		Constructor<WarehouseItem> constructor = WarehouseItem.class.getConstructor( String.class );
		WarehouseItem createdObject = new WarehouseItem( null );

		Set<ConstraintViolation<WarehouseItem>> violations = executableValidator.validateConstructorReturnValue(
				constructor,
				createdObject
		);

		assertCorrectConstraintTypes( violations, ValidWarehouseItem.class );
		assertCorrectPathNodeNames( violations, names( WarehouseItem.class.getSimpleName(), TestUtil.RETURN_VALUE_NODE_NAME ) );
		assertCorrectPathNodeKinds( violations, kinds( ElementKind.CONSTRUCTOR, ElementKind.RETURN_VALUE ) );
	}
}
