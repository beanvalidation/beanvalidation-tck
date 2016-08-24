// $Id:$
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
package org.hibernate.beanvalidation.tck.tests.xmlconfiguration.constraintdeclaration.propertylevel;

import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

import org.hibernate.beanvalidation.tck.util.TestUtil;
import org.hibernate.beanvalidation.tck.util.shrinkwrap.WebArchiveBuilder;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * @author Hardy Ferentschik
 */
@SpecVersion(spec = "beanvalidation", version = "2.0.0")
public class ExcludePropertyLevelAnnotationsDueToBeanDefaultsTest extends Arquillian {

	@Deployment
	public static WebArchive createTestArchive() {
		return new WebArchiveBuilder()
				.withTestClass( ExcludePropertyLevelAnnotationsDueToBeanDefaultsTest.class )
				.withClasses( User.class, CreditCard.class )
				.withValidationXml( "validation-ExcludePropertyLevelAnnotationsDueToBeanDefaultsTest.xml" )
				.withResource( "user-constraints-ExcludePropertyLevelAnnotationsDueToBeanDefaultsTest.xml" )
				.build();
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "8.1.1.3", id = "b"),
			@SpecAssertion(section = "8.1.1.3", id = "c")
	})
	public void testIgnoreAnnotations() {
		Validator validator = TestUtil.getValidatorUnderTest();
		BeanDescriptor beanDescriptor = validator.getConstraintsForClass( User.class );
		assertNotNull( beanDescriptor );

		PropertyDescriptor propDescriptor = beanDescriptor.getConstraintsForProperty( "firstname" );
		assertNull( propDescriptor, "The annotation defined constraints should be ignored." );
	}
}
