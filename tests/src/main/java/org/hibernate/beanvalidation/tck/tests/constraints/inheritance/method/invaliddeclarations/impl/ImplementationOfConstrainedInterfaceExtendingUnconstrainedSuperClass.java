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
package org.hibernate.beanvalidation.tck.tests.constraints.inheritance.method.invaliddeclarations.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.beanvalidation.tck.tests.constraints.inheritance.method.invaliddeclarations.AbstractCalendarService;
import org.hibernate.beanvalidation.tck.tests.constraints.inheritance.method.invaliddeclarations.IAnotherCalendarService;
import org.hibernate.beanvalidation.tck.tests.constraints.inheritance.method.invaliddeclarations.model.Person;

/**
 * @author Gunnar Morling
 */
public class ImplementationOfConstrainedInterfaceExtendingUnconstrainedSuperClass
		extends AbstractCalendarService implements IAnotherCalendarService {

	@Override
	public void createEvent(Date start, Date end, List<Person> participants) {
	}
}
