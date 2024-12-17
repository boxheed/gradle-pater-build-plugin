/* (C) 2024 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.pater;

import org.gradle.api.Project;

public class TestableGradleBuildFile implements GradleBuildFile {

  @Override
  public String getName() {
    return "my-build-2";
  }

  @Override
  public void apply(Project project) {
    //        throw new RuntimeException("Apple");
    project.setProperty("prop1", "value");
  }
}
