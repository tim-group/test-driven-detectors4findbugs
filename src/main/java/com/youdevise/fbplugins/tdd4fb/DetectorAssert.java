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

import static com.youdevise.fbplugins.tdd4fb.DetectorRunner.runDetectorOnClass;

import org.hamcrest.Matcher;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.Detector2;
import edu.umd.cs.findbugs.DetectorToDetector2Adapter;
import edu.umd.cs.findbugs.classfile.IAnalysisEngineRegistrar;

public class DetectorAssert {

    private static BugsReportedAsserter asserter = new BugsReportedAsserter();

    public static void assertBugReported(Class<?> classToTest, Detector detector, BugReporter bugReporter)
            throws Exception {
        assertBugReported(classToTest, adapt(detector), bugReporter);
    }

    public static void assertBugReported(Class<?> classToTest, Detector2 detector, BugReporter bugReporter)
            throws Exception {
        runDetectorOnClass(detector, classToTest, bugReporter);
        asserter.assertBugReported(bugReporter);
    }

    public static void assertBugReported(Class<?> classToTest,
                                         Detector detector,
                                         BugReporter bugReporter,
                                         Matcher<BugInstance> bugInstanceMatcher) throws Exception {
        assertBugReported(classToTest, adapt(detector), bugReporter);
    }

    public static void assertBugReported(Class<?> classToTest,
                                         Detector2 detector,
                                         BugReporter bugReporter,
                                         Matcher<BugInstance> bugInstanceMatcher) throws Exception {
        runDetectorOnClass(detector, classToTest, bugReporter);
        asserter.assertBugReported(bugReporter, bugInstanceMatcher);
    }

    public static void assertAllBugsReported(Class<?> classToTest,
                                             Detector detector,
                                             BugReporter bugReporter,
                                             Matcher<BugInstance>... bugInstanceMatchers) throws Exception {
        assertAllBugsReported(classToTest, adapt(detector), bugReporter, bugInstanceMatchers);
    }

    public static void assertAllBugsReported(Class<?> classToTest,
                                             Detector2 detector,
                                             BugReporter bugReporter,
                                             Matcher<BugInstance>... bugInstanceMatchers) throws Exception {
        runDetectorOnClass(detector, classToTest, bugReporter);
        asserter.assertAllBugsReported(bugReporter, bugInstanceMatchers);
    }

    public static void assertNoBugsReported(Class<?> classToTest, Detector detector, BugReporter bugReporter)
            throws Exception {
        assertNoBugsReported(classToTest, adapt(detector), bugReporter);
    }

    public static void assertNoBugsReported(Class<?> classToTest, Detector2 detector, BugReporter bugReporter)
            throws Exception {
        runDetectorOnClass(detector, classToTest, bugReporter);
        asserter.assertNoBugsReported(bugReporter);
    }

    public static BugReporter bugReporterForTesting() {
        return TestingBugReporter.tddBugReporter();
    }

    public static Matcher<BugInstance> ofType(String type) {
        return FindBugsMatchers.ofType(type);
    }

    public static void addRegistrar(IAnalysisEngineRegistrar registrar) {
        DetectorRunner.addRegistrar(registrar);
    }

    public static void clearRegistrar() {
        DetectorRunner.clearRegistrar();
    }

    private static Detector2 adapt(Detector detector) {
        return new DetectorToDetector2Adapter(detector);
    }

}
