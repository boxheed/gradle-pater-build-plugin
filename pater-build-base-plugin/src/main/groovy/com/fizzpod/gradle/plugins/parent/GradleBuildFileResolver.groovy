package com.fizzpod.gradle.plugins.parent;

import org.gradle.api.Project

public interface GradleBuildFileResolver {

	Collection<URI> findBuildFiles(Project project);
	
}
