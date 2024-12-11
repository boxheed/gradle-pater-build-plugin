/* (C) 2024 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.pater;

import org.junit.Assert;
import org.junit.Test;

public class GradleBuildFileSorterFactoryTest {

  @Test
  public void testLoadingCustomBuildFileResolver() {
    GradleBuildFileSorter sorter = new GradleBuildFileSorterFactory().getBuildFileSorter();
    Assert.assertEquals(CustomBuildFileSorter.class, sorter.getClass());
  }
}
