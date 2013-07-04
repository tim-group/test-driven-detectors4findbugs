# Test Driven Detectors 4 FindBugs #

[![Build Status](https://travis-ci.org/youdevise/test-driven-detectors4findbugs.png)](https://travis-ci.org/youdevise/test-driven-detectors4findbugs)

Utility for test driving development of [FindBugs](http://findbugs.sourceforge.net/) plugins. Allows writing unit tests which run your custom detector against a given class, and assert on bugs, if any, reported by the detector. Copyright 2010 [youDevise, Ltd.](http://www.youdevise.com).

# Installation #

Add the JAR (available from the Downloads section) as a dependency for use in unit tests. The dependency can also be listed in Maven compatible build tools with the following information:

<table>
    <tr>
        <td>groupId</td>
        <td>artifactId</td>
        <td>version</td>
        <td>scope</td>
    </tr>
    <tr>
        <td>com.youdevise</td>
        <td>test-driven-detectors4findbugs</td>
        <td>0.2.1</td>
        <td>test</td>
    </tr>
</table>



test-driven-detectors4findbugs depends on [Hamcrest](http://code.google.com/p/hamcrest/) 1.1+, and FindBugs 1.3.9.

Dependency on 1.3.9 version of FindBugs is only for testing, custom detectors which pass tests with 1.3.9 have been verified to work from version 1.3.7 of FindBugs.

As of the next release, FindBugs releases earlier than 1.3.9 will not be supported. Support will range from 1.3.9 to the latest stable release of the 2.x branch.

# How To Use

An example would best illustrate how to use test your custom detectors.

Consider that you want to write a FindBugs plugin which will report a bug against classes whose type names are too long (not that you'd want to do that, of course).

Start with two code examples (or 'benchmarks'), one which you wish to report a bug for, and one which would you don't.

E.g:

    public class ExampleClassWithAnAllowedName {
    
    }
    
    public class ExampleClassWithANameThatIsTooLongForThisSillyDetector {
    
    }
    
Next write the template for your custom FindBugs detector:

    import edu.umd.cs.findbugs.BugInstance;
    import edu.umd.cs.findbugs.BugReporter;
    import edu.umd.cs.findbugs.Detector;
    import edu.umd.cs.findbugs.ba.ClassContext;
    
    public class CustomClassNameLengthDetector implements Detector {
    	
    	private final BugReporter bugReporter;
    
        // Every custom detector requires a constructor with this signature.
    	public CustomClassNameLengthDetector(BugReporter bugReporter) {
    		this.bugReporter = bugReporter;
    	}
    	
    	@Override public void report() { }
    
    	@Override public void visitClassContext(ClassContext classContext) { }

    }
    

Now write a failing test to develop against:

    import static com.youdevise.fbplugins.tdd4fb.DetectorAssert.ofType;
    import com.youdevise.fbplugins.tdd4fb.DetectorAssert;

    import org.junit.Test;
    
    import edu.umd.cs.findbugs.BugReporter;
    
    public class CustomClassNameLengthDetectorTest {
    
    	@Test public void
    	raisesAnyBugAgainstClassWithLongName() throws Exception {
    	
    	    // Must obtain a BugReporter instance from this method
    		BugReporter bugReporter = DetectorAssert.bugReporterForTesting();
    		
    		// And pass the same BugReporter to your detector
    		CustomClassNameLengthDetector detector = new CustomClassNameLengthDetector(bugReporter);
    		
    		// Next assert that your detector has raised a bug against a specific class
    		DetectorAssert.assertBugReported(ExampleClassWithANameThatIsTooLongForThisSillyDetector.class, 
    										 detector, 
    										 bugReporter);
    	}
    }
    
Since the detector never reports a bug, this predictably fails. You can now begin to develop your custom detector with the tight feedback loop of test-driven development!

All assertion methods, and other functionality is accessed through the static methods of DetectorAssert.

# Building from source #

The project can be built with [Maven](http://maven.apache.org/).

Run 'mvn package' to build the JAR.

# License #

Open source under the very permissive [MIT license](https://github.com/youdevise/test-driven-detectors4findbugs/blob/master/LICENSE).

# Acknowledgements #

A project of [youDevise](https://dev.youdevise.com). We're [hiring](http://www.youdevise.com/careers)!


