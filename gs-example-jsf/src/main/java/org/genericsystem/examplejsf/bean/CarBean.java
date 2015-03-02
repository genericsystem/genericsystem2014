package org.genericsystem.examplejsf.bean;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.cdi.Engine;
import org.genericsystem.examplejsf.crud.Car;
import org.genericsystem.examplejsf.crud.Power;
import org.genericsystem.mutability.Generic;

@Named
@RequestScoped
public class CarBean {

	@Inject
	private Engine engine;

	private Generic car;
	private Generic power;

	private String newCarName;
	private Integer newCarPower;

	@PostConstruct
	public void init() {
		car = engine.find(Car.class);
		power = engine.find(Power.class);
	}

	public List<Generic> getCars() {
		return car.getAllInstances().get().collect(Collectors.toList());
	}

	public ValueExpressionWrapper getPower(Generic instance) {
		return new ValueExpressionWrapper() {
			@Override
			public String getValue() {
				// Power is a property constraint
				return Objects.toString(instance.getValues(power).first());
			}

			@Override
			public void setValue(String value) {
				// The value power must be an integer due the InstanceValueClassConstraint
				instance.setHolder(power, Integer.parseInt(value));
			}
		};
	}

	public String addCar() {
		car.setInstance(newCarName).setHolder(power, newCarPower);
		return "#";
	}

	public String update() {
		return "#";
	}

	public String deleteCar(Generic car) {
		car.remove();
		return "#";
	}

	public String flush() {
		engine.getCurrentCache().flush();
		return "#";
	}

	public String clear() {
		engine.getCurrentCache().clear();
		return "#";
	}

	public static interface ValueExpressionWrapper {
		public String getValue();

		public void setValue(String value);
	}

	public String getNewCarName() {
		return newCarName;
	}

	public void setNewCarName(String newCarName) {
		this.newCarName = newCarName;
	}

	public Integer getNewCarPower() {
		return newCarPower;
	}

	public void setNewCarPower(Integer newCarPower) {
		this.newCarPower = newCarPower;
	}

}
