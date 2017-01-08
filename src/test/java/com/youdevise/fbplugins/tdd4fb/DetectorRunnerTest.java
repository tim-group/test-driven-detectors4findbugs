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

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.Detector2;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.XClass;
import edu.umd.cs.findbugs.ba.XFactory;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.DescriptorFactory;
import edu.umd.cs.findbugs.classfile.Global;
import edu.umd.cs.findbugs.classfile.analysis.ClassInfo;
import edu.umd.cs.findbugs.classfile.analysis.MethodInfo;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import static com.youdevise.fbplugins.tdd4fb.TestingBugReporter.tddBugReporter;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class DetectorRunnerTest {

	private final Detector detector = mock(Detector.class);
	private final Detector2 detector2 = mock(Detector2.class);
    private final BugReporter bugReporter = tddBugReporter();

    
    @Test public void runDetectorOnClassVisitsDetectorWithClassContextOfSpecifiedClass() throws Exception {
		new DetectorRunner().runDetectorOnClass(detector, DetectorRunner.class, bugReporter);

		verify(detector).visitClassContext(argThat(isClassContextFor("DetectorRunner")));
	}

	@Test public void runDetector2OnClassVisitsDetectorWithClassContextOfSpecifiedClass() throws Exception {
		new DetectorRunner().runDetectorOnClass(detector2, DetectorRunner.class, bugReporter);

		verify(detector2).visitClass(argThat(isClassDescriptorFor("DetectorRunner")));
	}
	

	public static ArgumentMatcher<ClassContext> isClassContextFor(final String simpleClassName) {
		return new ArgumentMatcher<ClassContext>() {
			@Override
            public boolean matches(ClassContext argument) {
                return argument.getClassDescriptor().getSimpleName().equals(simpleClassName);
			}

		};
	}

	private ArgumentMatcher<ClassDescriptor> isClassDescriptorFor(final String simpleClassName) {
		return new ArgumentMatcher<ClassDescriptor>() {
			@Override public boolean matches(ClassDescriptor argument) {
                return argument.getSimpleName().equals(simpleClassName);
			}
		};
	}
	
    @Test 
    public void canRetrieveXClassInfoForAnApplicationClass() throws Exception {
        BugReporter bugReporter = DetectorAssert.bugReporterForTesting();
        MyDetector myDetector = new MyDetector();
        DetectorAssert.assertNoBugsReported(SomeClassOfMine.class, myDetector, bugReporter);
        
        assertThat(myDetector.xMethod, instanceOf(MethodInfo.class));
        assertTrue(myDetector.xMethod.isSynchronized());
        
    }
    
    @Test
    public void canRetrieveSuperclassInfoAsXClass() throws Exception {
        BugReporter bugReporter = DetectorAssert.bugReporterForTesting();
        MyDetector myDetector = new MyDetector();
        DetectorAssert.assertNoBugsReported(SomeClassOfMine.class, myDetector, bugReporter);
        
        assertThat(myDetector.xSuperclass, instanceOf(ClassInfo.class));
        assertThat(asList(myDetector.xSuperclass.getInterfaceDescriptorList()), hasSize(1));
        assertThat(asList(myDetector.xSuperclass.getInterfaceDescriptorList()).get(0), equalTo(DescriptorFactory.createClassDescriptor(MarkerInterface.class)));
    }
    
    public static interface MarkerInterface { }
    
    public static class SuperClass implements MarkerInterface {
        public void superMethod() { 
        }
    }
    
    public static class SomeClassOfMine extends SuperClass {
        public synchronized void aMethod() {}
    }
    
    public static final class MyDetector implements Detector {

        private XMethod xMethod;
        private XClass xSuperclass;

        public void report() { }

        public void visitClassContext(ClassContext classContext) {
            xMethod = XFactory.createXMethod(DescriptorFactory.instance().getMethodDescriptor(
                "com/youdevise/fbplugins/tdd4fb/DetectorRunnerTest$SomeClassOfMine", 
                "aMethod", 
                "()V", 
                false));

            try {
                xSuperclass = Global.getAnalysisCache().getClassAnalysis(XClass.class, classContext.getXClass().getSuperclassDescriptor());
            } catch (CheckedAnalysisException e) {
                throw new RuntimeException(e);
            }
        }


    }

}

