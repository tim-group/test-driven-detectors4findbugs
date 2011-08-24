package com.youdevise.fbplugins.tdd4fb;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class CustomCollectionsTest {
    
    @Test
    public void splitByDelimiterAndFilterByRegexSplitsCorrectly() {
        String string = "guice-3.0.jar:b.jar:c.class";
        String delimiter = ":";
        String regex = ".*\\.jar";
        
        Set<String> expectedSet = new HashSet<String>();
        expectedSet.add("guice-3.0.jar");
        expectedSet.add("b.jar");
        
        Set<String> actualSet = CustomCollections.splitByDelimiterAndFilterByRegex(string, delimiter, regex);
        
        assertEquals(expectedSet, actualSet);
    }

}
