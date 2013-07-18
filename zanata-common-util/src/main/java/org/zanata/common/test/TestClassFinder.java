/*
 * Copyright 2013, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.zanata.common.test;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Set;

import org.junit.Test;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

import com.dhemery.runtimesuite.ClassFinder;
import com.google.common.collect.Lists;


/**
 * This class finds all the JUnit 4 test classes on a classpath.
 *
 * @author Sean Flanigan <a href="mailto:sflaniga@redhat.com">sflaniga@redhat.com</a>
 */
public class TestClassFinder implements ClassFinder
{
   private final URL[] urls;

   /**
    * Finds all the classes on the same classpath as the specified class.
    * @param baseClass
    */
   public TestClassFinder(Class<?> baseClass)
   {
      this(ClasspathUrlFinder.findClassBase(baseClass));
   }

   /**
    * Finds all the classes on the entire system classpath (SLOW).
    */
   public TestClassFinder()
   {
      this(ClasspathUrlFinder.findClassPaths());
   }

   private TestClassFinder(URL... urls)
   {
      this.urls = urls;
   }

   @Override
   public Collection<Class<?>> find()
   {
      try
      {
         AnnotationDB annotationDB = new AnnotationDB();
         annotationDB.scanArchives(urls);
         Set<String> classNames = annotationDB.getAnnotationIndex().get(Test.class.getName());
         if (classNames == null)
         {
            throw new RuntimeException("No test classes found");
         }
         Collection<Class<?>> classes = Lists.newArrayList();
         for (String className : classNames)
         {
            classes.add(Class.forName(className));
         }
         return classes;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      catch (ClassNotFoundException e)
      {
         throw new RuntimeException(e);
      }
   }

}
