/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package com.youdevise.fbplugins.tdd4fb.example;

import static com.youdevise.fbplugins.tdd4fb.internal.FindBugsMatchers.ofType;

import org.junit.Test;

import com.youdevise.fbplugins.tdd4fb.DetectorAssert;

import edu.umd.cs.findbugs.BugReporter;

public class CustomClassNameLengthDetectorTest {

	@Test public void
	raisesAnyBugAgainstClassWithLongName() throws Exception {
		BugReporter bugReporter = DetectorAssert.bugReporterForTesting();
		
		CustomClassNameLengthDetector detector = new CustomClassNameLengthDetector(bugReporter);
		
		DetectorAssert.assertBugReported(ExampleClassWithANameThatIsTooLongForThisSillyDetector.class, 
										 detector, bugReporter);
	}
	
	@Test public void
	raisesABugOfASpecificTypeAgainstClassWithLongName() throws Exception {
		BugReporter bugReporter = DetectorAssert.bugReporterForTesting();
		
		CustomClassNameLengthDetector detector = new CustomClassNameLengthDetector(bugReporter);
		 
		DetectorAssert.assertBugReported(ExampleClassWithANameThatIsTooLongForThisSillyDetector.class, 
				detector, bugReporter, ofType("SILLY_BUG_TYPE"));
	}
	
	@Test public void
	doesNotRaiseABugAgainstClassWhichHasAShortName() throws Exception {
		BugReporter bugReporter = DetectorAssert.bugReporterForTesting();
		CustomClassNameLengthDetector detector = new CustomClassNameLengthDetector(bugReporter);
		
		DetectorAssert.assertNoBugsReported(ExampleClassWithAnAllowedName.class, detector, bugReporter);
	}
	
}
