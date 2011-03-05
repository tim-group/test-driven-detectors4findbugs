/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package com.youdevise.fbplugins.tdd4fb.internal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.ProjectStats;

public class FindBugsMocks {

	public static BugReporter mockBugReporter() {
		BugReporter bugReporter = mock(BugReporter.class);
		when(bugReporter.getProjectStats()).thenReturn(new ProjectStats());
		return bugReporter;
	}
	
}
