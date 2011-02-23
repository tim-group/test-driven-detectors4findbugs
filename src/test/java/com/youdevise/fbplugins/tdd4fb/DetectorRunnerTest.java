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

import static com.youdevise.fbplugins.tdd4fb.DetectorRunnerTest.ClassContextArgumentMatcher.isClassContextFor;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.ArgumentMatcher;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.ProjectStats;
import edu.umd.cs.findbugs.ba.ClassContext;


public class DetectorRunnerTest {

	
	@Test public void
	runDetectorOnClassVisitsDetectorWithClassContextOfSpecifiedClass() throws Exception {
		Detector detector = mock(Detector.class); 
		BugReporter bugReporter = mock(BugReporter.class);
        when(bugReporter.getProjectStats()).thenReturn(new ProjectStats());
		
		DetectorRunner.runDetectorOnClass(detector, DetectorRunner.class, bugReporter);
		
		verify(detector).visitClassContext(argThat(isClassContextFor("DetectorRunner")));
		
	}

	public static class ClassContextArgumentMatcher extends ArgumentMatcher<ClassContext> {
		
		private final String simpleClassName;

		public ClassContextArgumentMatcher(String simpleClassName) {
			this.simpleClassName = simpleClassName;
		}

		public static ClassContextArgumentMatcher isClassContextFor(String simpleClassName) {
			return new ClassContextArgumentMatcher(simpleClassName);
		}

		@Override public boolean matches(Object argument) {
			if(!(argument instanceof ClassContext)) {
				return false;
			} else {
				return ((ClassContext)argument).getClassDescriptor().getSimpleName().equals(simpleClassName);
			}
		}
		
	}
}
