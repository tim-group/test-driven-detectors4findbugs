package com.youdevise.fbplugins.tdd4fb;

import static java.util.Collections.unmodifiableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.classfile.ICodeBaseLocator;
import edu.umd.cs.findbugs.classfile.impl.FilesystemCodeBaseLocator;

public class AuxCodeBaseLocatorProvider {

	public Iterable<ICodeBaseLocator> get(Iterable<String> auxilliaryCodeBasePaths) throws IOException {
		List<ICodeBaseLocator> auxilliaryCodeBaseLocators = new ArrayList<ICodeBaseLocator>();
		for (String codeBasePath : auxilliaryCodeBasePaths) {
			auxilliaryCodeBaseLocators.add(new FilesystemCodeBaseLocator(codeBasePath));
		}
		return unmodifiableList(auxilliaryCodeBaseLocators);
	}

}
