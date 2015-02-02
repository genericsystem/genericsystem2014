package org.genericsystem.example;

import org.genericsystem.cdi.CacheRequestProvider;
import org.genericsystem.cdi.CacheSessionProvider;
import org.genericsystem.cdi.EngineProvider;
import org.genericsystem.cdi.PersistentDirectoryProvider;
import org.genericsystem.cdi.UserClassesProvider;
import org.genericsystem.example.CDIUses.Color;
import org.genericsystem.example.CDIUses.Options;
import org.genericsystem.example.CDIUses.Vehicle;
import org.genericsystem.example.CDIUses.VehicleColor;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public abstract class AbstractTest extends Arquillian {
	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class);
		javaArchive.addClasses(UserClassesProvider.class, PersistentDirectoryProvider.class, CacheSessionProvider.class, CacheRequestProvider.class, EngineProvider.class, Vehicle.class, Options.class, Color.class, VehicleColor.class);
		createBeansXml(javaArchive);
		return javaArchive;
	}

	private static void createBeansXml(JavaArchive javaArchive) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<beans xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\" http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/beans_1_0.xsd\">");
		stringBuilder.append("</beans>");
		javaArchive.addAsManifestResource(new StringAsset(stringBuilder.toString()), "beans.xml");
	}
}
