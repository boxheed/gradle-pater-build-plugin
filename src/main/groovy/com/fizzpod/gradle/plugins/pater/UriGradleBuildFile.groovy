package com.fizzpod.gradle.plugins.pater;

import org.apache.commons.io.FilenameUtils
import org.gradle.api.Project

public class UriGradleBuildFile implements GradleBuildFile {
	
	private URI uri;
	
	public UriGradleBuildFile(URI uri) {
		this.uri = uri;
	}

	public String getName() {
		return FilenameUtils.getBaseName(uri.toString())
	}
	
	public void apply(Project project) {
		project.apply(["from": uri]);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UriGradleBuildFile other = (UriGradleBuildFile) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UriGradleBuildFile [uri=" + uri + ", getName()=" + getName()
				+ "]";
	}
	
}
