/* (C) 2024 */
/* SPDX-License-Identifier: Apache-2.0 */
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
    Mockito.verify(project, Mockito.times(1)).setProperty("prop1", "value");
  }
}
