package com.fizzpod.gradle.plugins.pater;

import java.net.URI;
import java.util.Collection;
import java.util.regex.Pattern

import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class FileNameGradleBuildFileSorter implements GradleBuildFileSorter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileNameGradleBuildFileSorter.class);

	@Override
	public Collection<URI> sortBuildFiles(Project project, Collection<URI> uris) {
		return uris;
	}
	
}
