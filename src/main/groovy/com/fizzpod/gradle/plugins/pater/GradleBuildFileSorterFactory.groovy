/* (C) 2024 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.pater

import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class GradleBuildFileSorterFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(GradleBuildFileSorterFactory.class)

	private static ServiceLoader<GradleBuildFileSorter> gradleBuildFileSorterLoader = ServiceLoader
	.load(GradleBuildFileSorter.class)

	public GradleBuildFileSorter getBuildFileSorter() {

		GradleBuildFileSorter sorter = new FileNameGradleBuildFileSorter()

		Iterator<GradleBuildFileSorter> sortersIterator = gradleBuildFileSorterLoader.iterator()

		if(sortersIterator.hasNext()) {
			sorter = sortersIterator.next()
			LOGGER.info("Found sorter {}", sorter)
		} else {
			LOGGER.debug("Using default sorter {}", sorter)
		}

		return sorter
	}
}
