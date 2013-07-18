package org.zanata.common.util;

import org.junit.runner.RunWith;
import org.zanata.common.test.TestClassFinder;

import com.dhemery.runtimesuite.ClassFinder;
import com.dhemery.runtimesuite.Finder;
import com.dhemery.runtimesuite.RuntimeSuite;

/**
 * This suite runs all the JUnit 4 tests found in this Maven module's classpath.
 *
 * @author Sean Flanigan <a href="mailto:sflaniga@redhat.com">sflaniga@redhat.com</a>
 */
@RunWith(RuntimeSuite.class)
public class CommonUtilTestSuite
{
   @Finder public ClassFinder myFinder = new TestClassFinder(getClass());
}
