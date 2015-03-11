package org.genericsystem.cdi;

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.util.AnnotationLiteral;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.jboss.weld.Container;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class StartupBean implements Extension {

	private final Logger log = LoggerFactory.getLogger(StartupBean.class);

	public void onStartup(@Observes AfterDeploymentValidation event, BeanManager beanManager) throws ClassNotFoundException {
		assert Container.instance().beanDeploymentArchives().size() == 1;
		BeanDeploymentArchive archive = Container.instance().beanDeploymentArchives().keySet().iterator().next();
		log.info("------------------start initialization-----------------------");
		UserClassesProvider userClasses = getBean(UserClassesProvider.class, beanManager);
		@SuppressWarnings("serial")
		Set<Bean<?>> beans = beanManager.getBeans(Object.class, new AnnotationLiteral<Any>() {});
		for (String className : archive.getBeanClasses()) {
			Class<?> clazz = Class.forName(className);
			if (clazz.getAnnotation(SystemGeneric.class) != null) {
				log.info("Generic System: providing " + clazz);
				userClasses.addUserClasse(clazz);
			}
		}
		// Start Engine after deployment
		getBean(Engine.class, beanManager);
		// EventLauncher eventLauncher = getBean(EventLauncher.class, beanManager);
		// eventLauncher.launchStartEvent();
		log.info("-------------------end initialization------------------------");
	}

	@SuppressWarnings("unchecked")
	public static <T extends Object> T getBean(Class<T> clazz, BeanManager beanManager) {
		Bean<?> bean = beanManager.resolve(beanManager.getBeans(clazz));
		return (T) beanManager.getReference(bean, clazz, beanManager.createCreationalContext(bean));
	}
}
