/* (C) 2024 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.pater

import org.gradle.api.Project

public class JavaServiceLoaderGradleBuildFileResolver implements GradleBuildFileResolver {

	private static ServiceLoader<GradleBuildFile> gradleBuildFileServiceLoader = ServiceLoader
	.load(GradleBuildFile.class)

	Collection<GradleBuildFile> findBuildFiles(Project project) {
		return gradleBuildFileServiceLoader.asList()
	}
}
