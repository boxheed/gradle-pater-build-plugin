[![CircleCI](https://circleci.com/gh/boxheed/gradle-pater-build-plugin/tree/master.svg?style=svg)](https://circleci.com/gh/boxheed/gradle-pater-build-plugin/tree/master)

# Gradle Pater Plugin
This plugin provides parent build script like capabilities to Gradle. Inspired by a number of blog posts about how to work with/around the current capabilities of Gradle to support importing other build scripts. Current strategies use either `apply from:` in conjunction with a URL or recommend writing your own plugin to set different aspects of a Gradle project programmatically. This plugin combines both of these approaches into a single strategy that allows you to write simple gradle build files and take advantage of dependency management.

# How
By packaging your gradle build script up in a jar file and including in your build script dependencies this plugin will find the build script and apply it to the gradle project as if it was referenced in the `apply from:` notation. What's more by taking advantage of jar library dependency management you can chain a number of build scripts together and have them applied to the project. This plugin uses the naming convention of the build scripts to sort the discovered scripts into a determinate order for applying to the project.

# Simple Usage
Create your java library which includes your build script. the pater-build plugin scans for files in a specific location in the `META-INF/pater-build` and must end in `.gradle` e.g. `META-INF/pater-build/opinion-java.gradle`. Then all you have to do is add your library into the buildscripts classpath along with the pater-build plugin e.g.

```
buildscript {
	repositories {
		jcenter()
	}
	dependencies {
	    //The pater build plugin.
		classpath 'com.fizzpod:gradle-pater-build-plugin:1.0.0'
		//Your library containing the build file in META-INF/pater-build
		classpath 'com.example:opinion-java:1.0.0'
	}
}

apply plugin: 'com.fizzpod.pater-build'

```

You can of course include the pater-build plugin as a dependency of your build jar, in which case you would only have your build jar as a dependency, I'll leave this up to you to decide whether you want to manage it that way or not.

# Build script ordering
The plugin orders the build scripts that it finds according to the names of the build scripts following a naming convention.
* The .gradle extension is removed.
* The names are separated out by hyphens e.g. `my-build-script.gradle` will become three tokens `[my][build][script]`.
* The tokens from each build script name are compared with scripts with fewer tokens migrating to the top of the list.

It is therefore necessary to apply a naming convention to the build files included to be certain that they are applied in the correct order.

# Limitations
As the library uses classpath scanning if you have more than one build script with the same name it is indeterminate which one will be selected, but it will be only one of them.

# Extension points
The plugin exposes a number of extension points, these are provided by using the Java ServiceLoader to discover implementations of the extension points.

## Build file resolution
You can customise the build file resolution with your own implementation. The interface is `com.fizzpod.gradle.plugins.pater.GradleBuildFileResolver` with the defualt implentation being `com.fizzpod.gradle.plugins.pater.ClasspathGradleBuildFileResolver`. To apply your own implementation you need to add a file to your `META-INF/services/com.fizzpod.gradle.plugins.pater.GradleBuildFileResolver` the contents of which is the implementing classes. Note that if you still want the default implementation to run along with your custom one then you need to specify the default one in this file. The plugin will use all defined GradleBuildFileResolvers to find the build files.

## Build file sorting
you can customise the sorting of the buil files with your own implementation. The interface is `com.fizzpod.gradle.plugins.pater.GradleBuildFileSorter` with the default implementation being `com.fizzpod.gradle.plugins.pater.FileNameGradleBuildFileSorter`. As with the build file resolution you need to specify the implementation in a `META-INF/services/com.fizzpod.gradle.plugins.pater.GradleBuildFileSorter`. The difference with this extension point is that whilst you can specify multiple GradleBuildFileSorters it will only use the first one discovered.
