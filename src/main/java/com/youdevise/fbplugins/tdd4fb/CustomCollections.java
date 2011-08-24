package com.youdevise.fbplugins.tdd4fb;

import java.util.HashSet;
import java.util.Set;


class CustomCollections {
    private CustomCollections() {}
    
    // Ideally a Predicate class would be used in this method, but a dependence on, say, guava, is unwarranted
    static Set<String> splitByDelimiterAndFilterByRegex(String stringToBeSplit, String delimiter, String regex) {
        Set<String> set = new HashSet<String>();
        
        for(String field : stringToBeSplit.split(delimiter)) {
            if (field.matches(regex)) {
                set.add(field);
            }
        }
        
        return set;
    }
}
