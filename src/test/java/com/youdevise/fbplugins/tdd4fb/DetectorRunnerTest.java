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

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import com.youdevise.fbplugins.tdd4fb.DetectorRunner;
import com.youdevise.fbplugins.tdd4fb.TestingBugReporter;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.Detector2;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;

public class DetectorRunnerTest {

	@Test public void runDetectorOnClassVisitsDetectorWithClassContextOfSpecifiedClass() throws Exception {
		Detector detector = mock(Detector.class);
		BugReporter bugReporter = TestingBugReporter.tddBugReporter();

		DetectorRunner.runDetectorOnClass(detector, DetectorRunner.class, bugReporter);

		verify(detector).visitClassContext(argThat(isClassContextFor("DetectorRunner")));
	}

	@Test public void runDetector2OnClassVisitsDetectorWithClassContextOfSpecifiedClass() throws Exception {
		Detector2 detector = mock(Detector2.class);
		BugReporter bugReporter = TestingBugReporter.tddBugReporter();

		DetectorRunner.runDetectorOnClass(detector, DetectorRunner.class, bugReporter);

		verify(detector).visitClass(argThat(isClassDescriptorFor("DetectorRunner")));
	}

	public static ArgumentMatcher<ClassContext> isClassContextFor(final String simpleClassName) {
		return new ArgumentMatcher<ClassContext>() {
			@Override public boolean matches(Object argument) {
				if (!(argument instanceof ClassContext)) {
					return false;
				} else {
					return ((ClassContext) argument).getClassDescriptor().getSimpleName().equals(simpleClassName);
				}
			}

		};
	}

	private Matcher<ClassDescriptor> isClassDescriptorFor(final String simpleClassName) {
		return new ArgumentMatcher<ClassDescriptor>() {
			@Override public boolean matches(Object argument) {
				if (!(argument instanceof ClassDescriptor)) {
					return false;
				} else {
					return ((ClassDescriptor) argument).getSimpleName().equals(simpleClassName);
				}
			}

		};
	}
}

