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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collection;

import org.hamcrest.Matcher;

import com.youdevise.fbplugins.tdd4fb.TestingBugReporter.TddBugReporter;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;

class BugsReportedAsserter {

	public void assertNoBugsReported(BugReporter bugReporter) {
		Collection<BugInstance> bugsReported = bugsFrom(bugReporter);
		assertThat(bugsReported.isEmpty(), is(true));
	}

	public void assertBugReported(BugReporter bugReporter) {
		Collection<BugInstance> bugsReported = bugsFrom(bugReporter);
		assertThat(bugsReported.isEmpty(), is(false));
	}

	public void assertBugReported(BugReporter bugReporter, Matcher<BugInstance> matches) {
		Collection<BugInstance> bugsReported = bugsFrom(bugReporter);
		assertThat(bugsReported, hasItem(matches));
	}
	
	public void assertAllBugsReported(BugReporter bugReporter, Matcher<BugInstance>... matches) {
		Collection<BugInstance> bugsReported = bugsFrom(bugReporter);
		assertThat("Expected a certain number of bugs to be reported.", bugsReported.size(), is(matches.length));
		assertThat(bugsReported, hasItems(matches));
	}
	
	private Collection<BugInstance> bugsFrom(BugReporter bugReporter) {
		if(bugReporter instanceof TddBugReporter) {
			return ((TddBugReporter)bugReporter).getReportedBugs();
		} else {
			throw new AssertionError(
					"An invalid BugReporter has been used. " +
					"Please use the BugReporter given by DetectorAssert.bugReporterForTesting(), passing " +
					"the same BugReporter to your custom detector, and any DetectorAssert assertion methods.");
		}
	}
}
