/*******************************************************************************
 * This file is part of the OpenNMS(R) Application.
 *
 * OpenNMS(R) is Copyright (C) 1999-2011 The OpenNMS Group, Inc.  All rights reserved.
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 *     along with OpenNMS(R).  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information contact: 
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/
package org.opennms.netmgt.dao.db;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.sql.DataSource;

import org.junit.Test;
import org.opennms.netmgt.config.DataSourceFactory;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 * This {@link TestExecutionListener} creates a temporary database and then
 * registers it as the default datasource inside {@link DataSourceFactory} by
 * using {@link DataSourceFactory#setInstance(DataSource)}.
 * 
 * To change the settings for the temporary database, use the 
 * {@link JUnitTemporaryDatabase} annotation on the test class or method.
 * 
 * @author <a href="mailto:brozow@opennms.org">Mathew Brozowski</a>
 */
public class TemporaryDatabaseExecutionListener extends AbstractTestExecutionListener {

	private boolean m_createNewDatabases = false;
	private TemporaryDatabase m_database;
	private final Queue<TemporaryDatabase> m_databases = new ConcurrentLinkedQueue<TemporaryDatabase>();

	@Override
	public void afterTestMethod(final TestContext testContext) throws Exception {
		System.err.printf("TemporaryDatabaseExecutionListener.afterTestMethod(%s)\n", testContext);

		final JUnitTemporaryDatabase jtd = findAnnotation(testContext);
		if (jtd == null) return;

		try {
			// DON'T REMOVE THE DATABASE, just rely on the ShutdownHook to remove them instead
			// otherwise you might remove the class-level database that is reused between tests.
			// {@link TemporaryDatabase#createTestDatabase()}
			if (m_createNewDatabases) {
				final DataSource dataSource = DataSourceFactory.getInstance();
				final TemporaryDatabase tempDb = findTemporaryDatabase(dataSource);
				if (tempDb != null) {
					tempDb.drop();
				}
			}
		} finally {
			// We must mark the application context as dirty so that the DataSourceFactoryBean is
			// correctly pointed at the next temporary database.
			//
			// If the next database is the same as the current database, then do not rewire.
			// NOTE: This does not work because the Hibernate objects need to be reinjected or they
			// will reject database operations because they think that the database rows already
			// exist even if they were rolled back after a previous test execution.
			//
			//final DataSource dataSource = DataSourceFactory.getInstance();
			//final TemporaryDatabase tempDb = findTemporaryDatabase(dataSource);
			//if (tempDb != m_databases.peek()) {
				testContext.markApplicationContextDirty();
				testContext.setAttribute(DependencyInjectionTestExecutionListener.REINJECT_DEPENDENCIES_ATTRIBUTE, Boolean.TRUE);
			//}
		}
	}

	private static TemporaryDatabase findTemporaryDatabase(final DataSource dataSource) {
		if (dataSource instanceof TemporaryDatabase) {
			return (TemporaryDatabase) dataSource;
		} else if (dataSource instanceof DelegatingDataSource) {
			return findTemporaryDatabase(((DelegatingDataSource) dataSource).getTargetDataSource());
		} else {
			return null;
		}
	}

	private static JUnitTemporaryDatabase findAnnotation(final TestContext testContext) {
		JUnitTemporaryDatabase jtd = null;
		final Method testMethod = testContext.getTestMethod();
		if (testMethod != null) {
			jtd = testMethod.getAnnotation(JUnitTemporaryDatabase.class);
		}
		if (jtd == null) {
			final Class<?> testClass = testContext.getTestClass();
			jtd = testClass.getAnnotation(JUnitTemporaryDatabase.class);
		}
		return jtd;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void beforeTestMethod(final TestContext testContext) throws Exception {
		System.err.printf("TemporaryDatabaseExecutionListener.beforeTestMethod(%s)\n", testContext);

		// FIXME: Is there a better way to inject the instance into the test class?
		if (testContext.getTestInstance() instanceof TemporaryDatabaseAware) {
			System.err.println("injecting TemporaryDatabase into TemporaryDatabaseAware test: "
							+ testContext.getTestInstance().getClass().getSimpleName() + "."
							+ testContext.getTestMethod().getName());
			((TemporaryDatabaseAware) testContext.getTestInstance()).setTemporaryDatabase(m_database);
		}
	}

	@Override
	public void beforeTestClass(final TestContext testContext) {
		// Fire up a thread pool for each CPU to create test databases
		ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		List<Future<TemporaryDatabase>> futures = new ArrayList<Future<TemporaryDatabase>>();
		final JUnitTemporaryDatabase classJtd = testContext.getTestClass().getAnnotation(JUnitTemporaryDatabase.class);
		Future<TemporaryDatabase> classDs = null;
		if (classJtd == null) {
			classDs = null;
		} else {
			classDs = pool.submit(new CreateNewDatabaseCallable(classJtd));
		}
		if (classJtd != null && classJtd.reuseDatabase() == false) {
			m_createNewDatabases = true;
		}
		for (Method method : testContext.getTestClass().getMethods()) {
			if (method != null) {
				final JUnitTemporaryDatabase methodJtd = method.getAnnotation(JUnitTemporaryDatabase.class);
				boolean methodHasTest = method.getAnnotation(Test.class) != null;
				if (methodHasTest) {
					// If there is a method-specific annotation, use it to create the temporary database
					if (methodJtd != null) {
						// Create a new database based on the method-specific annotation
						futures.add(pool.submit(new CreateNewDatabaseCallable(methodJtd)));
					} else if (classJtd != null) {
						if (m_createNewDatabases) {
							// Create a new database based on the test class' annotation
							futures.add(pool.submit(new CreateNewDatabaseCallable(classJtd)));
						} else {
							// Reuse the database based on the test class' annotation
							futures.add(classDs);
						}
					}
				}
			}
		}

		for (Future<TemporaryDatabase> db : futures) {
			try {
				m_databases.add(db.get());
			} catch (InterruptedException e) {
				System.err.printf("TemporaryDatabaseExecutionListener: error while creating database: %s\n", e.getMessage());
				e.printStackTrace(System.err);
			} catch (ExecutionException e) {
				System.err.printf("TemporaryDatabaseExecutionListener: error while creating database: %s\n", e.getMessage());
				e.printStackTrace(System.err);
			}
		}
	}

	@Override
	public void prepareTestInstance(final TestContext testContext) throws Exception {
		System.err.printf("TemporaryDatabaseExecutionListener.prepareTestInstance(%s)\n", testContext);
		final JUnitTemporaryDatabase jtd = findAnnotation(testContext);

		if (jtd == null) return;

		m_database = m_databases.remove();
		final LazyConnectionDataSourceProxy proxy = new LazyConnectionDataSourceProxy(m_database);
		DataSourceFactory.setInstance(proxy);
		System.err.printf("TemporaryDatabaseExecutionListener.prepareTestInstance(%s) prepared db %s\n", testContext, m_database.toString());
	}

	private static class CreateNewDatabaseCallable implements Callable<TemporaryDatabase> {
		
		private final JUnitTemporaryDatabase m_jtd;
		
		public CreateNewDatabaseCallable(JUnitTemporaryDatabase jtd) {
			m_jtd = jtd;
		}
		
		@Override
		public TemporaryDatabase call() throws Exception {
			return createNewDatabase(m_jtd);
		}
		
	}

	private static TemporaryDatabase createNewDatabase(JUnitTemporaryDatabase jtd) {
		TemporaryDatabase retval;
		boolean useExisting = false;
		if (jtd.useExistingDatabase() != null) {
			useExisting = !jtd.useExistingDatabase().equals("");
		}

		try {
			final String dbName = useExisting ? jtd.useExistingDatabase() : getDatabaseName(jtd);
			retval = ((jtd.tempDbClass()).getConstructor(String.class, Boolean.TYPE).newInstance(dbName, useExisting));
			retval.setPopulateSchema(jtd.createSchema() && !useExisting);
			retval.create();
			return retval;
		} catch (final Throwable e) {
			System.err.printf("TemporaryDatabaseExecutionListener.prepareTestInstance: error while creating database: %s\n", e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
	}

	private static String getDatabaseName(Object hashMe) {
		// Append the current object's hashcode to make this value truly unique
		return String.format("opennms_test_%s_%s", System.nanoTime(), Math.abs(hashMe.hashCode()));
	}
}
