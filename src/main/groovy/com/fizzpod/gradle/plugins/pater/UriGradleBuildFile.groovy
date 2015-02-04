package com.fizzpod.gradle.plugins.pater;

import org.apache.commons.io.FilenameUtils
import org.gradle.api.Project

public class UriGradleBuildFile implements GradleBuildFile {
	
	private URI uri;
	
	public UriGradleBuildFile(URI uri) {
		this.uri = uri;
	}

	public String getName() {
		return FilenameUtils.getBaseName(uri.toString())
	}
	
	public void apply(Project project) {
		project.apply(["from": uri]);
	}
	
}
