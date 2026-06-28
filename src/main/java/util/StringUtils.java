package util;

import com.google.common.base.CaseFormat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static String toUnderscore(String name) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }

    public static String formatStringBrackets(String rawString, String value) {
        Matcher matcher = Pattern.compile("\\{.+?\\}").matcher(rawString);
        StringBuffer sbUrl = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sbUrl, value);
        }
        matcher.appendTail(sbUrl);
        return sbUrl.toString();
    }
}
