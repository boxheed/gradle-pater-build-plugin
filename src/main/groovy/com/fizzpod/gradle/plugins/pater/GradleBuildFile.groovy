package com.fizzpod.gradle.plugins.pater;

import org.gradle.api.Project

public interface GradleBuildFile {

	String getName();
	
	void apply(Project project);
	
}
