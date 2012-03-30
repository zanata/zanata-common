package org.zanata.util;

import java.util.ArrayList;
import java.util.List;

/**
 * ShortStrings are meant for use in logging. They don't incur the cost of
 * shortening until toString() is called. This means they hold on to the entire
 * string, so don't bother keeping them around in memory for long.
 * 
 * @author Sean Flanigan <a
 *         href="mailto:sflaniga@redhat.com">sflaniga@redhat.com</a>
 * 
 */
public class ShortString
{

   static final int MAX_LENGTH = 66;
   private static final String ELLIPSIS = "…";
   private String input;

   public ShortString(String input)
   {
      this.input = input;
   }

   @Override
   public String toString()
   {
      return input = shorten(input);
   }

   public static String shorten(String s)
   {
      if (s.length() <= MAX_LENGTH)
         return s;
      return s.substring(0, MAX_LENGTH - ELLIPSIS.length()) + ELLIPSIS;
   }

   /**
    * @param strings
    * @return
    */
   public static String shorten(List<String> strings)
   {
      List<String> shortStrings = new ArrayList<String>(strings.size());
      for (String s : strings)
      {
         shortStrings.add(shorten(s));
      }
      return shortStrings.toString();
   }

}
