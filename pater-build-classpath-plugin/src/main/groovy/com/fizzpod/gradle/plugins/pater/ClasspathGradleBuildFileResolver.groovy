package com.fizzpod.gradle.plugins.pater;

import java.util.regex.Pattern

import org.gradle.api.Project
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class ClasspathGradleBuildFileResolver implements GradleBuildFileResolver {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClasspathGradleBuildFileResolver.class);

	Collection<URI> findBuildFiles(Project project) {
		Set<String> buildFiles = scanForBuildFiles();
		LOGGER.info("Discovered build files {}", buildFiles);
		Collection<File> exportedBuildFiles = exportBuildFiles(buildFiles);
		return transformBuildFilesToUri(project, exportedBuildFiles);
	}
	
	private Set<String> scanForBuildFiles(Project project) {
		
		Reflections reflections = new Reflections("META-INF.pater-build", new ResourcesScanner());
		
		Set<String> buildFiles =
		reflections.getResources(Pattern.compile(".*\\.gradle"));
		return buildFiles;
	}
	
	private Collection<File> exportBuildFiles(Collection<String> classpathBuildFiles) {
		List<File> buildFiles = new LinkedList<>();
		for(String classpathBuildFile: classpathBuildFiles) {
			File buildFile = exportBuildFile(classpathBuildFile);
			if(buildFile != null && buildFile.exists()) {
				buildFiles.add(buildFile);
			} else {
				LOGGER.warn("Could not export build file {}", classpathBuildFile);
			}
		}
		return buildFiles;
	}
	
	private File exportBuildFile(classpathBuildFile) {
		return new File("C:/Users/andyd_000/Documents/workspace-ggts-3.6.3.SR1/gradle-parent/src/test/resources/build.gradle");
	}
	
	private Collection<URI> transformBuildFilesToUri(Project project, Collection<File> buildFiles) {
		List<URI> uris = new ArrayList<>();
		for(File buildFile: buildFiles) {
			uris.add(project.uri(buildFile));
		}
		return uris;
	}
	
}
