package com.fizzpod.gradle.plugins.pater;

import org.gradle.api.Project

public interface GradleBuildFileResolver {

	Collection<URI> findBuildFiles(Project project);
	
}
