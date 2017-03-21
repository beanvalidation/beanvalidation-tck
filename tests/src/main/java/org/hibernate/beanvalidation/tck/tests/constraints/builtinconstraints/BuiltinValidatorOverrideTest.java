/**
 * Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.constraints.builtinconstraints;

import static org.hibernate.beanvalidation.tck.util.TestUtil.assertCorrectNumberOfViolations;
import static org.hibernate.beanvalidation.tck.util.TestUtil.getInputStreamForPath;

import java.io.InputStream;
import java.util.Set;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import org.hibernate.beanvalidation.tck.beanvalidation.Sections;
import org.hibernate.beanvalidation.tck.util.TestUtil;
import org.hibernate.beanvalidation.tck.util.shrinkwrap.WebArchiveBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

/**
 * @author Hardy Ferentschik
 */
@SpecVersion(spec = "beanvalidation", version = "2.0.0")
public class BuiltinValidatorOverrideTest extends Arquillian {

	@Deployment
	public static WebArchive createTestArchive() {
		return new WebArchiveBuilder()
				.withTestClass( BuiltinValidatorOverrideTest.class )
				.withClass( InvertedNotNullValidator.class )
				.withResource( "builtin-constraints-override.xml" )
				.build();
	}

	@Test
	@SpecAssertion(section = Sections.BUILTINCONSTRAINTS, id = "b")
	public void testXmlConfiguredValidatorConfigurationHasPrecedence() {
		Configuration<?> config = TestUtil.getConfigurationUnderTest();
		InputStream in = getInputStreamForPath(
				"org/hibernate/beanvalidation/tck/tests/constraints/builtinconstraints/builtin-constraints-override.xml"
		);
		config.addMapping( in );
		Validator validator = config.buildValidatorFactory().getValidator();
		DummyEntity dummy = new DummyEntity();
		Set<ConstraintViolation<DummyEntity>> violations = validator.validate( dummy );
		assertCorrectNumberOfViolations( violations, 0 );

		dummy.dummyProperty = "foobar";
		violations = validator.validate( dummy );
		assertCorrectNumberOfViolations( violations, 1 );
	}

	private static class DummyEntity {
		@NotNull
		private String dummyProperty;
	}

}
