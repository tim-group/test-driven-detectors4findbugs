/*
 * test-driven-detectors4findbugs. Copyright (c) 2011 youDevise, Ltd.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
*/

package com.youdevise.fbplugins.tdd4fb;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;

import com.youdevise.fbplugins.tdd4fb.BugsReportedAsserter;
import com.youdevise.fbplugins.tdd4fb.TestingBugReporter;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Priorities;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.iterableWithSize;

public class DetectorAssertTest {
	
	private BugReporter bugReporter;
	private BugInstance bugInstance;
	private BugsReportedAsserter asserter;
	
	@Before
	public void setUp() {
		bugReporter = TestingBugReporter.tddBugReporter();
		bugInstance = new BugInstance("UUF_UNUSED_FIELD", Priorities.LOW_PRIORITY);
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

	@Test public void
	assertBugsReportedPassesTestWhenAnyBugIsReported() {
		bugReporter.reportBug(bugInstance);
		asserter.assertBugsReported(bugReporter, allOf(iterableWithSize(greaterThan(0))));
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
	
	@SuppressWarnings("unchecked")
	@Test public void
	assertBugsReportedWithManyMatchersPassesWhenNoMatchersFail() {
		bugReporter.reportBug(bugInstance);
		bugReporter.reportBug(bugInstance);
		bugReporter.reportBug(bugInstance);
		
		asserter.assertAllBugsReported(bugReporter, alwaysMatch(true), alwaysMatch(true), alwaysMatch(true));
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=AssertionError.class) public void
	assertBugsReportedWithManyMatchersFailsWhenThereAreMoreMatchersThanBugs() {
		bugReporter.reportBug(bugInstance);
		bugReporter.reportBug(bugInstance);
		
		asserter.assertAllBugsReported(bugReporter, alwaysMatch(true), alwaysMatch(true), alwaysMatch(true));
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=AssertionError.class) public void
	assertBugsReportedWithManyMatchersFailsWhenThereAreMoreBugsThanMatchers() {
		bugReporter.reportBug(bugInstance);
		bugReporter.reportBug(bugInstance);
		bugReporter.reportBug(bugInstance);
		
		asserter.assertAllBugsReported(bugReporter, alwaysMatch(true), alwaysMatch(true));
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=AssertionError.class) public void
	assertBugsReportedWithManyMatchersFailsWhenOneMatcherFails() {
		bugReporter.reportBug(bugInstance);
		bugReporter.reportBug(bugInstance);
		bugReporter.reportBug(bugInstance);
		
		asserter.assertAllBugsReported(bugReporter, alwaysMatch(true), alwaysMatch(true), alwaysMatch(false));
	}
	
	
	private BaseMatcher<BugInstance> alwaysMatch(final boolean alwaysMatch) {
		return new BaseMatcher<BugInstance>() {
			public boolean matches(Object item) { return alwaysMatch; }
			public void describeTo(Description description) { }
		};
	}

}
