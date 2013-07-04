package com.youdevise.fbplugins.tdd4fb;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionContaining.hasItem;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import edu.umd.cs.findbugs.classfile.ICodeBaseLocator;
import edu.umd.cs.findbugs.classfile.impl.FilesystemCodeBaseLocator;

public class AuxCodeBaseLocatorProviderTest {

	private final AuxCodeBaseLocatorProvider provider = new AuxCodeBaseLocatorProvider();


	@SuppressWarnings("unchecked")
	@Test
	public void createsFileSystemCodeBaseLocatorForEachJarPathGiven() throws Exception {
		Iterable<String> auxilliaryCodeBasePaths = asList("/some/path/to/myFirstJar.jar",
														  "/some/path/to/mySecondJar.jar",
														  "/some/path/to/myDirectory/");
		Iterable<ICodeBaseLocator> codeBases = provider.get(auxilliaryCodeBasePaths);
		assertThat(codeBases, allOf(hasItem(auxilliaryCodeBaseFor("/some/path/to/myFirstJar.jar")),
									hasItem(auxilliaryCodeBaseFor("/some/path/to/mySecondJar.jar")),
									hasItem(auxilliaryCodeBaseFor("/some/path/to/myDirectory"))));
	}

	private Matcher<ICodeBaseLocator> auxilliaryCodeBaseFor(final String jarPath) {
		return new TypeSafeMatcher<ICodeBaseLocator>() {
			public void describeTo(Description description) {
				description.appendText("a FileSystemCodeBaseLocator ")
						   .appendText("with directory equal to ").appendValue(jarPath);
			}

			public boolean matchesSafely(ICodeBaseLocator codeBaseLocator) {
				return codeBaseLocator instanceof FilesystemCodeBaseLocator
						&& ((FilesystemCodeBaseLocator)codeBaseLocator).getPathName().equals(jarPath);
			}
		};
	}
	
}
