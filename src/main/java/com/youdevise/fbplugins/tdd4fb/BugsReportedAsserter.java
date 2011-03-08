/*
 * test-driven-detectors4findbugs. Copyright (c) 2011 youDevise, Ltd.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.youdevise.fbplugins.tdd4fb;

import static org.hamcrest.CoreMatchers.is;

import java.util.Collection;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

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
		if (bugReporter instanceof TddBugReporter) {
			return ((TddBugReporter) bugReporter).getReportedBugs();
		} else {
			throw new DetectorAssertException("An invalid BugReporter has been used. "
					+ "Please use the BugReporter given by DetectorAssert.bugReporterForTesting(), passing "
					+ "the same BugReporter to your custom detector, and any DetectorAssert assertion methods.");
		}
	}

	private Matcher<Iterable<BugInstance>> hasItems(Matcher<BugInstance>... items) {
		return CoreMatchers.hasItems(items);
	}

	private Matcher<Iterable<? super BugInstance>> hasItem(Matcher<BugInstance> item) {
		return CoreMatchers.hasItem(item);
	}

	/*
	 * Replace with MatcherAssert.assertThat() if/when only supporting Hamcrest 1.2+
	 */
	private static <T> void assertThat(String reason, T actual, Matcher<? super T> matcher) {
		if (!matcher.matches(actual)) {
			Description description = new StringDescription();
			description.appendText(reason).appendText("\nExpected: ").appendDescriptionOf(matcher).appendText(
					"\n     but: ");
			matcher.describeMismatch(actual, description);

			throw new DetectorAssertException(description.toString());
		}
	}

	private static <T> void assertThat(T actual, Matcher<? super T> matcher) {
		assertThat("", actual, matcher);
	}
}
