# Gradle Pater Plugin
This plugin provides parent build script like capabilities to Gradle. Inspired by a number of blog posts about how to work with/around the current capabilities of Gradle to support importing other build scripts. Current strategies use either `apply from:` in conjunction with a URL or recommend writing your own plugin to set different aspects of a Gradle project programmatically. This plugin combines both of these approaches.

# Usage
This plugin will detect gradle build scripts included on the buildscripts classpath and load them into the root Project. To do this you need to package your build script into a jar file and include the gradle pater plugin as a dependency, then it is as simple as including your jar as a dependency in the buildscripts section which will in turn pull in the plugin. See the example plugin.

The `pater-build-classpath-plugin` scans the buildscript dependencies for Gradle build files in a `META-INF/pater-build` package the build scripts must end in `.gradle`. e.g. `META-INF/pater-build/my-parent-build.gradle`.

The build script to create the jar file to hold your parent build script might look like the following. 

```
buildscript {
	repositories {
		jcenter()
	}
}

apply plugin: 'java'

group = 'com.fizzpod'
version = '1.0'

dependencies {
	compile 'com.fizzpod:pater-build-classpath-plugin:0.1.1'
}

```
The key here is the dependency on the `pater-build-classpath` plugin, the created jar file have a dependency on the `pater-build-classpath-plugin` so when included in the build dependencies it will also pull in the `pater-build-classpath-plugin`

Include this library in your other build files to import the parent build script, e.g. 
```
buildscript {
	repositories {
		mavenLocal()
		jcenter()
	}
	dependencies {
		classpath group: 'com.fizzpod', name: 'pater-build-example-plugin', version: '1.0'
	}
}

apply plugin: 'com.fizzpod.pater-build-cp'

```
