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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;

import com.youdevise.fbplugins.tdd4fb.internal.BugsReportedAsserter;
import com.youdevise.fbplugins.tdd4fb.internal.FindBugsMocks;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Priorities;

public class DetectorAssertTest {
	
	private BugReporter bugReporter;
	private BugInstance bugInstance;
	private BugsReportedAsserter asserter;
	
	@Before
	public void setUp() {
		bugReporter = FindBugsMocks.mockBugReporter();
		bugInstance = new BugInstance("SOME_BUG_TYPE", Priorities.LOW_PRIORITY);
		asserter = new BugsReportedAsserter();
	}
	
	@Test public void 
	assertNoBugsReportedPassesTestWhenNoBugIsReported() {
		// don't report a bug
		asserter.assertNoBugsReported(bugReporter);
	}
	
	@Test(expected=AssertionError.class) public void
	failsTestWhenABugHasBeenReportedAndAssertedThatNoBugHasBeenReported() {
		bugReporter.reportBug(bugInstance);
		asserter.assertNoBugsReported(bugReporter);
	}
	
	@Test public void
	assertBugReportedPassesTestWhenAnyBugIsReported() {
		bugReporter.reportBug(bugInstance);
		asserter.assertBugReported(bugReporter);
	}
	
	@Test(expected=AssertionError.class) public void
	assertBugReportedFailsTestWhenNoBugIsReported() {
		 // don't report a bug
		asserter.assertBugReported(bugReporter);
	}
	
	@Test public void
	assertBugReportedWhichMatchesDoesNotFailTest() {
		bugReporter.reportBug(bugInstance);
		asserter.assertBugReported(bugReporter, alwaysMatch(true));
	}
	
	@Test(expected=AssertionError.class) public void
	assertBugReportedWhichDoesNotMatchFailsTest() {
		bugReporter.reportBug(bugInstance);
		asserter.assertBugReported(bugReporter, alwaysMatch(false));
	}
	
	@Test(expected=AssertionError.class) public void
	assertBugReportedWithMatcherFailsWhenNoBugIsReported() {
		 // don't report a bug
		asserter.assertBugReported(bugReporter, alwaysMatch(true));
	}
	
	
	private BaseMatcher<BugInstance> alwaysMatch(final boolean alwaysMatch) {
		return new BaseMatcher<BugInstance>() {
			@Override public boolean matches(Object item) { return alwaysMatch; }
			@Override public void describeTo(Description description) { }
		};
	}

}
