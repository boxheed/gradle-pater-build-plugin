/* SPDX-License-Identifier: Apache-2.0 */
/* (C) 2024 */
package com.fizzpod.gradle.plugins.pater

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class PaterPlugin implements Plugin<Project> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PaterPlugin.class)

	private GradleBuildFileResolverFactory resolverFactory = new GradleBuildFileResolverFactory()

	private GradleBuildFileSorterFactory sorterFactory = new GradleBuildFileSorterFactory()

	void apply(Project project) {
		def buildFiles = this.resolveBuildFiles(project)
		LOGGER.info("Discovered build files: {}", buildFiles)

		buildFiles = this.sortBuildFiles(project, buildFiles)

		buildFiles.each() {
			it.apply(project)
		}
	}

	private Collection<GradleBuildFile> sortBuildFiles(Project project, Collection<GradleBuildFile> buildFiles) {
		List sortedFiles = new ArrayList<>(buildFiles)
		GradleBuildFileSorter sorter = sorterFactory.getBuildFileSorter()
		LOGGER.info("Using build file sorter {}", sorter)
		return sorter.sortBuildFiles(project, buildFiles)
	}

	private Collection<GradleBuildFile> resolveBuildFiles(Project project) {
		Set<GradleBuildFile> buildFiles = new LinkedHashSet<>()
		def resolvers = getBuildFileResolvers()
		LOGGER.info("Using build file resolvers: {} ", resolvers)
		resolvers.each() {
			def resolvedBuildFiles = it.findBuildFiles(project)
			if(resolvedBuildFiles != null) {
				buildFiles.addAll(resolvedBuildFiles)
			}
		}
		return buildFiles
	}

	private Collection<GradleBuildFileResolver> getBuildFileResolvers() {
		return resolverFactory.getBuildFileResolvers()
	}
}
