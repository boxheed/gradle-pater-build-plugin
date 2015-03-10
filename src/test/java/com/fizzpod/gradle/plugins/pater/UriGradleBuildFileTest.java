package com.fizzpod.gradle.plugins.pater;

import java.io.File;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.gradle.api.Project;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

public class UriGradleBuildFileTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void applyBuildFileToProjectTest() {
		List<GradleBuildFile> buildFiles = createFiles("build-file-1.gradle");
		GradleBuildFile buildFile = buildFiles.get(0);
		Project project = Mockito.mock(Project.class);
		buildFile.apply(project);
		Assert.assertEquals("build-file-1", buildFile.getName());
		Mockito.verify(project, Mockito.times(1)).apply(Mockito.anyMap());
	}
	
	
	private List<GradleBuildFile> createFiles(String... fileNames) {
		List<GradleBuildFile> uris = new LinkedList<GradleBuildFile>();
		for(String name: fileNames) {
			URI uri = new File(folder.getRoot(), name).toPath().toUri();
			uris.add(new UriGradleBuildFile(uri));
		}
		return uris;
	}
	
}
