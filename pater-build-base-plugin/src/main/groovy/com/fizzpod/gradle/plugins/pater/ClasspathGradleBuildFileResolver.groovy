package com.fizzpod.gradle.plugins.pater;

import java.util.regex.Pattern

import org.gradle.api.Project
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder

public class ClasspathGradleBuildFileResolver implements GradleBuildFileResolver {

	Collection<URI> findBuildFiles(Project project) {
		Set<String> buildFiles = scanForBuildFiles();
		
		return Arrays.asList(project.uri(new File("C:/Users/andyd_000/Documents/workspace-ggts-3.6.3.SR1/gradle-parent/src/test/resources/build.gradle")));
	}
	
	private Set<String> scanForBuildFiles(Project project) {
		
		Reflections reflections = new Reflections("META-INF.gradle-parent", new ResourcesScanner());
		
		Set<String> buildFiles =
		reflections.getResources(Pattern.compile(".*\\.gradle"));
		println "buildFiles" + buildFiles;
		return buildFiles;
	}
	
}
