package com.fizzpod.gradle.plugins.pater;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

public class ClasspathGradleBuildFileResolverTest {

	@Test
	public void testFindingBuildFile() {
		ClasspathGradleBuildFileResolver resolver = new ClasspathGradleBuildFileResolver();
		Collection<GradleBuildFile> buildFiles = resolver.findBuildFiles(null);
		Assert.assertEquals(1, buildFiles.size());
		GradleBuildFile buildFile = buildFiles.iterator().next();
		Assert.assertNotNull(buildFile);
		Assert.assertEquals("my-build", buildFile.getName());
	}
	
}
