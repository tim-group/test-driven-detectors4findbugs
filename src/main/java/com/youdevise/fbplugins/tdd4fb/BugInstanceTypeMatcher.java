/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package com.youdevise.fbplugins.tdd4fb;

import static java.lang.String.format;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import edu.umd.cs.findbugs.BugInstance;

public class BugInstanceTypeMatcher extends BaseMatcher<BugInstance> {

    private final String bugType;

    BugInstanceTypeMatcher(String bugType) {
        this.bugType = bugType;
    }
    
    @Override
    public boolean matches(Object obj) {
        if(! (obj instanceof BugInstance)) {
            return false;
        }
        BugInstance bug = (BugInstance) obj;
        
        return bugType.equals(bug.getType());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(format("with bug of type '%s'", bugType));
    }
    
}