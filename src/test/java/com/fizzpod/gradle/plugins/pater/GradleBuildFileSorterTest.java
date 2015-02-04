package com.fizzpod.gradle.plugins.pater;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GradleBuildFileSorterTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void testFirstLevelNameSorting() {
		Collection<URI> uris = createFileUris("def.gradle", "abc.gradle");
		FileNameGradleBuildFileSorter sorter = new FileNameGradleBuildFileSorter();
		List<URI> sortedUris = new LinkedList<>(sorter.sortBuildFiles(null, uris));
		assertUri("abc.gradle", sortedUris.get(0));
		assertUri("def.gradle", sortedUris.get(1)) ;
	}
	
	@Test
	public void testSameFirstLevelWithTwoLevels() {
		Collection<URI> uris = createFileUris("abc-xyz.gradle", "abc.gradle");
		FileNameGradleBuildFileSorter sorter = new FileNameGradleBuildFileSorter();
		List<URI> sortedUris = new LinkedList<>(sorter.sortBuildFiles(null, uris));
		assertUri("abc.gradle", sortedUris.get(0));
		assertUri("abc-xyz.gradle", sortedUris.get(1));
	}
	
	@Test
	public void testSameFirstLevelDifferentSecondLevelNameSorting() {
		Collection<URI> uris = createFileUris("abc-xyz.gradle", "abc-def.gradle");
		FileNameGradleBuildFileSorter sorter = new FileNameGradleBuildFileSorter();
		List<URI> sortedUris = new LinkedList<>(sorter.sortBuildFiles(null, uris));
		assertUri("abc-def.gradle", sortedUris.get(0));
		assertUri("abc-xyz.gradle", sortedUris.get(1));
	}
	
	
	@Test
	public void testSameFirstLevelSecondLevelSameButLongerNameSorting() {
		Collection<URI> uris = createFileUris("abc-xyz.gradle", "abc-xyzdef.gradle");
		FileNameGradleBuildFileSorter sorter = new FileNameGradleBuildFileSorter();
		List<URI> sortedUris = new LinkedList<>(sorter.sortBuildFiles(null, uris));
		assertUri("abc-xyz.gradle", sortedUris.get(0));
		assertUri("abc-xyzdef.gradle", sortedUris.get(1));
	}
	
	@Test
	public void testMulitpleMixedNameSorting() {
		Collection<URI> uris = createFileUris("abc.gradle", "def-xyz.gradle", "abc-xyz.gradle", "def.gradle");
		FileNameGradleBuildFileSorter sorter = new FileNameGradleBuildFileSorter();
		List<URI> sortedUris = new LinkedList<>(sorter.sortBuildFiles(null, uris));
		assertUri("abc.gradle", sortedUris.get(0));
		assertUri("def.gradle", sortedUris.get(1));
		assertUri("abc-xyz.gradle", sortedUris.get(2));
		assertUri("def-xyz.gradle", sortedUris.get(3));
	}
	
	@Test
	public void testCaseInsensitiveNameSorting() {
		Collection<URI> uris = createFileUris("a.gradle", "B.gradle");
		FileNameGradleBuildFileSorter sorter = new FileNameGradleBuildFileSorter();
		List<URI> sortedUris = new LinkedList<>(sorter.sortBuildFiles(null, uris));
		assertUri("a.gradle", sortedUris.get(0));
		assertUri("B.gradle", sortedUris.get(1));
	}
	
	private void assertUri(String expected, URI actual) {
		Assert.assertEquals(expected, FilenameUtils.getName(actual.toString()));
		
	}

	private Collection<URI> createFileUris(String... fileNames) {
		List<URI> uris = new LinkedList<URI>();
		for(String name: fileNames) {
			URI uri = new File(folder.getRoot(), name).toPath().toUri();
			uris.add(uri);
		}
		return uris;
	}
	
}
