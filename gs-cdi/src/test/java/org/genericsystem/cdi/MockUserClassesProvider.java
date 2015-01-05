package org.genericsystem.cdi;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Specializes;

import org.genericsystem.cdi.PersistenceTest.Count;

@Specializes
@Alternative
@ApplicationScoped
public class MockUserClassesProvider extends UserClassesProvider {

	@PostConstruct
	public void init() {
		userClasses.add(Count.class);
	}

}
