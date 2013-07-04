package com.youdevise.fbplugins.tdd4fb;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import com.youdevise.fbplugins.tdd4fb.TestingBugReporter.TddBugReporter;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.ProjectStats;

public class TestingBugReporterTest {

	@Test
	public void recordsProjectStats() {
		assertThat(TestingBugReporter.tddBugReporter().getProjectStats(), is(instanceOf(ProjectStats.class)));
	}
	
	@Test
	public void accumulatesReportedBugs() {
		BugInstance first = new BugInstance("UUF_UNUSED_FIELD", Priorities.LOW_PRIORITY).addClass(getClass().getName());
		BugInstance second = new BugInstance("UUF_UNUSED_FIELD", Priorities.NORMAL_PRIORITY).addClass(getClass().getName());
		BugInstance third = new BugInstance("UUF_UNUSED_FIELD", Priorities.HIGH_PRIORITY).addClass(getClass().getName());
		
		TddBugReporter tddBugReporter = (TddBugReporter) TestingBugReporter.tddBugReporter();
		
		tddBugReporter.reportBug(first);
		tddBugReporter.reportBug(second);
		tddBugReporter.reportBug(third);
		
		assertThat(tddBugReporter.getBugCollection().getCollection(), hasItems(first, second, third));
		assertThat(tddBugReporter.getReportedBugs(), hasItems(first, second, third));
		
	}
	
}
