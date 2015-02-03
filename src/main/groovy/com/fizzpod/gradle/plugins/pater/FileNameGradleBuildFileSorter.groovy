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
		List<URI> uriList = new LinkedList<>(uris);
		Collections.sort(uriList, new UriNameSplittingComaparator());
		return uriList;
	}
	
	private static final class UriNameSplittingComaparator implements Comparator<URI> {

		@Override
		public int compare(URI uri1, URI uri2) {
			String[] parts1 = getNameParts(uri1);
			String[] parts2 = getNameParts(uri2);
			return parts1.length - parts2.length;
		}
		
		private String[] getNameParts(URI uri) {
			String name = FilenameUtils.getBaseName(uri.toString());
			return name.split("-");
		}
		
	} 
}
