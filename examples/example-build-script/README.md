# Build file example

This example show how simple it is to package your build file and use it.

## Build script
The following generates a jar file with your gradle file in place.
```
buildscript {
	repositories {
		mavenLocal()
		jcenter()
	}
}

apply plugin: 'java'
apply plugin: 'maven'

group = 'com.fizzpod'
version = '1.0'
```

Running `gradle clean install` will install this to your local repository. Using this is as simple as the following snippet.

```
buildscript {
	repositories {
		mavenLocal()
		jcenter()
	}
	dependencies {
		classpath 'com.fizzpod:pater-build:0+'
		classpath 'com.fizzpod:example-build-script:1.0'
	}
}

apply plugin: 'com.fizzpod.pater-build'

```

Running `gradle -b usage.gradle -i` with info level logging you will see the `Discovered build files: [META-INF/pater-build/pater-example.gradle]`. The `pater-example.gradle` file applies the `application` plugin so by running `gradle -b usage.gradle tasks` you will see the application tasks specified:

```
Application tasks
-----------------
distTar - Bundles the project as a JVM application with libs and OS specific scripts.
distZip - Bundles the project as a JVM application with libs and OS specific scripts.
installApp - Installs the project as a JVM application along with libs and OS specific scripts.
run - Runs this project as a JVM application
```