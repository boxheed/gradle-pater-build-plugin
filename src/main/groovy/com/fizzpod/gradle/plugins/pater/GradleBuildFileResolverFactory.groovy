/* (C) 2024 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.pater

import java.util.Collection
import java.util.LinkedList
import java.util.ServiceLoader

public class GradleBuildFileResolverFactory {

	private static ServiceLoader<GradleBuildFileResolver> gradleBuildFileResolverLoader = ServiceLoader
	.load(GradleBuildFileResolver.class)

	public Collection<GradleBuildFileResolver> getBuildFileResolvers() {

		Collection<GradleBuildFileResolver> resolvers = new LinkedList<GradleBuildFileResolver>()

		for (GradleBuildFileResolver resolver : gradleBuildFileResolverLoader) {
			resolvers.add(resolver)
		}
		if(resolvers.isEmpty()) {
			resolvers.add(new ClasspathGradleBuildFileResolver())
			resolvers.add(new JavaServiceLoaderGradleBuildFileResolver())
		}
		return resolvers
	}
}
