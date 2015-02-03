package com.fizzpod.gradle.plugins.pater;

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class PaterPlugin implements Plugin<Project> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PaterPlugin.class);
	
	void apply(Project project) {
		Collection<URI> buildFiles = this.resolveBuildFiles(project);
		LOGGER.info("Discovered build files: {}", buildFiles);
		
		buildFiles = this.sortBuildFiles(project, buildFiles);
		
		for(URI uri: buildFiles) {
			project.apply(["from": uri]);
		}
		
	}
	
	private Collection<URI> sortBuildFiles(Project project, Collection<URI> buildFiles) {
		List sortedFiles = new ArrayList<>(buildFiles);
		GradleBuildFileSorter sorter = new GradleBuildFileSorterFactory().getBuildFileSorter();
		LOGGER.info("Using build file sorter {}", sorter);
		return sorter.sortBuildFiles(project, buildFiles);
	}
	
	private Collection<URI> resolveBuildFiles(Project project) {
		Set<URI> buildFileUris = new LinkedHashSet<URI>();
		Collection<GradleBuildFileResolver> resolvers = getBuildFileResolvers();
		LOGGER.info("Using build file resolvers: {} ", resolvers);
		for(GradleBuildFileResolver resolver: resolvers) {
			Collection<URI> buildFiles = resolver.findBuildFiles(project);
			if(buildFiles != null) {
				buildFileUris.addAll(buildFiles);
			}
		}
		return buildFileUris;
	}
	
	private Collection<GradleBuildFileResolver> getBuildFileResolvers() {
		return new GradleBuildFileResolverFactory().getBuildFileResolvers();
	}
	
}