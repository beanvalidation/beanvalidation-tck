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
package org.hibernate.beanvalidation.tck.tests.bootstrap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.validation.spi.ValidationProvider;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

import org.hibernate.beanvalidation.tck.util.TestUtil;
import org.hibernate.beanvalidation.tck.util.shrinkwrap.WebArchiveBuilder;

import static org.testng.Assert.assertTrue;

/**
 * @author Hardy Ferentschik
 */
@SpecVersion(spec = "beanvalidation", version = "1.1.0")
public class ValidationProviderResolverTest extends Arquillian {
	private static final String SERVICES_FILE = "META-INF/services/" + ValidationProvider.class.getName();

	@Deployment
	public static WebArchive createTestArchive() {
		return new WebArchiveBuilder()
				.withTestClass( ValidationProviderResolverTest.class )
				.build();
	}

	@Test
	@SpecAssertion(section = "4.4.4.1", id = "c")
	public void testServiceFileExists() {
		List<Class<?>> providers = readBeanValidationServiceFile();
		assertTrue( !providers.isEmpty(), "There should be at least one provider" );

		assertTrue(
				providers.contains( TestUtil.getValidationProviderUnderTest().getClass() ),
				"The validation class of the provider under test has to be in the list."
		);
	}

	private List<Class<?>> readBeanValidationServiceFile() {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		if ( classloader == null ) {
			classloader = ValidationProviderResolverTest.class.getClassLoader();
		}
		List<Class<?>> providers = new ArrayList<Class<?>>();
		try {

			Enumeration<URL> providerDefinitions = classloader.getResources( SERVICES_FILE );
			while ( providerDefinitions.hasMoreElements() ) {
				URL url = providerDefinitions.nextElement();
				addProviderToList( providers, url );
			}
		}
		catch ( Exception e ) {
			throw new RuntimeException( "Unable to load service file", e );
		}
		return providers;
	}

	private void addProviderToList(List<Class<?>> providers, URL url)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		InputStream stream = url.openStream();
		try {
			BufferedReader reader = new BufferedReader( new InputStreamReader( stream ), 100 );
			String name = reader.readLine();
			while ( name != null ) {
				name = name.trim();
				if ( !name.startsWith( "#" ) ) {
					final Class<?> providerClass = loadClass(
							name,
							ValidationProviderResolverTest.class
					);

					providers.add( providerClass );
				}
				name = reader.readLine();
			}
		}
		finally {
			stream.close();
		}
	}

	private static Class<?> loadClass(String name, Class caller) throws ClassNotFoundException {
		try {
			//try context classloader, if fails try caller classloader
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if ( loader != null ) {
				return loader.loadClass( name );
			}
		}
		catch ( ClassNotFoundException e ) {
			//trying caller classloader
			if ( caller == null ) {
				throw e;
			}
		}
		return Class.forName( name, true, caller.getClassLoader() );
	}
}
