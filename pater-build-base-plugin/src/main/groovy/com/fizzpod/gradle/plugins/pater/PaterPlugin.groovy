package com.fizzpod.gradle.plugins.pater;

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class PaterPlugin implements Plugin<Project> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PaterPlugin.class);
	
	private static ServiceLoader<GradleBuildFileResolver> gradleBuildFileResolverLoader = ServiceLoader.load(GradleBuildFileResolver.class);
	
	void apply(Project project) {
		Set<URI> buildFiles = this.resolveBuildFiles(project);
		LOGGER.info("Discovered build files: {}", buildFiles);
		for(URI uri: buildFiles) {
			project.apply(["from": uri]);
			
		}
		
	}
	
	private Set<URI> resolveBuildFiles(Project project) {
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
		
		Collection<GradleBuildFileResolver> resolvers = new LinkedList<GradleBuildFileResolver>();
		
		for(GradleBuildFileResolver resolver: gradleBuildFileResolverLoader) {
			resolvers.add(resolver);
		}
		resolvers;
	}
	
	
}