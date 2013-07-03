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

import static edu.umd.cs.findbugs.classfile.DescriptorFactory.createClassDescriptorFromDottedClassName;
import static java.lang.System.getProperty;
import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.Detector2;
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
import edu.umd.cs.findbugs.classfile.IAnalysisEngineRegistrar;
import edu.umd.cs.findbugs.classfile.IClassFactory;
import edu.umd.cs.findbugs.classfile.IClassPathBuilder;
import edu.umd.cs.findbugs.classfile.IClassPathBuilderProgress;
import edu.umd.cs.findbugs.classfile.ICodeBase;
import edu.umd.cs.findbugs.classfile.ICodeBaseLocator;
import edu.umd.cs.findbugs.classfile.ResourceNotFoundException;
import edu.umd.cs.findbugs.classfile.engine.bcel.ClassContextClassAnalysisEngine;
import edu.umd.cs.findbugs.classfile.impl.ClassFactory;
import edu.umd.cs.findbugs.classfile.impl.ClassPathImpl;
import edu.umd.cs.findbugs.classfile.impl.DirectoryCodeBase;
import edu.umd.cs.findbugs.classfile.impl.FilesystemCodeBaseLocator;

class DetectorRunner {

	private static final String CODEBASE_DIRECTORY = ".";
	private static final BugReporter STATIC_BUG_REPORTER = TestingBugReporter.tddBugReporter();
	private final AuxCodeBaseLocatorProvider auxCodeBaseLocatorProvider = new AuxCodeBaseLocatorProvider();
	private final List<IAnalysisEngineRegistrar> userDefined = new ArrayList<IAnalysisEngineRegistrar>();

	private static class Singleton {
		public static final DetectorRunner DETECTOR_RUNNER = new DetectorRunner();
	}

	private DetectorRunner() {
		try {
			setUpStaticDependenciesWithinFindBugs(STATIC_BUG_REPORTER);
		} catch (Exception e) {
			throw new RuntimeException("Failed to setup FindBugs dependencies for testing.", e);
		}
	}

	private void setUpStaticDependenciesWithinFindBugs(BugReporter bugReporter) 
			throws CheckedAnalysisException, IOException, InterruptedException {
		bugReporter.setPriorityThreshold(Priorities.LOW_PRIORITY);
		ClassPathImpl classPath = new ClassPathImpl();

		IAnalysisCache analysisCache = ClassFactory.instance().createAnalysisCache(classPath, bugReporter);
		new ClassContextClassAnalysisEngine().registerWith(analysisCache);
		new edu.umd.cs.findbugs.classfile.engine.asm.EngineRegistrar().registerAnalysisEngines(analysisCache);
		new edu.umd.cs.findbugs.classfile.engine.bcel.EngineRegistrar().registerAnalysisEngines(analysisCache);
		new edu.umd.cs.findbugs.classfile.engine.EngineRegistrar().registerAnalysisEngines(analysisCache);
		registUserDefined(analysisCache);

		Global.setAnalysisCacheForCurrentThread(analysisCache);

		ICodeBaseLocator codeBaseLocator = new FilesystemCodeBaseLocator(".");
		ICodeBase codeBase = new DirectoryCodeBase(codeBaseLocator, new File(CODEBASE_DIRECTORY));
		codeBase.setApplicationCodeBase(true);
		classPath.addCodeBase(codeBase);

		addAuxCodeBasesFromClassPath(classPath);

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

	private void registUserDefined(IAnalysisCache analysisCache) {
		for (IAnalysisEngineRegistrar registrar : userDefined) {
			registrar.registerAnalysisEngines(analysisCache);
		}
	}

	static void addRegistarar(IAnalysisEngineRegistrar registrar) {
		Singleton.DETECTOR_RUNNER.userDefined.add(registrar);
	}

	static void clearRegistarar() {
		Singleton.DETECTOR_RUNNER.userDefined.clear();
	}

	private void addAuxCodeBasesFromClassPath(ClassPathImpl classPath) throws IOException, ResourceNotFoundException {
		Iterable<ICodeBaseLocator> codeBaseLocators = auxCodeBaseLocatorProvider.get(classPathEntries());
		for (ICodeBaseLocator auxCodeBaseLocator : codeBaseLocators) {
			ICodeBase auxCodeBase = auxCodeBaseLocator.openCodeBase();
			classPath.addCodeBase(auxCodeBase);
		}
	}

	private Iterable<String> classPathEntries() {
		return asList(getProperty("java.class.path").split(File.pathSeparator));
	}

	private void doRunDetectorOnClass(Detector2 pluginDetector, Class<?> classToTest, BugReporter bugReporter)
			throws CheckedAnalysisException, IOException, InterruptedException {

		String dottedClassName = classToTest.getName();
		ClassDescriptor classDescriptor = createClassDescriptorFromDottedClassName(dottedClassName);
		pluginDetector.visitClass(classDescriptor);
	}

	public static void runDetectorOnClass(Detector2 pluginDetector, Class<?> classToTest, BugReporter bugReporter)
			throws CheckedAnalysisException, IOException, InterruptedException {
		Singleton.DETECTOR_RUNNER.doRunDetectorOnClass(pluginDetector, classToTest, bugReporter);
	}

	public static void runDetectorOnClass(Detector pluginDetector, Class<?> classToTest, BugReporter bugReporter)
			throws CheckedAnalysisException, IOException, InterruptedException {
		Singleton.DETECTOR_RUNNER.doRunDetectorOnClass(new DetectorToDetector2Adapter(pluginDetector), classToTest, bugReporter);
	}
}
