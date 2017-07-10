/**
 * Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.IterableAssert;

/**
 * This class provides useful functions to assert correctness of constraint violations raised
 * during tests.
 *
 * @author Kevin Pollet &lt;kevin.pollet@serli.com&gt; (C) 2011 SERLI
 * @author Hardy Ferentschik
 * @author Gunnar Morling
 */
public final class ConstraintViolationAssert {

	/**
	 * Expected name for return value nodes.
	 */
	public static final String BEAN_NODE_NAME = null;

	/**
	 * Expected name for cross-parameter nodes.
	 */
	public static final String CROSS_PARAMETER_NODE_NAME = "<cross-parameter>";

	/**
	 * Expected name for cross-parameter nodes.
	 */
	public static final String RETURN_VALUE_NODE_NAME = "<return value>";

	/**
	 * Private constructor in order to avoid instantiation.
	 */
	private ConstraintViolationAssert() {
	}

	/**
	 * Asserts that the messages in the violation list matches exactly the expected messages list.
	 *
	 * @param violations The violation list to verify.
	 * @param expectedMessages The expected constraint violation messages.
	 */
	public static void assertCorrectConstraintViolationMessages(Set<? extends ConstraintViolation<?>> violations,
			String... expectedMessages) {
		List<String> actualMessages = new ArrayList<>();
		for ( ConstraintViolation<?> violation : violations ) {
			actualMessages.add( violation.getMessage() );
		}

		Assertions.assertThat( actualMessages ).containsExactlyInAnyOrder( expectedMessages );
	}

	public static void assertCorrectConstraintViolationMessages(ConstraintViolationException e,
			String... expectedMessages) {
		assertCorrectConstraintViolationMessages( e.getConstraintViolations(), expectedMessages );
	}

	/**
	 * Asserts that the violated constraint type in the violation list matches exactly the expected constraint types
	 * list.
	 *
	 * @param violations The violation list to verify.
	 * @param expectedConstraintTypes The expected constraint types.
	 */
	public static void assertCorrectConstraintTypes(Set<? extends ConstraintViolation<?>> violations,
			Class<?>... expectedConstraintTypes) {
		List<Class<? extends Annotation>> actualConstraintTypes = new ArrayList<>();
		for ( ConstraintViolation<?> violation : violations ) {
			actualConstraintTypes.add( violation.getConstraintDescriptor().getAnnotation().annotationType() );
		}

		assertCorrectConstraintTypes( actualConstraintTypes, expectedConstraintTypes );
	}

	public static ConstraintViolationSetAssert assertThat(Set<? extends ConstraintViolation<?>> actualViolations) {
		return new ConstraintViolationSetAssert( actualViolations );
	}

	/**
	 * Asserts that the error message, root bean class, invalid value and property path of the given violation are equal
	 * to the expected message, root bean class, invalid value and propertyPath.
	 *
	 * @param violation The violation to verify.
	 * @param rootBeanClass The expected root bean class.
	 * @param invalidValue The expected invalid value.
	 * @param propertyPath The expected property path.
	 */
	public static void assertConstraintViolation(ConstraintViolation<?> violation, Class<?> rootBeanClass, Object invalidValue, PathExpectation propertyPath) {
		Assertions.assertThat( new PathExpectation( violation.getPropertyPath() ) ).isEqualTo( propertyPath );
		assertConstraintViolation( violation, rootBeanClass, invalidValue );
	}

	/**
	 * Asserts that the error message, root bean class and invalid value of the given violation are equal to the
	 * expected message, root bean class and invalid value.
	 *
	 * @param violation The violation to verify.
	 * @param rootBeanClass The expected root bean class.
	 * @param invalidValue The expected invalid value.
	 */
	public static void assertConstraintViolation(ConstraintViolation<?> violation, Class<?> rootBeanClass, Object invalidValue) {
		assertEquals( violation.getInvalidValue(), invalidValue, "Wrong invalid value" );
		assertConstraintViolation( violation, rootBeanClass );
	}

	/**
	 * Asserts that the error message and the root bean class of the given violation are equal to the expected message
	 * and root bean class.
	 *
	 * @param violation The violation to verify.
	 * @param rootBeanClass The expected root bean class.
	 */
	public static void assertConstraintViolation(ConstraintViolation<?> violation, Class<?> rootBeanClass) {
		assertEquals( violation.getRootBeanClass(), rootBeanClass, "Wrong root bean type" );
	}

	/**
	 * Asserts that the given violation list has the expected number of violations.
	 *
	 * @param violations The violation list to verify.
	 * @param numberOfViolations The expected number of violation.
	 */
	public static void assertNumberOfViolations(Set<? extends ConstraintViolation<?>> violations,
			int numberOfViolations) {
		assertEquals(
				violations.size(),
				numberOfViolations,
				"Wrong number of constraint violations"
		);
	}

	/**
	 * Checks that two property paths are equal.
	 *
	 * @param p1 The first property path.
	 * @param p2 The second property path.
	 *
	 * @return {@code true} if the given paths are equal, {@code false} otherwise.
	 */
	public static boolean pathsAreEqual(Path p1, Path p2) {
		if ( p1 == null ) {
			return p2 == null;
		}
		return pathsAreEqual( new PathExpectation( p1 ), new PathExpectation( p2 ) );
	}

	/**
	 * Checks that two property paths are equal.
	 *
	 * @param p1 The first property path.
	 * @param p2 The second property path.
	 *
	 * @return {@code true} if the given paths are equal, {@code false} otherwise.
	 */
	public static boolean pathsAreEqual(Path p1, PathExpectation p2) {
		if ( p1 == null ) {
			return p2 == null;
		}
		return pathsAreEqual( new PathExpectation( p1 ), p2 );
	}

	/**
	 * Checks that two property paths are equal.
	 *
	 * @param p1 The first property path.
	 * @param p2 The second property path.
	 *
	 * @return {@code true} if the given paths are equal, {@code false} otherwise.
	 */
	public static boolean pathsAreEqual(PathExpectation p1, PathExpectation p2) {
		if ( p1 == null ) {
			return p2 == null;
		}
		return p1.equals( p2 );
	}

	/**
	 * <p>
	 * Asserts that the two given collections contain the same constraint types.
	 * </p>
	 * <p>
	 * Multiset semantics is used for the comparison, i.e. the same constraint
	 * type can be contained several times in the compared collections, but the
	 * order doesn't matter. The comparison is done using the class names, since
	 * {@link Class} doesn't implement {@link Comparable}.
	 * </p>
	 *
	 * @param actualConstraintTypes The actual constraint types.
	 * @param expectedConstraintTypes The expected constraint types.
	 */
	private static <T> void assertCorrectConstraintTypes(Iterable<Class<? extends Annotation>> actualConstraintTypes,
			Class<?>... expectedConstraintTypes) {

		Assertions.assertThat( actualConstraintTypes )
				.extracting( Class::getName )
				.containsExactlyInAnyOrder( Arrays.stream( expectedConstraintTypes ).map( clazz -> clazz.getName() ).toArray( size -> new String[size] ) );
	}

	public static PathExpectation pathWith() {
		return new PathExpectation();
	}

	public static ViolationExpectation violationOf(Class<? extends Annotation> constraintType) {
		return new ViolationExpectation( constraintType );
	}

	public static class ConstraintViolationSetAssert extends IterableAssert<ConstraintViolation<?>> {

		protected ConstraintViolationSetAssert(Set<? extends ConstraintViolation<?>> actualViolations) {
			super( actualViolations );
		}

		public void containsOnlyViolations(ViolationExpectation... expectedViolations) {
			isNotNull();

			List<ViolationExpectation> actualViolations = new ArrayList<>();

			ViolationExpectationPropertiesToTest referencePropertiesToTest;
			if ( expectedViolations.length == 0 ) {
				referencePropertiesToTest = ViolationExpectationPropertiesToTest.all();
			}
			else {
				referencePropertiesToTest = expectedViolations[0].propertiesToTest;
				for ( ViolationExpectation expectedViolation : expectedViolations ) {
					if ( !referencePropertiesToTest.equals( expectedViolation.propertiesToTest ) ) {
						throw new IllegalArgumentException( String.format( "Expected violations passed in parameter must test the exact same properties but do not: %1$s != %2$s",
								expectedViolations[0], expectedViolation ) );
					}
				}
			}

			for ( ConstraintViolation<?> violation : actual ) {
				actualViolations.add( new ViolationExpectation( violation, referencePropertiesToTest ) );
			}

			Assertions.assertThat( actualViolations ).containsExactlyInAnyOrder( expectedViolations );
		}

		public void containsOnlyPaths(PathExpectation... paths) {
			isNotNull();

			List<PathExpectation> actualPaths = new ArrayList<>();

			for ( ConstraintViolation<?> violation : actual ) {
				actualPaths.add( new PathExpectation( violation.getPropertyPath() ) );
			}

			Assertions.assertThat( actualPaths ).containsExactlyInAnyOrder( paths );
		}

		public void containsPath(PathExpectation expectedPath) {
			isNotNull();

			List<PathExpectation> actualPaths = new ArrayList<>();
			for ( ConstraintViolation<?> violation : actual ) {
				PathExpectation actual = new PathExpectation( violation.getPropertyPath() );
				if ( actual.equals( expectedPath ) ) {
					return;
				}
				actualPaths.add( actual );
			}

			fail( String.format( "Didn't find path <%s> in actual paths <%s>.", expectedPath, actualPaths ) );
		}

		public void containsPaths(PathExpectation... expectedPaths) {
			for ( PathExpectation pathExpectation : expectedPaths ) {
				containsPath( pathExpectation );
			}
		}
	}

	public static class ViolationExpectation {

		private final ViolationExpectationPropertiesToTest propertiesToTest = new ViolationExpectationPropertiesToTest();

		private final Class<? extends Annotation> constraintType;

		private Class<?> rootBeanClass;

		private String message;

		private Object invalidValue;

		private PathExpectation propertyPath;

		private Object leafBean;

		private ViolationExpectation(Class<? extends Annotation> constraintType) {
			this.constraintType = constraintType;
		}

		private ViolationExpectation(ConstraintViolation<?> violation, ViolationExpectationPropertiesToTest propertiesToTest) {
			this.constraintType = violation.getConstraintDescriptor().getAnnotation().annotationType();

			if ( propertiesToTest.testRootBeanClass ) {
				withRootBeanClass( violation.getRootBeanClass() );
			}
			if ( propertiesToTest.testMessage ) {
				withMessage( violation.getMessage() );
			}
			if ( propertiesToTest.testInvalidValue ) {
				withInvalidValue( violation.getInvalidValue() );
			}
			if ( propertiesToTest.testPropertyPath ) {
				withPropertyPath( new PathExpectation( violation.getPropertyPath() ) );
			}
			if ( propertiesToTest.testLeafBean ) {
				withLeafBean( violation.getLeafBean() );
			}
		}

		public ViolationExpectation withRootBeanClass(Class<?> rootBeanClass) {
			propertiesToTest.testRootBeanClass();
			this.rootBeanClass = rootBeanClass;
			return this;
		}

		public ViolationExpectation withMessage(String message) {
			propertiesToTest.testMessage();
			this.message = message;
			return this;
		}

		public ViolationExpectation withInvalidValue(Object invalidValue) {
			propertiesToTest.testInvalidValue();
			this.invalidValue = invalidValue;
			return this;
		}

		public ViolationExpectation withPropertyPath(PathExpectation propertyPath) {
			propertiesToTest.testPropertyPath();
			this.propertyPath = propertyPath;
			return this;
		}

		public ViolationExpectation withProperty(String property) {
			return withPropertyPath( pathWith().property( property ) );
		}

		public ViolationExpectation withLeafBean(Object leafBean) {
			propertiesToTest.testLeafBean();
			this.leafBean = leafBean;
			return this;
		}

		@Override
		public String toString() {
			String lineBreak = System.getProperty( "line.separator" );
			StringBuilder asString = new StringBuilder( lineBreak + "ViolationExpectation(" + lineBreak );
			asString.append( "  constraintType: " ).append( constraintType ).append( lineBreak );
			if ( propertiesToTest.testRootBeanClass ) {
				asString.append( "  rootBeanClass: " ).append( rootBeanClass ).append( lineBreak );
			}
			if ( propertiesToTest.testMessage ) {
				asString.append( "  message: " ).append( message ).append( lineBreak );
			}
			if ( propertiesToTest.testInvalidValue ) {
				asString.append( "  invalidValue: " ).append( invalidValue ).append( lineBreak );
			}
			if ( propertiesToTest.testPropertyPath ) {
				asString.append( "  propertyPath: " ).append( propertyPath.toStringInViolation() ).append( lineBreak );
			}
			if ( propertiesToTest.testLeafBean ) {
				asString.append( "  leafBean: " ).append( leafBean ).append( lineBreak );
			}

			return asString.append( ")" ).toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ( constraintType == null ? 0 : constraintType.hashCode() );
			if ( propertiesToTest.testRootBeanClass ) {
				result = prime * result + ( rootBeanClass == null ? 0 : rootBeanClass.hashCode() );
			}
			if ( propertiesToTest.testMessage ) {
				result = prime * result + ( message == null ? 0 : message.hashCode() );
			}
			if ( propertiesToTest.testInvalidValue ) {
				result = prime * result + ( invalidValue == null ? 0 : invalidValue.hashCode() );
			}
			if ( propertiesToTest.testPropertyPath ) {
				result = prime * result + ( propertyPath == null ? 0 : propertyPath.hashCode() );
			}
			if ( propertiesToTest.testLeafBean ) {
				result = prime * result + ( leafBean == null ? 0 : leafBean.hashCode() );
			}
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if ( this == obj ) {
				return true;
			}
			if ( obj == null ) {
				return false;
			}
			if ( getClass() != obj.getClass() ) {
				return false;
			}
			ViolationExpectation other = (ViolationExpectation) obj;
			if ( constraintType == null ) {
				if ( other.constraintType != null ) {
					return false;
				}
			}
			else if ( !constraintType.equals( other.constraintType ) ) {
				return false;
			}
			if ( propertiesToTest.testRootBeanClass ) {
				if ( rootBeanClass == null ) {
					if ( other.rootBeanClass != null ) {
						return false;
					}
				}
				else if ( !rootBeanClass.equals( other.rootBeanClass ) ) {
					return false;
				}
			}
			if ( propertiesToTest.testMessage ) {
				if ( message == null ) {
					if ( other.message != null ) {
						return false;
					}
				}
				else if ( !message.equals( other.message ) ) {
					return false;
				}
			}
			if ( propertiesToTest.testInvalidValue ) {
				if ( invalidValue == null ) {
					if ( other.invalidValue != null ) {
						return false;
					}
				}
				else if ( !invalidValue.equals( other.invalidValue ) ) {
					return false;
				}
			}
			if ( propertiesToTest.testPropertyPath ) {
				if ( propertyPath == null ) {
					if ( other.propertyPath != null ) {
						return false;
					}
				}
				else if ( !propertyPath.equals( other.propertyPath ) ) {
					return false;
				}
			}
			if ( propertiesToTest.testLeafBean ) {
				if ( leafBean == null ) {
					if ( other.leafBean != null ) {
						return false;
					}
				}
				else if ( !leafBean.equals( other.leafBean ) ) {
					return false;
				}
			}
			return true;
		}
	}

	private static class ViolationExpectationPropertiesToTest {

		private boolean testRootBeanClass = false;

		private boolean testMessage = false;

		private boolean testInvalidValue = false;

		private boolean testPropertyPath = false;

		private boolean testLeafBean = false;

		private static ViolationExpectationPropertiesToTest all() {
			ViolationExpectationPropertiesToTest propertiesToTest = new ViolationExpectationPropertiesToTest()
					.testRootBeanClass()
					.testMessage()
					.testInvalidValue()
					.testPropertyPath()
					.testLeafBean();
			return propertiesToTest;
		}

		private ViolationExpectationPropertiesToTest testRootBeanClass() {
			testRootBeanClass = true;
			return this;
		}

		private ViolationExpectationPropertiesToTest testMessage() {
			testMessage = true;
			return this;
		}

		private ViolationExpectationPropertiesToTest testInvalidValue() {
			testInvalidValue = true;
			return this;
		}

		private ViolationExpectationPropertiesToTest testPropertyPath() {
			testPropertyPath = true;
			return this;
		}

		private ViolationExpectationPropertiesToTest testLeafBean() {
			testLeafBean = true;
			return this;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ( testRootBeanClass ? 1 : 0 );
			result = prime * result + ( testMessage ? 1 : 0 );
			result = prime * result + ( testInvalidValue ? 1 : 0 );
			result = prime * result + ( testPropertyPath ? 1 : 0 );
			result = prime * result + ( testLeafBean ? 1 : 0 );
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if ( this == obj ) {
				return true;
			}
			if ( obj == null ) {
				return false;
			}
			if ( getClass() != obj.getClass() ) {
				return false;
			}

			ViolationExpectationPropertiesToTest other = (ViolationExpectationPropertiesToTest) obj;
			if ( testRootBeanClass != other.testRootBeanClass ) {
				return false;
			}
			if ( testMessage != other.testMessage ) {
				return false;
			}
			if ( testInvalidValue != other.testInvalidValue ) {
				return false;
			}
			if ( testPropertyPath != other.testPropertyPath ) {
				return false;
			}
			if ( testLeafBean != other.testLeafBean ) {
				return false;
			}

			return true;
		}
	}

	/**
	 * A property path expected to be returned by a given {@link ConstraintViolation}.
	 */
	public static class PathExpectation {

		private final List<NodeExpectation> nodes = new ArrayList<>();

		private PathExpectation() {
		}

		private PathExpectation(Path propertyPath) {
			for ( Path.Node node : propertyPath ) {
				Integer parameterIndex = null;
				if ( node.getKind() == ElementKind.PARAMETER ) {
					parameterIndex = node.as( Path.ParameterNode.class ).getParameterIndex();
				}
				Class<?> containerClass = getContainerClass( node );
				Integer typeArgumentIndex = getTypeArgumentIndex( node );
				nodes.add(
						new NodeExpectation(
								node.getName(),
								node.getKind(),
								node.isInIterable(),
								node.getKey(),
								node.getIndex(),
								parameterIndex,
								containerClass,
								typeArgumentIndex
						)
				);
			}
		}

		public PathExpectation property(String name) {
			nodes.add( new NodeExpectation( name, ElementKind.PROPERTY ) );
			return this;
		}

		public PathExpectation property(String name, Class<?> containerClass, Integer typeArgumentIndex) {
			nodes.add( new NodeExpectation( name, ElementKind.PROPERTY, false, null, null, null, containerClass, typeArgumentIndex ) );
			return this;
		}

		public PathExpectation property(String name, boolean inIterable, Object key, Integer index) {
			nodes.add( new NodeExpectation( name, ElementKind.PROPERTY, inIterable, key, index, null, null, null ) );
			return this;
		}

		public PathExpectation property(String name, boolean inIterable, Object key, Integer index, Class<?> containerClass, Integer typeArgumentIndex) {
			nodes.add( new NodeExpectation( name, ElementKind.PROPERTY, inIterable, key, index, null, containerClass, typeArgumentIndex ) );
			return this;
		}

		public PathExpectation bean() {
			nodes.add( new NodeExpectation( null, ElementKind.BEAN ) );
			return this;
		}

		public PathExpectation bean(boolean inIterable, Object key, Integer index) {
			nodes.add( new NodeExpectation( null, ElementKind.BEAN, inIterable, key, index, null, null, null ) );
			return this;
		}

		public PathExpectation bean(boolean inIterable, Object key, Integer index, Class<?> containerClass, Integer typeArgumentIndex) {
			nodes.add( new NodeExpectation( null, ElementKind.BEAN, inIterable, key, index, null, containerClass, typeArgumentIndex ) );
			return this;
		}

		public PathExpectation constructor(Class<?> clazz) {
			nodes.add( new NodeExpectation( clazz.getSimpleName(), ElementKind.CONSTRUCTOR ) );
			return this;
		}

		public PathExpectation method(String name) {
			nodes.add( new NodeExpectation( name, ElementKind.METHOD ) );
			return this;
		}

		public PathExpectation parameter(String name, int index) {
			nodes.add( new NodeExpectation( name, ElementKind.PARAMETER, false, null, null, index, null, null ) );
			return this;
		}

		public PathExpectation crossParameter() {
			nodes.add( new NodeExpectation( CROSS_PARAMETER_NODE_NAME, ElementKind.CROSS_PARAMETER ) );
			return this;
		}

		public PathExpectation returnValue() {
			nodes.add( new NodeExpectation( RETURN_VALUE_NODE_NAME, ElementKind.RETURN_VALUE ) );
			return this;
		}

		public PathExpectation containerElement(String name, boolean inIterable, Object key, Integer index, Class<?> containerClass, Integer typeArgumentIndex) {
			nodes.add( new NodeExpectation( name, ElementKind.CONTAINER_ELEMENT, inIterable, key, index, null, containerClass, typeArgumentIndex ) );
			return this;
		}

		@Override
		public String toString() {
			String lineBreak = System.getProperty( "line.separator" );
			StringBuilder asString = new StringBuilder( lineBreak + "PathExpectation(" + lineBreak );
			for ( NodeExpectation node : nodes ) {
				asString.append( "  " ).append( node ).append( lineBreak );
			}

			return asString.append( ")" ).toString();
		}

		public String toStringInViolation() {
			String lineBreak = System.getProperty( "line.separator" );
			StringBuilder asString = new StringBuilder( "PathExpectation(" + lineBreak );
			for ( NodeExpectation node : nodes ) {
				asString.append( "    " ).append( node ).append( lineBreak );
			}

			return asString.append( "  )" ).toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ( ( nodes == null ) ? 0 : nodes.hashCode() );
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if ( this == obj ) {
				return true;
			}
			if ( obj == null ) {
				return false;
			}
			if ( getClass() != obj.getClass() ) {
				return false;
			}
			PathExpectation other = (PathExpectation) obj;
			if ( nodes == null ) {
				if ( other.nodes != null ) {
					return false;
				}
			}
			else if ( !nodes.equals( other.nodes ) ) {
				return false;
			}
			return true;
		}
	}

	/**
	 * A node expected to be contained in the property path returned by a given {@link ConstraintViolation}.
	 */
	private static class NodeExpectation {
		private final String name;
		private final ElementKind kind;
		private final boolean inIterable;
		private final Object key;
		private final Integer index;
		private final Integer parameterIndex;
		private final Class<?> containerClass;
		private final Integer typeArgumentIndex;

		private NodeExpectation(String name, ElementKind kind) {
			this( name, kind, false, null, null, null, null, null );
		}

		private NodeExpectation(String name, ElementKind kind, boolean inIterable, Object key, Integer index,
				Integer parameterIndex, Class<?> containerClass, Integer typeArgumentIndex) {
			this.name = name;
			this.kind = kind;
			this.inIterable = inIterable;
			this.key = key;
			this.index = index;
			this.parameterIndex = parameterIndex;
			this.containerClass = containerClass;
			this.typeArgumentIndex = typeArgumentIndex;
		}

		@Override
		public String toString() {
			return "NodeExpectation(" + name + ", " + kind + ", " + inIterable
					+ ", " + key + ", " + index + ", " + parameterIndex + ", " + containerClass + ", " + typeArgumentIndex + ")";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ( inIterable ? 1231 : 1237 );
			result = prime * result + ( ( index == null ) ? 0 : index.hashCode() );
			result = prime * result + ( ( key == null ) ? 0 : key.hashCode() );
			result = prime * result + ( ( kind == null ) ? 0 : kind.hashCode() );
			result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
			result = prime * result + ( ( parameterIndex == null ) ? 0 : parameterIndex.hashCode() );
			result = prime * result + ( ( containerClass == null ) ? 0 : containerClass.hashCode() );
			result = prime * result + ( ( typeArgumentIndex == null ) ? 0 : typeArgumentIndex.hashCode() );
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if ( this == obj ) {
				return true;
			}
			if ( obj == null ) {
				return false;
			}
			if ( getClass() != obj.getClass() ) {
				return false;
			}
			NodeExpectation other = (NodeExpectation) obj;
			if ( inIterable != other.inIterable ) {
				return false;
			}
			if ( index == null ) {
				if ( other.index != null ) {
					return false;
				}
			}
			else if ( !index.equals( other.index ) ) {
				return false;
			}
			if ( key == null ) {
				if ( other.key != null ) {
					return false;
				}
			}
			else if ( !key.equals( other.key ) ) {
				return false;
			}
			if ( kind != other.kind ) {
				return false;
			}
			if ( name == null ) {
				if ( other.name != null ) {
					return false;
				}
			}
			else if ( !name.equals( other.name ) ) {
				return false;
			}
			if ( parameterIndex == null ) {
				if ( other.parameterIndex != null ) {
					return false;
				}
			}
			else if ( !parameterIndex.equals( other.parameterIndex ) ) {
				return false;
			}
			if ( containerClass == null ) {
				if ( other.containerClass != null ) {
					return false;
				}
			}
			else if ( !containerClass.equals( other.containerClass ) ) {
				return false;
			}
			if ( typeArgumentIndex == null ) {
				if ( other.typeArgumentIndex != null ) {
					return false;
				}
			}
			else if ( !typeArgumentIndex.equals( other.typeArgumentIndex ) ) {
				return false;
			}
			return true;
		}
	}

	private static Class<?> getContainerClass(Path.Node node) {
		Class<?> containerClass = null;
		if ( node.getKind() == ElementKind.PROPERTY ) {
			containerClass = node.as( Path.PropertyNode.class ).getContainerClass();
		}
		if ( node.getKind() == ElementKind.BEAN ) {
			containerClass = node.as( Path.BeanNode.class ).getContainerClass();
		}
		if ( node.getKind() == ElementKind.CONTAINER_ELEMENT ) {
			containerClass = node.as( Path.ContainerElementNode.class ).getContainerClass();
		}
		return containerClass;
	}

	private static Integer getTypeArgumentIndex(Path.Node node) {
		Integer typeArgumentIndex = null;
		if ( node.getKind() == ElementKind.PROPERTY ) {
			typeArgumentIndex = node.as( Path.PropertyNode.class ).getTypeArgumentIndex();
		}
		if ( node.getKind() == ElementKind.BEAN ) {
			typeArgumentIndex = node.as( Path.BeanNode.class ).getTypeArgumentIndex();
		}
		if ( node.getKind() == ElementKind.CONTAINER_ELEMENT ) {
			typeArgumentIndex = node.as( Path.ContainerElementNode.class ).getTypeArgumentIndex();
		}
		return typeArgumentIndex;
	}
}
