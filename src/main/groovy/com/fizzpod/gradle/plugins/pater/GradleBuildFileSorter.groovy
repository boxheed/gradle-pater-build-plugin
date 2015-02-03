package com.fizzpod.gradle.plugins.pater;

import org.gradle.api.Project

public interface GradleBuildFileSorter {

	public Collection<URI> sortBuildFiles(Project project, Collection<URI> uris);
	
}
