package org.zanata.common.util;

import org.zanata.common.LocaleId;
import org.zanata.util.HashUtil;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public class GlossaryUtil {

    /**
     * Generate hash resId with locale + content
     * @param locale
     * @param content
     */
    public static String getResId(LocaleId locale, String content) {
        String sep = "\u0000";
        String hashBase = locale + sep + content;

        return HashUtil.generateHash(hashBase);
    }
}
