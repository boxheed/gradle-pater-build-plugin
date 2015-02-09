# External dependency build file example
Most frquently you will want to use a build file that itself will have an external dependency perhaps on a third party plugin.

## Internal Build script
The internal build script (in the `META_INF/pater-build` package) is small with a single plugin in it:
```
apply plugin: 'pl.allegro.tech.build.axion-release'
```

You then have to add the `axion-release` plugin as a dependency in the main build script:
```
dependencies {
	compile group: 'pl.allegro.tech.build', name: 'axion-release-plugin', version: '1.1.0'
}
```
Running `gradle clean install` will install this to your local repository. Using this is as simple as the following snippet.

```
buildscript {
	repositories {
		mavenLocal()
		jcenter()
	}
	dependencies {
		classpath 'com.fizzpod:gradle-pater-build-plugin:0+'
		classpath 'com.fizzpod:external-dependency-build-script:1.0'
	}
}

apply plugin: 'com.fizzpod.pater-build'

```

Running `gradle -b usage.gradle -i` with info level logging you will see the `Discovered build files: [META-INF/pater-build/pater-external-example.gradle]`. The `pater-external-example.gradle` file applies the `axion-release` plugin so by running `gradle -b usage.gradle tasks` you will see the axion-release tasks specified:

```
Release tasks
-------------
markNextVersionTask - Creates next version marker tag and pushes it to remote.
release - Performs release - creates tag and pushes it to remote.
verifyRelease - Verifies code is ready for release.
```