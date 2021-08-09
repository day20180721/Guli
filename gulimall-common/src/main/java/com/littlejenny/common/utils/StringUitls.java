package com.littlejenny.common.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class StringUitls {
    public static String combineByDelimiter(String delimiter, List<String> strings){
        StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            builder.append(string+delimiter);
        }
        builder = builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
