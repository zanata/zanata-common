package org.zanata.common.util;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.zanata.common.test.IntegrationTestCategory;

/**
 * This suite runs all the JUnit 4 tests in the Integration Test Category
 * found in this Maven module's classpath.
 *
 * @author Sean Flanigan <a href="mailto:sflaniga@redhat.com">sflaniga@redhat.com</a>
 */
@RunWith(Categories.class)
@SuiteClasses({CommonUtilTestSuite.class})
@Categories.IncludeCategory(IntegrationTestCategory.class)
public class CommonUtilITSuite
{
}
