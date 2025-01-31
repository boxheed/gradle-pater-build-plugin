/* (C) 2024 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.pater;

import static org.mockito.Mockito.when;

import java.io.IOException;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.provider.Provider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

public class PaterPluginTest {

  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void applyBuildFileToProjectTest() throws IOException {
    PaterPlugin paterPlugin = new PaterPlugin();
    Project project = Mockito.mock(Project.class);
    ProjectLayout projectLayout = Mockito.mock(ProjectLayout.class);
    DirectoryProperty directoryProperty = Mockito.mock(DirectoryProperty.class);
    Provider provider = Mockito.mock(Provider.class);
    Directory directory = Mockito.mock(Directory.class);
    when(project.getLayout()).thenReturn(projectLayout);
    when(projectLayout.getBuildDirectory()).thenReturn(directoryProperty);
    when(directoryProperty.dir(ClasspathGradleBuildFileResolver.TEMP_BUILDFILE_DIR_NAME))
        .thenReturn(provider);
    when(provider.getOrNull()).thenReturn(directory);
    when(directory.getAsFile()).thenReturn(folder.newFolder());

    paterPlugin.apply(project);
    Mockito.verify(project, Mockito.times(1)).apply(Mockito.anyMap());
    Mockito.verify(project, Mockito.times(1)).setProperty("prop1", "value");
  }
}
