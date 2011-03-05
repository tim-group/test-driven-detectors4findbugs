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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BugReporterObserver;
import edu.umd.cs.findbugs.ProjectStats;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;

class TestingBugReporter {

	public static BugReporter tddBugReporter() {
		return new TddBugReporter();
	}
	
	static class TddBugReporter implements BugReporter {

		private ProjectStats projectStats = new ProjectStats();
		private Collection<BugInstance> reportedBugs = new ArrayList<BugInstance>();
		
		@Override public ProjectStats getProjectStats() {
			return projectStats;
		}

		@Override public void reportBug(BugInstance bugInstance) {
			reportedBugs.add(bugInstance);
		}
		
		public Collection<BugInstance> getReportedBugs() {
			return Collections.unmodifiableCollection(reportedBugs);
		}

		@Override public void addObserver(BugReporterObserver observer) { }
		@Override public void finish() { }
		@Override public BugReporter getRealBugReporter() { return this; }
		@Override public void reportQueuedErrors() { }
		@Override public void setErrorVerbosity(int level) { }
		@Override public void setPriorityThreshold(int threshold) { }
		@Override public void logError(String message) { }
		@Override public void logError(String message, Throwable e) { }
		@Override public void reportMissingClass(ClassNotFoundException ex) { }
		@Override public void reportMissingClass(ClassDescriptor classDescriptor) { }
		@Override public void reportSkippedAnalysis(MethodDescriptor method) { }
		@Override public void observeClass(ClassDescriptor classDescriptor) { }
		
	}
	
}
