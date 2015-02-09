package com.fizzpod.gradle.plugins.pater;

import org.gradle.api.Project;
import org.junit.Test;
import org.mockito.Mockito;

public class PaterPluginTest {

	@Test
	public void applyBuildFileToProjectTest() {
		PaterPlugin paterPlugin = new PaterPlugin();
		Project project = Mockito.mock(Project.class);
		paterPlugin.apply(project);
		Mockito.verify(project, Mockito.times(1)).apply(Mockito.anyMap());
	}
	
}
