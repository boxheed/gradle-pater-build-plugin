/* (C) 2024 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.pater

import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.slf4j.Logger
import org.slf4j.LoggerFactory


public class ClasspathGradleBuildFileResolver implements GradleBuildFileResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClasspathGradleBuildFileResolver.class)

	Collection<GradleBuildFile> findBuildFiles(Project project) {
		Set<String> buildFiles = scanForBuildFiles()
		LOGGER.info("Discovered build files: {}", buildFiles)
		return exportBuildFiles(buildFiles)
	}

	private Set<String> scanForBuildFiles(Project project) {

		Reflections reflections = new Reflections("META-INF.pater-build", new ResourcesScanner())

		Set<String> buildFiles =
				reflections.getResources(Pattern.compile(".*\\.gradle"))
		return buildFiles
	}

	private Collection<GradleBuildFile> exportBuildFiles(Collection<String> classpathBuildFiles) {
		List<GradleBuildFile> buildFiles = new LinkedList<>()
		for(String classpathBuildFile: classpathBuildFiles) {
			File buildFile = exportBuildFile(classpathBuildFile)
			if(buildFile != null && buildFile.exists()) {
				GradleBuildFile gradleBuildFile = new UriGradleBuildFile(buildFile.toPath().toUri())
				buildFiles.add(gradleBuildFile)
			} else {
				LOGGER.warn("Could not export build file {}", classpathBuildFile)
			}
		}
		return buildFiles
	}

	private File exportBuildFile(String classpathBuildFile) {
		File tempDirectoryFile = Files.createTempDirectory("pater-build").toFile()
		FileUtils.forceDeleteOnExit(tempDirectoryFile)
		File buildFile = new File(tempDirectoryFile, FilenameUtils.getName(classpathBuildFile))
		InputStream inputStream = null
		OutputStream outputStream = null
		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(classpathBuildFile)
			outputStream = new FileOutputStream(buildFile)
			IOUtils.copy(inputStream, outputStream)
			FileUtils.forceDeleteOnExit(buildFile)
		} finally {
			IOUtils.closeQuietly(inputStream)
			IOUtils.closeQuietly(outputStream)
		}
		return buildFile
	}
}
