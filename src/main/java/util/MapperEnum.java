package util;

import com.google.common.base.CaseFormat;

public class MapperEnum {
     public static String toUnderscore(String name){
       return   CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
     }
}
