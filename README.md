# Gradle Pater Plugin
This plugin provides parent build script like capabilities to Gradle. Inspired by a number of blog posts about how to work with/around the current capabilities of Gradle to support importing other build scripts. Current strategies use either `apply from:` in conjunction with a URL or recommend writing your own plugin to set different aspects of a Gradle project programmatically. This plugin combines both of these approaches.

# Overview
This plugin will detect gradle build scripts included on the buildscripts classpath and load them into the root Project. To do this you need to package your build script into a jar file and include the gradle pater plugin as a dependency, then it is as simple as including your jar as a dependency in the buildscripts section which will in turn pull in the plugin. See the example plugin.

# Example
A cut down version of the build script for the plugin might look like the following. The key here is the dependency on the pater-build-classpath plugin.


```
buildscript {
	repositories {
		jcenter()
	}
}

apply plugin: 'java'

group = 'com.fizzpod'
version = '1.0-SNAPSHOT'

dependencies {
	compile 'com.fizzpod:pater-build-classpath-plugin:0.1.1'
}

```

Your build script is scanned for in a specific location in the jar files `META-INF/pater-build` and must end in `.gradle`. 