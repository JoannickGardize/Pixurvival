package fr.sharkhendrix.pixurvival.core.contentPack;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import fr.sharkhendrix.pixurvival.core.util.ListViewOfMap;

public class Dependencies {

	private Map<String, Dependency> dependencies = new HashMap<>();

	public Collection<Dependency> all() {
		return Collections.unmodifiableCollection(dependencies.values());
	}

	public Dependency byRef(String ref) {
		return dependencies.get(ref);
	}

	@XmlElement(name = "dependency")
	public List<Dependency> getDependenciesListView() {
		return new ListViewOfMap<String, Dependency>(dependencies, Dependency::getRef);
	}
}
