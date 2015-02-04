package com.fizzpod.gradle.plugins.pater;

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class PaterPlugin implements Plugin<Project> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PaterPlugin.class);
	
	private GradleBuildFileResolverFactory resolverFactory = new GradleBuildFileResolverFactory();
	
	private GradleBuildFileSorterFactory sorterFactory = new GradleBuildFileSorterFactory();
	
	
	void apply(Project project) {
		Collection<GradleBuildFile> buildFiles = this.resolveBuildFiles(project);
		LOGGER.info("Discovered build files: {}", buildFiles);
		
		buildFiles = this.sortBuildFiles(project, buildFiles);
		
		for(GradleBuildFile buildFile: buildFiles) {
			buildFile.apply(project);
		}
	}
	
	private Collection<GradleBuildFile> sortBuildFiles(Project project, Collection<GradleBuildFile> buildFiles) {
		List sortedFiles = new ArrayList<>(buildFiles);
		GradleBuildFileSorter sorter = sorterFactory.getBuildFileSorter();
		LOGGER.info("Using build file sorter {}", sorter);
		return sorter.sortBuildFiles(project, buildFiles);
	}
	
	private Collection<GradleBuildFile> resolveBuildFiles(Project project) {
		Set<GradleBuildFile> buildFileUris = new LinkedHashSet<>();
		Collection<GradleBuildFileResolver> resolvers = getBuildFileResolvers();
		LOGGER.info("Using build file resolvers: {} ", resolvers);
		for(GradleBuildFileResolver resolver: resolvers) {
			Collection<GradleBuildFile> buildFiles = resolver.findBuildFiles(project);
			if(buildFiles != null) {
				buildFileUris.addAll(buildFiles);
			}
		}
		return buildFileUris;
	}
	
	private Collection<GradleBuildFileResolver> getBuildFileResolvers() {
		return resolverFactory.getBuildFileResolvers();
	}
	
}