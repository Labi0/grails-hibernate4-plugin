/* Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.grails.scaffolding;

import groovy.lang.GroovyClassLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass;
import org.codehaus.groovy.grails.commons.GrailsDomainClass;
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty;
import org.codehaus.groovy.grails.plugins.MockGrailsPluginManager;
import grails.util.Holders;

/**
 * @author Graeme Rocher
 * @author Sergey Nebolsin
 */
public class DomainClassPropertyComparatorTests extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		MockGrailsPluginManager pluginManager = new MockGrailsPluginManager();
		Holders.setPluginManager(pluginManager);
		pluginManager.registerMockPlugin(DefaultGrailsTemplateGeneratorTests.fakeHibernatePlugin);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		Holders.setPluginManager(null);
	}

	@SuppressWarnings("unchecked")
	public void testPropertyComparator() throws Exception {
		GroovyClassLoader gcl = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());

		Class<?> dc = gcl.parseClass(
				"class Test { \n" +
				"	Long id\n" +
				"	Long version\n" +
				"	String zip\n" +
				"	String dob\n" +
				"	Date age\n" +
				"	String name\n" +
				"	static constraints = {\n" +
				"		name(size:5..15)\n" +
				"		age()\n" +
				"	}\n" +
				"}\n");

		GrailsDomainClass domainClass = new DefaultGrailsDomainClass(dc);

		DomainClassPropertyComparator comp = new DomainClassPropertyComparator(domainClass);

		GrailsDomainClassProperty[] props = domainClass.getProperties();
		Arrays.sort(props, comp);

		List<String> nonConstrainedProps = new ArrayList<String>();
		nonConstrainedProps.add("zip");
		nonConstrainedProps.add("dob");
		nonConstrainedProps.add("version");

		// all we need to test is that the first properties will be 'id' after that constrained properties
		// will appear in the same order as in 'constraints' closure, and all other properties will be
		// putted at the end
		assertEquals("id", props[0].getName());
		assertEquals("name", props[1].getName());
		assertEquals("age", props[2].getName());
		assertTrue(nonConstrainedProps.contains(props[3].getName()));
		assertTrue(nonConstrainedProps.contains(props[4].getName()));
		assertTrue(nonConstrainedProps.contains(props[5].getName()));
	}
}
