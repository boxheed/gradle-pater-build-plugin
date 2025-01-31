/* (C) 2025 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.pater

import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.slf4j.Logger
import org.slf4j.LoggerFactory


public class ClasspathGradleBuildFileResolver implements GradleBuildFileResolver {

    public static final String TEMP_BUILDFILE_DIR_NAME = "pater-build"

    private static final Logger LOGGER = LoggerFactory.getLogger(ClasspathGradleBuildFileResolver.class)

    Collection<GradleBuildFile> findBuildFiles(Project project) {
        Set<String> buildFiles = scanForBuildFiles()
        LOGGER.info("Discovered build files: {}", buildFiles)
        File exportFolder = getExportFolder(project)
        return exportBuildFiles(buildFiles, exportFolder)
    }

    private File getExportFolder(Project project) {
        File exportFolder = null
        if(project != null && project.getLayout() != null && project.getLayout().getBuildDirectory() != null) {
            Provider<Directory> dirProvider = project.getLayout().getBuildDirectory().dir(TEMP_BUILDFILE_DIR_NAME)
            Directory directory = dirProvider.getOrNull()
            exportFolder = directory.getAsFile()
        }
        if(exportFolder == null) {
            exportFolder = Files.createTempDirectory(TEMP_BUILDFILE_DIR_NAME).toFile()
        }
        FileUtils.forceMkdir(exportFolder)
        return exportFolder
    }

    private Set<String> scanForBuildFiles() {

        Reflections reflections = new Reflections("META-INF.pater-build", new ResourcesScanner())

        Set<String> buildFiles =
                reflections.getResources(Pattern.compile(".*\\.gradle"))
        return buildFiles
    }

    private Collection<GradleBuildFile> exportBuildFiles(Collection<String> classpathBuildFiles, File exportFolder) {
        List<GradleBuildFile> buildFiles = new LinkedList<>()
        for(String classpathBuildFile: classpathBuildFiles) {
            File buildFile = exportBuildFile(classpathBuildFile, exportFolder)
            if(buildFile != null && buildFile.exists()) {
                GradleBuildFile gradleBuildFile = new UriGradleBuildFile(buildFile.toPath().toUri())
                buildFiles.add(gradleBuildFile)
            } else {
                LOGGER.warn("Could not export build file {}", classpathBuildFile)
            }
        }
        return buildFiles
    }

    private File exportBuildFile(String classpathBuildFile, File exportFolder) {
        File buildFile = new File(exportFolder, FilenameUtils.getName(classpathBuildFile))
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(classpathBuildFile)
            OutputStream outputStream = new FileOutputStream(buildFile)) {
            IOUtils.copy(inputStream, outputStream)
        }
        return buildFile
    }
}
