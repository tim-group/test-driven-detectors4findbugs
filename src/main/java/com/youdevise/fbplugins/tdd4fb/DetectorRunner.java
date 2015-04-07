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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.Detector2;
import edu.umd.cs.findbugs.DetectorToDetector2Adapter;
import edu.umd.cs.findbugs.NoOpFindBugsProgress;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.ba.FieldSummary;
import edu.umd.cs.findbugs.ba.XClass;
import edu.umd.cs.findbugs.ba.XFactory;
import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.DescriptorFactory;
import edu.umd.cs.findbugs.classfile.Global;
import edu.umd.cs.findbugs.classfile.IAnalysisCache;
import edu.umd.cs.findbugs.classfile.IAnalysisEngineRegistrar;
import edu.umd.cs.findbugs.classfile.IClassFactory;
import edu.umd.cs.findbugs.classfile.IClassPathBuilder;
import edu.umd.cs.findbugs.classfile.IClassPathBuilderProgress;
import edu.umd.cs.findbugs.classfile.ICodeBase;
import edu.umd.cs.findbugs.classfile.ICodeBaseLocator;
import edu.umd.cs.findbugs.classfile.MissingClassException;
import edu.umd.cs.findbugs.classfile.engine.bcel.ClassContextClassAnalysisEngine;
import edu.umd.cs.findbugs.classfile.impl.ClassFactory;
import edu.umd.cs.findbugs.classfile.impl.ClassPathImpl;
import edu.umd.cs.findbugs.classfile.impl.DirectoryCodeBase;
import edu.umd.cs.findbugs.classfile.impl.FilesystemCodeBaseLocator;

class DetectorRunner {

    private static class Singleton {
        static final String CODEBASE_DIRECTORY = ".";
        static final List<IAnalysisEngineRegistrar> USER_DEFINED_ENGINE_REGISTRARS = new CopyOnWriteArrayList<IAnalysisEngineRegistrar>();
        static final AuxCodeBaseLocatorProvider AUX_CODEBASE_LOCATOR_PROVIDER = new AuxCodeBaseLocatorProvider();
        static final BugReporter STATIC_BUG_REPORTER = TestingBugReporter.tddBugReporter();
        static final InitialisationResult INITIALISATION_RESULT = attemptSetup();

        
        private static InitialisationResult attemptSetup() {
            try {
                setUpStaticDependenciesWithinFindBugs(STATIC_BUG_REPORTER);
                return new InitialisationResult(null);
            } catch (Exception e) {
                return new InitialisationResult(e);
            }
        }

        /**
         * Build a project instance.
         *
         * TODO: Any further initialization?
         *
         * @return A project instance.
         */
        private static Project buildProject() {
            return new Project();
        }

        private static void setUpStaticDependenciesWithinFindBugs(BugReporter bugReporter) throws Exception {
            bugReporter.setPriorityThreshold(Priorities.LOW_PRIORITY);
            ClassPathImpl classPath = new ClassPathImpl();

            IAnalysisCache analysisCache = ClassFactory.instance().createAnalysisCache(classPath, bugReporter);
            new ClassContextClassAnalysisEngine().registerWith(analysisCache);
            new edu.umd.cs.findbugs.classfile.engine.asm.EngineRegistrar().registerAnalysisEngines(analysisCache);
            new edu.umd.cs.findbugs.classfile.engine.bcel.EngineRegistrar().registerAnalysisEngines(analysisCache);
            new edu.umd.cs.findbugs.classfile.engine.EngineRegistrar().registerAnalysisEngines(analysisCache);
            registerUserDefined(analysisCache);

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

            Project project = buildProject();

            AnalysisContext analysisContext = new AnalysisContext(project);
            AnalysisContext.setCurrentAnalysisContext(analysisContext);
            analysisContext.setAppClassList(appClassList);
            analysisContext.setFieldSummary(new FieldSummary());
            
            Collection<ClassDescriptor> buildReferencedClassSet = buildReferencedClassSet(appClassList);
            internXClasses(buildReferencedClassSet);
        }

        private static void registerUserDefined(IAnalysisCache analysisCache) {
            for (IAnalysisEngineRegistrar registrar : USER_DEFINED_ENGINE_REGISTRARS) {
                registrar.registerAnalysisEngines(analysisCache);
            }
        }
        
        private static void addAuxCodeBasesFromClassPath(ClassPathImpl classPath) throws Exception {
            Iterable<ICodeBaseLocator> codeBaseLocators = AUX_CODEBASE_LOCATOR_PROVIDER.get(classPathEntries());
            for (ICodeBaseLocator auxCodeBaseLocator : codeBaseLocators) {
                ICodeBase auxCodeBase = auxCodeBaseLocator.openCodeBase();
                classPath.addCodeBase(auxCodeBase);
            }
        }

        private static Iterable<String> classPathEntries() {
            return asList(getProperty("java.class.path").split(File.pathSeparator));
        }
        
        private static Collection<ClassDescriptor> buildReferencedClassSet(List<ClassDescriptor> appClassList) throws CheckedAnalysisException, InterruptedException {
            Set<String> referencedPackageSet = new HashSet<String>();

            LinkedList<ClassDescriptor> workList = new LinkedList<ClassDescriptor>();
            workList.addAll(appClassList);

            Set<ClassDescriptor> seen = new HashSet<ClassDescriptor>();
            Set<ClassDescriptor> appClassSet = new HashSet<ClassDescriptor>(appClassList);

            Set<ReferencedClassException> badAppClassExceptions = new HashSet<ReferencedClassException>();
            Set<ClassDescriptor> badAppClasses = new HashSet<ClassDescriptor>();
            Set<ClassDescriptor> addedToWorkList = new HashSet<ClassDescriptor>(appClassList);

            while (!workList.isEmpty()) {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                ClassDescriptor classDesc = workList.removeFirst();

                if (seen.contains(classDesc)) {
                    continue;
                }
                seen.add(classDesc);

                referencedPackageSet.add(classDesc.getPackageName());

                // Get list of referenced classes and add them to set.
                // Add superclasses and superinterfaces to worklist.
                try {
                    XClass classNameAndInfo = Global.getAnalysisCache().getClassAnalysis(XClass.class, classDesc);

                    ClassDescriptor superclassDescriptor = classNameAndInfo.getSuperclassDescriptor();
                    if (superclassDescriptor != null && addedToWorkList.add(superclassDescriptor)) {
                        workList.addLast(superclassDescriptor);
                    }

                    for (ClassDescriptor ifaceDesc : classNameAndInfo.getInterfaceDescriptorList()) {
                        if (addedToWorkList.add(ifaceDesc))
                            workList.addLast(ifaceDesc);
                    }

                    ClassDescriptor enclosingClass = classNameAndInfo.getImmediateEnclosingClass();
                    if (enclosingClass != null && addedToWorkList.add(enclosingClass))
                        workList.addLast(enclosingClass);

                } catch (RuntimeException e) {
                    if (appClassSet.contains(classDesc)) {
                        badAppClasses.add(classDesc);
                        badAppClassExceptions.add(new ReferencedClassException(classDesc, e));
                    }
                } catch (MissingClassException e) {
                    if (appClassSet.contains(classDesc)) {
                        badAppClasses.add(classDesc);
                        badAppClassExceptions.add(new ReferencedClassException(classDesc, e));
                    }
                } catch (CheckedAnalysisException e) {
                    if (appClassSet.contains(classDesc)) {
                        badAppClasses.add(classDesc);
                        badAppClassExceptions.add(new ReferencedClassException(classDesc, e));
                    }
                }
            }
            
            if (!badAppClassExceptions.isEmpty()) {
                for (ReferencedClassException exception : badAppClassExceptions) {
                    exception.printStackTrace();
                }
                throw new RuntimeException("Error scanning classes. See System.err for details.");
            }
            
            appClassList.removeAll(badAppClasses);
            DescriptorFactory.instance().purge(badAppClasses);

            for (ClassDescriptor d : DescriptorFactory.instance().getAllClassDescriptors()) {
                referencedPackageSet.add(d.getPackageName());
            }
            return new ArrayList<ClassDescriptor>(DescriptorFactory.instance().getAllClassDescriptors());
        }
        
        private static void internXClasses(Collection<ClassDescriptor> referencedClassSet) throws InterruptedException {
            AnalysisContext.currentXFactory().canonicalizeAll();
            XFactory factory = AnalysisContext.currentXFactory();
            Collection<ClassDescriptor> badClasses = new LinkedList<ClassDescriptor>();
            for (ClassDescriptor desc : referencedClassSet) {
                try {
                    XClass info = Global.getAnalysisCache().getClassAnalysis(XClass.class, desc);
                    factory.intern(info);
                } catch (CheckedAnalysisException e) {
                    AnalysisContext.logError("Couldn't get class info for " + desc, e);
                    badClasses.add(desc);
                } catch (RuntimeException e) {
                    AnalysisContext.logError("Couldn't get class info for " + desc, e);
                    badClasses.add(desc);
                }
            }
            if (!badClasses.isEmpty()) {
                referencedClassSet = new LinkedHashSet<ClassDescriptor>(referencedClassSet);
                referencedClassSet.removeAll(badClasses);
            }
        }


        private static void doRunDetectorOnClass(Detector2 pluginDetector, Class<?> classToTest, BugReporter bugReporter) throws Exception {
            String dottedClassName = classToTest.getName();
            ClassDescriptor classDescriptor = createClassDescriptorFromDottedClassName(dottedClassName);
            pluginDetector.visitClass(classDescriptor);
        }
    }
    
    private static class ReferencedClassException extends RuntimeException {
        public ReferencedClassException(ClassDescriptor classDesc, Throwable cause) {
            super("Error scanning the following class: " + classDesc, cause);
        }
    }
    
    static class InitialisationResult {
        final boolean successful;
        final Throwable exceptionIfFailed;
        
        public InitialisationResult(Throwable exceptionIfFailed) {
            this.successful = exceptionIfFailed == null;
            this.exceptionIfFailed = exceptionIfFailed;
        }
    }
    
    public void assertInitialised() throws IllegalStateException {
        if (!Singleton.INITIALISATION_RESULT.successful) {
            throw new RuntimeException("tdd4fb failed to initialise correctly. No methods can be used until the underlying cause is fixed.",
                                       Singleton.INITIALISATION_RESULT.exceptionIfFailed);
        }
    }

    void addRegistrar(IAnalysisEngineRegistrar registrar) {
        assertInitialised();
        Singleton.USER_DEFINED_ENGINE_REGISTRARS.add(registrar);
    }

    void clearRegistrar() {
        assertInitialised();
        Singleton.USER_DEFINED_ENGINE_REGISTRARS.clear();
    }

    public void runDetectorOnClass(Detector2 pluginDetector, Class<?> classToTest, BugReporter bugReporter) throws Exception {
        assertInitialised();
        Singleton.doRunDetectorOnClass(pluginDetector, classToTest, bugReporter);
    }

    public void runDetectorOnClass(Detector pluginDetector, Class<?> classToTest, BugReporter bugReporter) throws Exception {
        assertInitialised();
        Singleton.doRunDetectorOnClass(new DetectorToDetector2Adapter(pluginDetector), classToTest, bugReporter);
    }
}
