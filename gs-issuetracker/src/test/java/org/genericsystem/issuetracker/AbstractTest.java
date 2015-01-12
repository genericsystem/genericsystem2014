package org.genericsystem.issuetracker;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.apache.deltaspike.core.api.message.MessageContext;
import org.genericsystem.cdi.Engine;
import org.genericsystem.issuetracker.crud.Issue;
import org.genericsystem.issuetracker.crud.IssueCRUD;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTest extends Arquillian {

	static protected Logger log = LoggerFactory.getLogger(AbstractTest.class);

	@Inject
	protected Engine engine;

	@Deployment
	public static WebArchive createTestArchive() {
		return new Archiver(Issue.class, IssueCRUD.class, IssueDTO.class, IssueWS.class).archive();
	}

	public static class Archiver {
		WebArchive archive = ShrinkWrap.create(WebArchive.class);

		public Archiver(Class<?>... classes) {
			BeansXml beanXml = new BeansXml();
			archive.addClasses(classes);
			archive.addPackage("org.genericsystem.cdi");
			archive.addPackage("org.genericsystem.cdi.event");
			archive.addPackage("org.apache.deltaspike.core.impl.scope.window");
			archive.addPackage("org.apache.deltaspike.core.impl.message");
			archive.addPackage("org.apache.deltaspike.core.impl.scope.conversation");
			archive.addPackage("org.apache.deltaspike.core.impl.scope.viewaccess");
			archive.addPackage(MessageContext.class.getPackage());
			// archive.addPackage(FacesContextProvider.class.getPackage());
			archive.addAsManifestResource(beanXml.byteArraySet(), ArchivePaths.create("beans.xml"));
		}

		public WebArchive archive() {
			return archive;
		}
	}

	public static class BeansXml {

		private final Set<String> interceptors = new HashSet<String>();
		private final Set<String> alternativeStereotypes = new HashSet<String>();

		public BeansXml addInterceptors(Class<?>... classes) {
			for (Class<?> clazz : classes)
				interceptors.add(clazz.getName());
			return this;
		}

		public BeansXml addAlternativeSterotypes(Class<?>... classes) {
			for (Class<?> clazz : classes)
				alternativeStereotypes.add(clazz.getName());
			return this;
		}

		@Override
		public String toString() {
			String xml = "<beans>\n";
			for (String interceptor : interceptors)
				xml += "<interceptors><class>" + interceptor + "</class></interceptors>\n";
			xml += "<alternatives>\n";
			if (!alternativeStereotypes.isEmpty())
				for (String alternativeStereotype : alternativeStereotypes)
					xml += "<stereotype>" + alternativeStereotype + "</stereotype>\n";
			xml += "</alternatives>\n";
			return xml += "</beans>\n";
		}

		public ByteArrayAsset byteArraySet() {
			return new ByteArrayAsset(toString().getBytes());
		}
	}

}