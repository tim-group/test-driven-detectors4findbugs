package com.youdevise.fbplugins.tdd4fb;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import edu.umd.cs.findbugs.classfile.ICodeBaseLocator;
import edu.umd.cs.findbugs.classfile.impl.FilesystemCodeBaseLocator;

public class AuxCodeBaseLocatorProviderTest {

	private final AuxCodeBaseLocatorProvider provider = new AuxCodeBaseLocatorProvider();


	@Test
	public void createsFileSystemCodeBaseLocatorForEachJarPathGiven() throws Exception {
		Iterable<String> auxilliaryCodeBasePaths = 
		        convertToFilePaths(asList("path/to/myFirstJar.jar",
										  "path/to/mySecondJar.jar",
										  "path/to/myDirectory/"));
		Iterable<ICodeBaseLocator> codeBases = provider.get(auxilliaryCodeBasePaths);
		assertThat(codeBases, allOf(Matchers.<ICodeBaseLocator>hasItem(auxilliaryCodeBaseFor("path/to/myFirstJar.jar")),
		                            Matchers.<ICodeBaseLocator>hasItem(auxilliaryCodeBaseFor("path/to/mySecondJar.jar")),
		                            Matchers.<ICodeBaseLocator>hasItem(auxilliaryCodeBaseFor("path/to/myDirectory"))));
	}
	
	private Iterable<String> convertToFilePaths(Iterable<String> paths) {
	    List<String> files = new ArrayList<String>();
	    for (String path : paths) {
            files.add(absolutePath(path));
        }
	    return files;
	}

	private Matcher<ICodeBaseLocator> auxilliaryCodeBaseFor(final String codeBasePath) {
		return new TypeSafeMatcher<ICodeBaseLocator>() {
			public void describeTo(Description description) {
				description.appendText("a FileSystemCodeBaseLocator ")
						   .appendText("with directory equal to ").appendValue(absolutePath(codeBasePath));
			}

			public boolean matchesSafely(ICodeBaseLocator codeBaseLocator) {
				return codeBaseLocator instanceof FilesystemCodeBaseLocator
						&& ((FilesystemCodeBaseLocator)codeBaseLocator).getPathName().equals(absolutePath(codeBasePath));
			}
		};
	}
	
	private static String absolutePath(String path) {
	    return new File(path).getAbsolutePath();
	}
}
