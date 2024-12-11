/* (C) 2024 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.pater

import org.gradle.api.Project

public interface GradleBuildFileResolver {

	Collection<GradleBuildFile> findBuildFiles(Project project)
}
