package org.genericsystem.cdi;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.cache.Engine;
import org.genericsystem.cdi.event.EventLauncher;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testenricher.cdi.container.CDIExtension;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTest extends Arquillian {

	protected static Logger log = LoggerFactory.getLogger(AbstractTest.class);

	@Inject
	Engine engine;

	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class);
		// javaArchive.addClasses(SerializableCache.class, GenericProvider.class, EngineProvider.class, CdiFactory.class);
		javaArchive.addClasses(UserClassesProvider.class, PersistentDirectoryProvider.class, MockPersistentDirectoryProvider.class, EventLauncher.class, CacheProvider.class, EngineProvider.class);
		javaArchive.addAsServiceProvider(Extension.class, CDIExtension.class);
		createBeansXml(javaArchive);
		return javaArchive;
	}

	private static void createBeansXml(JavaArchive javaArchive) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<beans xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\" http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/beans_1_0.xsd\">");
		// stringBuilder.append("<specializes> ");
		// stringBuilder.append("<class>org.genericsystem.cdi.MockPersistentDirectoryProvider</class>");
		// stringBuilder.append("</alternatives>");
		stringBuilder.append("</beans>");
		javaArchive.addAsManifestResource(new StringAsset(stringBuilder.toString()), "beans.xml");
	}

	public abstract static class RollbackCatcher {
		public void assertIsCausedBy(Class<? extends Throwable> clazz) {
			try {
				intercept();
			} catch (RollbackException ex) {
				if (ex.getCause() == null)
					throw new IllegalStateException("Rollback Exception has not any cause", ex);
				if (!clazz.isAssignableFrom(ex.getCause().getClass()))
					throw new IllegalStateException("Cause of rollback exception is not of type : " + clazz.getSimpleName(), ex);

				log.info("Caught exception : " + ex.getCause());
				return;
			} catch (Exception ex) {
				if (!clazz.isAssignableFrom(ex.getClass()))
					throw new IllegalStateException("Cause of exception is not of type : " + clazz.getSimpleName(), ex);
				return;
			}
			assert false : "Unable to catch any rollback exception!";
		}

		public abstract void intercept();
	}
}
