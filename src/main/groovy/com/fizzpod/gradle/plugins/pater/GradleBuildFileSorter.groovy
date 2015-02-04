package com.fizzpod.gradle.plugins.pater;

import org.gradle.api.Project

public interface GradleBuildFileSorter {

	public Collection<GradleBuildFile> sortBuildFiles(Project project, Collection<GradleBuildFile> uris);
	
}
