package org.genericsystem.cdi;

import javax.inject.Inject;

import org.genericsystem.cache.Cache;
import org.testng.annotations.Test;

@Test
public class CacheTest extends AbstractTest {

	@Inject
	Cache cache;
	static int hashCode;

	public void test001() {
		assert cache != null;
		hashCode = System.identityHashCode(cache);
	}

	@Test(dependsOnMethods = "test001")
	public void test002() {
		assert hashCode == System.identityHashCode(cache);

	}
}
