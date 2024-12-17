/* (C) 2024 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.pater

import java.net.URI
import java.util.Collection
import java.util.regex.Pattern
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class FileNameGradleBuildFileSorter implements GradleBuildFileSorter {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileNameGradleBuildFileSorter.class)

	@Override
	public Collection<GradleBuildFile> sortBuildFiles(Project project, Collection<GradleBuildFile> buildFiles) {
		List<GradleBuildFile> buildFileList = new LinkedList<>(buildFiles)
		Collections.sort(buildFileList, new NameSplittingComaparator())
		return buildFileList
	}

	private static final class NameSplittingComaparator implements Comparator<GradleBuildFile> {

		@Override
		public int compare(GradleBuildFile buildFile1, GradleBuildFile buildFile2) {
			int comparison = 0
			String[] parts1 = getNameParts(buildFile1.getName())
			String[] parts2 = getNameParts(buildFile2.getName())
			if(parts1.length == parts2.length) {
				comparison = compareParts(parts1, parts2)
			} else {
				comparison = parts1.length - parts2.length
			}
			return comparison
		}

		private String[] getNameParts(String name) {
			return name.split("-")
		}

		private int compareParts(String[] parts1, String[] parts2) {
			int comparison = 0
			for(int i = 0; i < parts1.length && comparison == 0; i++) {
				comparison = parts1[i].toLowerCase().compareTo(parts2[i].toLowerCase())
			}
			return comparison
		}
	}
}
