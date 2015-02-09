package com.fizzpod.gradle.plugins.pater;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

public class GradleBuildFileResolverFactoryTest {

	@Test
	public void testLoadingCustomBuildFileResolver() {
		Collection<GradleBuildFileResolver> resolvers = new GradleBuildFileResolverFactory().getBuildFileResolvers();
		Assert.assertEquals(1, resolvers.size());
		Assert.assertEquals(CustomBuildFileResolver.class, resolvers.iterator().next().getClass());
	}
	
}
