/* SPDX-License-Identifier: Apache-2.0 */
/* (C) 2024 */
package com.fizzpod.gradle.plugins.pater;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileNameGradleBuildFileSorterTest {

  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void testFirstLevelNameSorting() {
    Collection<GradleBuildFile> uris = createFiles("def.gradle", "abc.gradle");
    FileNameGradleBuildFileSorter sorter = new FileNameGradleBuildFileSorter();
    List<GradleBuildFile> sortedBuildFiles =
        new LinkedList<GradleBuildFile>(sorter.sortBuildFiles(null, uris));
    assertBuildFile("abc", sortedBuildFiles.get(0));
    assertBuildFile("def", sortedBuildFiles.get(1));
  }

  @Test
  public void testSameFirstLevelWithTwoLevels() {
    Collection<GradleBuildFile> uris = createFiles("abc-xyz.gradle", "abc.gradle");
    FileNameGradleBuildFileSorter sorter = new FileNameGradleBuildFileSorter();
    List<GradleBuildFile> sortedBuildFiles =
        new LinkedList<GradleBuildFile>(sorter.sortBuildFiles(null, uris));
    assertBuildFile("abc", sortedBuildFiles.get(0));
    assertBuildFile("abc-xyz", sortedBuildFiles.get(1));
  }

  @Test
  public void testSameFirstLevelDifferentSecondLevelNameSorting() {
    Collection<GradleBuildFile> uris = createFiles("abc-xyz.gradle", "abc-def.gradle");
    FileNameGradleBuildFileSorter sorter = new FileNameGradleBuildFileSorter();
    List<GradleBuildFile> sortedBuildFiles =
        new LinkedList<GradleBuildFile>(sorter.sortBuildFiles(null, uris));
    assertBuildFile("abc-def", sortedBuildFiles.get(0));
    assertBuildFile("abc-xyz", sortedBuildFiles.get(1));
  }

  @Test
  public void testSameFirstLevelSecondLevelSameButLongerNameSorting() {
    Collection<GradleBuildFile> uris = createFiles("abc-xyz.gradle", "abc-xyzdef.gradle");
    FileNameGradleBuildFileSorter sorter = new FileNameGradleBuildFileSorter();
    List<GradleBuildFile> sortedBuildFiles =
        new LinkedList<GradleBuildFile>(sorter.sortBuildFiles(null, uris));
    assertBuildFile("abc-xyz", sortedBuildFiles.get(0));
    assertBuildFile("abc-xyzdef", sortedBuildFiles.get(1));
  }

  @Test
  public void testMulitpleMixedNameSorting() {
    Collection<GradleBuildFile> uris =
        createFiles("abc.gradle", "def-xyz.gradle", "abc-xyz.gradle", "def.gradle");
    FileNameGradleBuildFileSorter sorter = new FileNameGradleBuildFileSorter();
    List<GradleBuildFile> sortedBuildFiles =
        new LinkedList<GradleBuildFile>(sorter.sortBuildFiles(null, uris));
    assertBuildFile("abc", sortedBuildFiles.get(0));
    assertBuildFile("def", sortedBuildFiles.get(1));
    assertBuildFile("abc-xyz", sortedBuildFiles.get(2));
    assertBuildFile("def-xyz", sortedBuildFiles.get(3));
  }

  @Test
  public void testCaseInsensitiveNameSorting() {
    Collection<GradleBuildFile> uris = createFiles("a.gradle", "B.gradle");
    FileNameGradleBuildFileSorter sorter = new FileNameGradleBuildFileSorter();
    List<GradleBuildFile> sortedBuildFiles =
        new LinkedList<GradleBuildFile>(sorter.sortBuildFiles(null, uris));
    assertBuildFile("a", sortedBuildFiles.get(0));
    assertBuildFile("B", sortedBuildFiles.get(1));
  }

  private void assertBuildFile(String expected, GradleBuildFile actual) {
    Assert.assertEquals(expected, actual.getName());
  }

  private Collection<GradleBuildFile> createFiles(String... fileNames) {
    List<GradleBuildFile> uris = new LinkedList<GradleBuildFile>();
    for (String name : fileNames) {
      URI uri = new File(folder.getRoot(), name).toPath().toUri();
      uris.add(new UriGradleBuildFile(uri));
    }
    return uris;
  }
}
