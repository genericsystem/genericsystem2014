package org.genericsystem.issuetracker.bean;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.mutability.Generic;

public abstract class AbstractBean {
	private static final Logger log = Logger.getAnonymousLogger();

	public ElStringWrapper updateHolder(Generic issue, Generic attribute) {
		return new ElStringWrapper() {

			@Override
			public void setValue(String value) {
				Generic searchedTarget = attribute.getTargetComponent();
				if (searchedTarget == null)
					issue.setHolder(attribute, value);
				else
					issue.setHolder(attribute, null, searchedTarget.getInstance(value));
			}

			@Override
			public String getValue() {
				Generic link = issue.getLinks(attribute).first();
				log.info("AbstractBean ; updateHolder ; getValue ; link : " + link.info());
				if (link != null) {
					log.info("AbstractBean ; attribute : " + attribute.info());
					log.info("AbstractBean ; updateHolder ; getValue ; return (target == null) : " + Objects.toString(link.getValue()));
					if (link.getTargetComponent() != null) {
						log.info("AbstractBean ; updateHolder ; getValue ; target : " + link.getTargetComponent().info());
						log.info("AbstractBean ; updateHolder ; getValue ; return (target != null) : " + Objects.toString(link.getTargetComponent().getValue()));
					}
					return (link.getTargetComponent() != null) ? Objects.toString(link.getTargetComponent().getValue()) : Objects.toString(link.getValue());
				}
				return null;
			}

			@Override
			public void setValues(List<String> selectedTargets) {
				for (String selectedTarget : selectedTargets)
					setValue(selectedTarget);
				for (Generic link : getTargets(attribute))
					if (!selectedTargets.contains(link.getValue()))
						link.remove();
			}

			private Snapshot<Generic> getTargets(Generic attribute) {
				return () -> issue.getHolders(attribute).get().map(x -> x.getTargetComponent() != null ? x.getTargetComponent() : x);
			}

			private List<String> getValues(Generic attribute) {
				return getTargets(attribute).get().map(x -> (String) x.getValue()).collect(Collectors.toList());
			}

			@Override
			public List<String> getValues() {
				return getValues(attribute);
			}
		};
	}

	public ElStringWrapper updateLink(Generic issue, Generic relation, Generic selectedTarget) {
		return new ElStringWrapper() {

			@Override
			public void setValue(String value) {
				if (selectedTarget == null)
					issue.setHolder(relation, null, relation.getTargetComponent().setInstance(value));
				else
					selectedTarget.getTargetComponent().updateValue(value);
			}

			@Override
			public String getValue() {
				return selectedTarget != null ? Objects.toString(selectedTarget.getTargetComponent().getValue()) : "";
			}

			@Override
			public List<String> getValues() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setValues(List<String> selectedTargets) {
				// TODO Auto-generated method stub

			}

		};
	}

	public interface ElStringWrapper {
		public String getValue();

		public void setValue(String value);

		List<String> getValues();

		void setValues(List<String> selectedTargets);
	}

}
