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
		
		public ProjectStats getProjectStats() {
			return projectStats;
		}

		public void reportBug(BugInstance bugInstance) {
			reportedBugs.add(bugInstance);
		}
		
		public Collection<BugInstance> getReportedBugs() {
			return Collections.unmodifiableCollection(reportedBugs);
		}

		public void addObserver(BugReporterObserver observer) { }
		public void finish() { }
		public BugReporter getRealBugReporter() { return this; }
		public void reportQueuedErrors() { }
		public void setErrorVerbosity(int level) { }
		public void setPriorityThreshold(int threshold) { }
		public void logError(String message) { }
		public void logError(String message, Throwable e) { }
		public void reportMissingClass(ClassNotFoundException ex) { }
		public void reportMissingClass(ClassDescriptor classDescriptor) { }
		public void reportSkippedAnalysis(MethodDescriptor method) { }
		public void observeClass(ClassDescriptor classDescriptor) { }
		
	}
	
}
