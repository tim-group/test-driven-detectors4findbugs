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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.hamcrest.Matcher;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;

public class BugsReportedAsserter {

	public void assertNoBugsReported(BugReporter bugReporter) {
		verify(bugReporter, never()).reportBug(any(BugInstance.class));
	}

	public void assertBugReported(BugReporter bugReporter) {
		verify(bugReporter).reportBug(any(BugInstance.class));
	}

	public void assertBugReported(BugReporter bugReporter, Matcher<BugInstance> matches) {
		verify(bugReporter).reportBug(argThat(matches));
	}
}
