/* (C) 2024 */
/* SPDX-License-Identifier: Apache-2.0 */
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

  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void applyBuildFileToProjectTest() {
    List<GradleBuildFile> buildFiles = createFiles("build-file-1.gradle");
    GradleBuildFile buildFile = buildFiles.get(0);
    Project project = Mockito.mock(Project.class);
    buildFile.apply(project);
    Assert.assertEquals("build-file-1", buildFile.getName());
    Assert.assertFalse("".equals(buildFile.toString()));
    Mockito.verify(project, Mockito.times(1)).apply(Mockito.anyMap());
  }

  @Test
  public void testEquals() {
    List<GradleBuildFile> buildFiles = createFiles("build-file-1.gradle", "build-file-1.gradle");
    GradleBuildFile buildFile1 = buildFiles.get(0);
    GradleBuildFile buildFile2 = buildFiles.get(1);
    Object obj = new Object();
    
    Assert.assertTrue(buildFiles.size() == 2);
    Assert.assertTrue(buildFile1.equals(buildFile1));
    Assert.assertTrue(buildFile1.equals(buildFile2));
    Assert.assertFalse(buildFile1.equals(obj));
  }

  private List<GradleBuildFile> createFiles(String... fileNames) {
    List<GradleBuildFile> uris = new LinkedList<GradleBuildFile>();
    for (String name : fileNames) {
      URI uri = new File(folder.getRoot(), name).toPath().toUri();
      uris.add(new UriGradleBuildFile(uri));
    }
    return uris;
  }
}
