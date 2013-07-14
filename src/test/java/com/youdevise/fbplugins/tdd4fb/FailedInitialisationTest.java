package com.youdevise.fbplugins.tdd4fb;

import static com.youdevise.fbplugins.tdd4fb.TestingBugReporter.tddBugReporter;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.Callable;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import com.youdevise.fbplugins.tdd4fb.DetectorRunner.InitialisationResult;

import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.Detector2;

@RunWith(Theories.class)
public class FailedInitialisationTest {
    
    @DataPoints public static Callable<?>[] methodsWhichShouldCheckInitialisationResult = new Callable<?>[] {
        new Callable<Void>() {
            public Void call() throws Exception {
                new DetectorRunner().runDetectorOnClass(mock(Detector.class), DetectorRunner.class, tddBugReporter());
                return null;
            }
        },
        new Callable<Void>() {
            public Void call() throws Exception {
                new DetectorRunner().runDetectorOnClass(mock(Detector2.class), DetectorRunner.class, tddBugReporter());
                return null;
            }
        },
        new Callable<Void>() {
            public Void call() throws Exception {
                new DetectorRunner().addRegistrar(null);
                return null;
            }
        },
        new Callable<Void>() {
            public Void call() throws Exception {
                new DetectorRunner().clearRegistrar();
                return null;
            }
        },
        new Callable<Void>() {
            public Void call() throws Exception {
                DetectorAssert.bugReporterForTesting();
                return null;
            }
        },
    };
    
    @Before public void grabRealInitialisationResult() throws Exception {
        actualInitialisationResult = getRealInitialisationResult();
    }
    
    @After public void reinstateRealInitialisationResult() throws Exception {
        setFinalStatic(getInitialisationResultField(), actualInitialisationResult);
    }
    
    
    @Theory public void checksInitialisationResultAndRethrowsErrorsIfSetupWasUnsuccessful(Callable<?> methodWhichShouldCheckInitialisation) throws Exception {
        IOException expectedCause = new IOException("something bad happened");
        horribleHackToTestInitialisationResult(expectedCause);
        
        try {
            methodWhichShouldCheckInitialisation.call();
            fail("Should have thrown an exception");
        } catch (Exception e) {
            assertThat(e, instanceOf(RuntimeException.class));
            assertThat(e.getMessage(), is("tdd4fb failed to initialise correctly. No methods can be used until the underlying cause is fixed."));
            assertThat(e.getCause(), Matchers.<Throwable>sameInstance(expectedCause));
        }
    }
    
    
    private InitialisationResult actualInitialisationResult;
    
    private void horribleHackToTestInitialisationResult(Exception initialisationExceptionIfFailed) throws Exception {
        setFinalStatic(getInitialisationResultField(), new InitialisationResult(initialisationExceptionIfFailed));
    }

    private Field getInitialisationResultField() throws Exception {
        Class<?> singletonClass = null;
        for (Class<?> innerClass : DetectorRunner.class.getDeclaredClasses()) {
            if (innerClass.getName().endsWith("$Singleton")) {
                singletonClass = innerClass;
            }
        }
        
        if (singletonClass == null) { 
            throw new IllegalArgumentException("Need to be able to find the Singleton class within DetectorRunner"); 
        }
        
        return singletonClass.getDeclaredField("INITIALISATION_RESULT");
    }
    
    private InitialisationResult getRealInitialisationResult() throws Exception {
        return (InitialisationResult) getInitialisationResultField().get(null);
    }

    // http://stackoverflow.com/a/3301720/4120
    private void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    @Test public void
    ensuresDetectorRunnerIsCorrectlyInitialisedBeforeCreatingABugReporter() {
        
    }
    
}
