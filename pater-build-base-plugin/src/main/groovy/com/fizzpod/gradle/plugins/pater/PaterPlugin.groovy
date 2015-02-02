package com.fizzpod.gradle.plugins.pater;

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin

public class PaterPlugin implements Plugin<Project> {
	
	private static ServiceLoader<GradleBuildFileResolver> gradleBuildFileResolverLoader = ServiceLoader.load(GradleBuildFileResolver.class);
	
	void apply(Project project) {
		println this.getClass().getName();
		Set<URI> buildFiles = this.resolveBuildFiles(project);
		println buildFiles;
		for(URI uri: buildFiles) {
			println uri;
			project.apply(["from": uri]);
			
		}
		
	}
	
	private Set<URI> resolveBuildFiles(Project project) {
		Set<URI> buildFileUris = new LinkedHashSet<URI>();
		Collection<GradleBuildFileResolver> resolvers = getBuildFileResolvers();
		println "resolvers: " + resolvers;
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