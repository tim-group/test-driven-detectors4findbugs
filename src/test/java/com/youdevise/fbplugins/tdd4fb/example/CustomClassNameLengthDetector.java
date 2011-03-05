/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package com.youdevise.fbplugins.tdd4fb.example;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.ba.ClassContext;

public class CustomClassNameLengthDetector implements Detector {

	
	private static final int ARBITRARY_MAX_CLASS_NAME_LENGTH = 50;
	private final BugReporter bugReporter;

	public CustomClassNameLengthDetector(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}
	
	@Override public void report() {
		
	}

	@Override public void visitClassContext(ClassContext classContext) {
		int classNameLength = classContext.getClassDescriptor().getSimpleName().length();
		
		if(classNameLength > ARBITRARY_MAX_CLASS_NAME_LENGTH) {
			bugReporter.reportBug(new BugInstance("SILLY_BUG_TYPE", Priorities.NORMAL_PRIORITY));
		}
	}

}
