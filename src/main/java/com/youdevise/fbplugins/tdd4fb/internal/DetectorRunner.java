/*
 * FindBugs4JUnit. Copyright (c) 2011 youDevise, Ltd.
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
package com.youdevise.fbplugins.tdd4fb.internal;

import static edu.umd.cs.findbugs.classfile.DescriptorFactory.createClassDescriptorFromDottedClassName;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.DetectorToDetector2Adapter;
import edu.umd.cs.findbugs.NoOpFindBugsProgress;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.ba.AnalysisCacheToAnalysisContextAdapter;
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.ba.FieldSummary;
import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.Global;
import edu.umd.cs.findbugs.classfile.IAnalysisCache;
import edu.umd.cs.findbugs.classfile.IClassFactory;
import edu.umd.cs.findbugs.classfile.IClassPathBuilder;
import edu.umd.cs.findbugs.classfile.IClassPathBuilderProgress;
import edu.umd.cs.findbugs.classfile.ICodeBase;
import edu.umd.cs.findbugs.classfile.ICodeBaseLocator;
import edu.umd.cs.findbugs.classfile.engine.bcel.ClassContextClassAnalysisEngine;
import edu.umd.cs.findbugs.classfile.impl.ClassFactory;
import edu.umd.cs.findbugs.classfile.impl.ClassPathImpl;
import edu.umd.cs.findbugs.classfile.impl.DirectoryCodeBase;
import edu.umd.cs.findbugs.classfile.impl.FilesystemCodeBaseLocator;

public class DetectorRunner {

	private static final String CODEBASE_DIRECTORY = ".";
	private static final BugReporter STATIC_BUG_REPORTER = TestingBugReporter.tddBugReporter();

	private DetectorRunner() {
		try {
			setUpStaticDependenciesWithinFindBugs(STATIC_BUG_REPORTER);
		} catch (Exception e) {
			throw new RuntimeException("Failed to setup FindBugs dependencies for testing.", e);
		}
	}

	private void setUpStaticDependenciesWithinFindBugs(BugReporter bugReporter) throws CheckedAnalysisException,
			IOException, InterruptedException {
		bugReporter.setPriorityThreshold(Priorities.LOW_PRIORITY);
		ClassPathImpl classPath = new ClassPathImpl();
		ICodeBaseLocator codeBaseLocator = new FilesystemCodeBaseLocator(".");
		ICodeBase codeBase = new DirectoryCodeBase(codeBaseLocator, new File(CODEBASE_DIRECTORY));
		codeBase.setApplicationCodeBase(true);
		classPath.addCodeBase(codeBase);

		IAnalysisCache analysisCache = ClassFactory.instance().createAnalysisCache(classPath, bugReporter);
		new ClassContextClassAnalysisEngine().registerWith(analysisCache);
		new edu.umd.cs.findbugs.classfile.engine.asm.EngineRegistrar().registerAnalysisEngines(analysisCache);
		new edu.umd.cs.findbugs.classfile.engine.bcel.EngineRegistrar().registerAnalysisEngines(analysisCache);
		new edu.umd.cs.findbugs.classfile.engine.EngineRegistrar().registerAnalysisEngines(analysisCache);

		Global.setAnalysisCacheForCurrentThread(analysisCache);

		IClassFactory classFactory = ClassFactory.instance();
		IClassPathBuilder builder = classFactory.createClassPathBuilder(bugReporter);
		builder.addCodeBase(codeBaseLocator, true);
		builder.scanNestedArchives(true);
		IClassPathBuilderProgress progress = new NoOpFindBugsProgress();
		builder.build(classPath, progress);
		List<ClassDescriptor> appClassList = builder.getAppClassList();

		AnalysisCacheToAnalysisContextAdapter analysisContext = new AnalysisCacheToAnalysisContextAdapter();
		AnalysisContext.setCurrentAnalysisContext(analysisContext);
		analysisContext.setAppClassList(appClassList);
		analysisContext.setFieldSummary(new FieldSummary());
	}

	private void doRunDetectorOnClass(Detector pluginDetector, Class<?> classToTest, BugReporter bugReporter)
			throws CheckedAnalysisException, IOException, InterruptedException {

		DetectorToDetector2Adapter adapter = new DetectorToDetector2Adapter(pluginDetector);

		String dottedClassName = classToTest.getName();
		ClassDescriptor classDescriptor = createClassDescriptorFromDottedClassName(dottedClassName);
		adapter.visitClass(classDescriptor);
	}
	
	private static class Singleton {
		public static final DetectorRunner DETECTOR_RUNNER = new DetectorRunner();
	}

	public static void runDetectorOnClass(Detector pluginDetector, Class<?> classToTest, BugReporter bugReporter)
			throws CheckedAnalysisException, IOException, InterruptedException {
		Singleton.DETECTOR_RUNNER.doRunDetectorOnClass(pluginDetector, classToTest, bugReporter);
	}


}
