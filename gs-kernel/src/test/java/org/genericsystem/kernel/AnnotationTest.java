package org.genericsystem.kernel;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.kernel.Config.DefaultNoReferentialIntegrityProperty;
import org.genericsystem.kernel.Config.MetaAttribute;
import org.genericsystem.kernel.Config.MetaRelation;
import org.genericsystem.kernel.Config.SystemMap;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Dependencies;
import org.genericsystem.kernel.annotations.InstanceClass;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.Supers;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.InstanceValueClassConstraint;
import org.genericsystem.kernel.annotations.constraints.PropertyConstraint;
import org.genericsystem.kernel.annotations.constraints.SingularConstraint;
import org.genericsystem.kernel.annotations.constraints.UniqueValueConstraint;
import org.genericsystem.kernel.annotations.value.IntValue;
import org.testng.annotations.Test;

@Test
public class AnnotationTest extends AbstractTest {

	public void test001() {
		Root engine = new Root();
		Vertex metaAttribute = engine.find(MetaAttribute.class);
		Class<Vertex> systemTClass = engine.getCurrentCache().getBuilder().getSystemTClass();
		assert systemTClass.isAssignableFrom(metaAttribute.getClass()) : metaAttribute.getClass();
		assert systemTClass.isAssignableFrom(engine.find(DefaultNoReferentialIntegrityProperty.class).getClass());
		assert systemTClass.isAssignableFrom(engine.find(MetaRelation.class).getClass());
		assert systemTClass.isAssignableFrom(engine.find(SystemMap.class).getClass());
		catchAndCheckCause(() -> engine.find(MetaRelation.class).remove(), IllegalAccessException.class);
	}

	public void test001_Vertex() {
		Root engine = new Root(Vehicle.class, Human.class, Myck.class);
		Vertex vehicle = engine.find(Vehicle.class);
		Vertex human = engine.find(Human.class);
		Vertex myck = engine.find(Myck.class);
		assert vehicle.isStructural();
		assert human.isStructural();
		assert myck.isConcrete();
	}

	public void test001_remove() {
		Root engine = new Root(Vehicle.class);
		Vertex vehicle = engine.find(Vehicle.class);
		catchAndCheckCause(() -> vehicle.remove(), IllegalAccessException.class);
	}

	public void test002_remove() {
		Root engine = new Root(OtherVehicle.class);
		Vertex vehicle = engine.find(OtherVehicle.class);
		catchAndCheckCause(() -> vehicle.remove(), IllegalAccessException.class);
	}

	public void test001_instanceof() {
		Root engine = new Root(Vehicle.class);
		assert engine.find(Vehicle.class) instanceof Vehicle;
		assert engine.getInstance(Vehicle.class) instanceof Vehicle : engine.find(Vehicle.class).info() + "   " + engine.getInstance(Vehicle.class).info();
		assert engine.getInstances().get().anyMatch(x -> x instanceof Vehicle);
	}

	public void test002_instanceof() {
		Root engine = new Root(VehicleType.class);
		assert engine.find(VehicleType.class) instanceof VehicleType;
		VehicleType vehicle = engine.find(VehicleType.class);
		assert vehicle.addInstance("myBmw") instanceof VehicleInstance;
		assert vehicle.setInstance("myBmw") instanceof VehicleInstance;
		VehicleInstance vi = (VehicleInstance) vehicle.setInstance("myBmw");
	}

	public void test0022_instanceof() {
		Root engine = new Root(OtherVehicleType.class);
		Vertex vehicle = engine.find(OtherVehicleType.class);
		assert vehicle.addInstance("myBmw") instanceof VehicleInstance;
		assert vehicle.setInstance("myBmw") instanceof VehicleInstance;
		VehicleInstance vi = (VehicleInstance) vehicle.setInstance("myBmw");
	}

	public void test002_instanceof_getInstances() {
		Root engine = new Root(VehicleType.class);
		VehicleType vehicle = engine.find(VehicleType.class);
		assert vehicle.addInstance("myBmw") instanceof VehicleInstance;
		assert vehicle.getInstances().get().allMatch(x -> x instanceof VehicleInstance);
	}

	public void test003_instanceof() {
		Root engine = new Root(MyAudi.class);
		assert engine.find(MyAudi.class) instanceof VehicleInstance : engine.find(MyAudi.class).getClass();
		assert engine.find(MyAudi.class) instanceof MyAudi : engine.find(MyAudi.class).getClass();
	}

	public void test004_instanceof() {
		catchAndCheckCause(() -> new Root(MyBmw.class), InstantiationException.class);
		catchAndCheckCause(() -> new Root(MyMercedes.class), InstantiationException.class);
	}

	public static class VehicleInstance extends Vertex {

	}

	@SystemGeneric
	@Meta(VehicleType.class)
	public static class MyAudi extends VehicleInstance {}

	@SystemGeneric
	@Meta(VehicleType.class)
	public static class MyBmw extends Vertex {}

	@SystemGeneric
	@Meta(VehicleType.class)
	public static class MyMercedes {}

	@SystemGeneric
	@InstanceClass(VehicleInstance.class)
	public static class VehicleType extends Vertex {

	}

	@SystemGeneric
	@InstanceClass(VehicleInstance.class)
	public static class OtherVehicleType {

	}

	public void test002_SuperVertex() {
		Root engine = new Root(Vehicle.class, Car.class, myCar.class);
		Vertex vehicle = engine.find(Vehicle.class);
		Vertex car = engine.find(Car.class);
		Vertex myCar = engine.find(myCar.class);

		assert vehicle.isStructural();
		assert car.isStructural();
		assert vehicle.getSupers().size() == 0 : vehicle.getSupers();
		assert car.getSupers().size() == 1 : car.getSupers();
		assert car.getSupers().contains(vehicle);
		assert myCar.getSupers().size() == 0 : myCar.getSupers();
		assert myCar.getMeta().equals(car);
	}

	public void test003_Attribute() {
		Root engine = new Root(Vehicle.class, Power.class);
		Vertex vehicle = engine.find(Vehicle.class);
		Vertex power = engine.find(Power.class);
		assert power.isStructural();
		assert vehicle.getAttributes(engine).contains(power) : vehicle.getAttributes(engine);
	}

	public void test004_AttributeValue() {
		Root engine = new Root(V123.class);
		Vertex myVehicle = engine.find(MyVehicle.class);
		engine.find(V123.class);
		assert myVehicle.getValues(engine.find(Power.class)).size() == 1;
		assert myVehicle.getValues(engine.find(Power.class)).contains(new Integer(123)) : myVehicle.getValues(engine.find(Power.class));
	}

	public void test005_SuperAttribute() {
		Root engine = new Root(Car.class, ElectrikPower.class);
		Vertex car = engine.find(Car.class);
		Vertex electrikPowerCar = engine.find(ElectrikPower.class);
		assert car.getAttributes(engine).contains(electrikPowerCar) : car.getAttributes(engine);
	}

	public void test006_AttributeOnAttribute() {
		Root engine = new Root(ElectrikPower.class, Unit.class);
		Vertex electrikPowerCar = engine.find(ElectrikPower.class);
		Vertex unit = engine.find(Unit.class);
		assert unit.isCompositeOf(electrikPowerCar);
		assert unit.isStructural();
		assert electrikPowerCar.getAttributes(engine).contains(unit);
	}

	public void test007_Relation() {
		Root engine = new Root(Vehicle.class, Human.class, HumanPossessVehicle.class);
		engine.find(Vehicle.class);
		Vertex human = engine.find(Human.class);
		Vertex possess = engine.find(HumanPossessVehicle.class);
		assert human.getAttributes().contains(possess);
	}

	public void test008_SubRelation() {
		Root engine = new Root(Car.class, Human.class, HumanPossessVehicle.class, HumanPossessCar.class);
		engine.find(Car.class);
		Vertex human = engine.find(Human.class);
		Vertex possessVehicle = engine.find(HumanPossessVehicle.class);
		Vertex possessCar = engine.find(HumanPossessCar.class);
		assert possessCar.inheritsFrom(possessVehicle);
		assert human.getAttributes().contains(possessCar) : human.getAttributes();

	}

	public void test009_SymetricSuperRelation() {
		Root engine = new Root(Car.class, Human.class, Man.class, HumanPossessVehicle.class, ManPossessCar.class);
		engine.find(Car.class);
		Vertex human = engine.find(Human.class);
		Vertex man = engine.find(Man.class);
		Vertex humanPossessVehicle = engine.find(HumanPossessVehicle.class);
		Vertex manPossessCar = engine.find(ManPossessCar.class);
		assert human.getAttributes().contains(humanPossessVehicle);
		assert man.getAttributes().contains(manPossessCar) : man.getAttributes();
		assert manPossessCar.inheritsFrom(humanPossessVehicle);
	}

	public void test010_TernaryRelation() {
		Root engine = new Root(Vehicle.class, Human.class, Time.class, HumanPossessVehicleTime.class);
		engine.find(Vehicle.class);
		Vertex human = engine.find(Human.class);
		engine.find(Time.class);
		Vertex possess = engine.find(HumanPossessVehicleTime.class);
		assert human.getAttributes().contains(possess);
	}

	public void test011_getDirectSubVertexsWithDiamondProblem() {
		Root engine = new Root(GraphicComposite.class, Window.class, Selectable.class, SelectableWindow.class);
		Vertex graphicComposite = engine.find(GraphicComposite.class);
		Vertex window = engine.find(Window.class);
		Vertex selectable = engine.find(Selectable.class);
		Vertex selectableWindow = engine.find(SelectableWindow.class);

		assert selectableWindow.getSupers().size() == 2 : selectableWindow.getSupers();
		assert selectableWindow.getSupers().contains(selectable) : selectableWindow.getSupers();
		assert selectableWindow.getSupers().contains(window) : selectableWindow.getSupers();

		assert window.getSupers().size() == 1 : window.getSupers();
		assert window.getSupers().contains(graphicComposite) : window.getSupers();

		assert selectable.getSupers().size() == 1 : selectable.getSupers();
		assert selectable.getSupers().contains(graphicComposite) : selectable.getSupers();

		assert selectableWindow.getSupers().size() == 2;
		assert selectableWindow.getSupers().contains(selectable);
		assert selectableWindow.getSupers().contains(window);

		assert selectableWindow.inheritsFrom(selectable);
		assert selectableWindow.inheritsFrom(window);
		assert selectableWindow.inheritsFrom(graphicComposite);
	}

	public void test012_Value() {
		Root engine = new Root(SelectableWindow.class, Size.class, Selected.class, MySelectableWindow.class);
		Vertex selectableWindow = engine.find(SelectableWindow.class);
		Vertex size = engine.find(Size.class);
		Vertex selectedSelectable = engine.find(Selected.class);
		Vertex mySelectableWindow = engine.find(MySelectableWindow.class);
		assert mySelectableWindow.isInstanceOf(selectableWindow) : mySelectableWindow.info() + selectableWindow.info();

		assert engine.find(Selectable.class).isAncestorOf(mySelectableWindow);
		Vertex vTrue = selectedSelectable.addInstance(true, selectedSelectable.getComponents().toArray(new Vertex[1]));
		Vertex v12 = size.addInstance(12, size.getComponents().toArray(new Vertex[1]));

		assert selectableWindow.getInstances().size() == 1 : selectableWindow.getInstances();
		assert selectableWindow.getInstances().contains(mySelectableWindow);
		assert mySelectableWindow.getHolders(size).size() == 1 : mySelectableWindow.getHolders(size);
		assert mySelectableWindow.getHolders(size).contains(v12) : mySelectableWindow.getHolders(size);
		assert mySelectableWindow.getHolders(selectedSelectable).size() == 1;
		assert mySelectableWindow.getHolders(selectedSelectable).contains(vTrue);
	}

	public void test013_MultiInheritanceComplexStructural() {
		Root engine = new Root(Games.class, Children.class, Vehicle.class, Human.class, ChildrenGames.class, Transformer.class, TransformerChildrenGames.class);
		Vertex games = engine.find(Games.class);
		Vertex children = engine.find(Children.class);
		Vertex vehicle = engine.find(Vehicle.class);
		Vertex human = engine.find(Human.class);
		Vertex childrenGames = engine.find(ChildrenGames.class);
		Vertex transformer = engine.find(Transformer.class);
		Vertex transformerChildrenGames = engine.find(TransformerChildrenGames.class);

		assert transformerChildrenGames.inheritsFrom(games);
		assert transformerChildrenGames.inheritsFrom(children);
		assert transformerChildrenGames.inheritsFrom(vehicle);
		assert transformerChildrenGames.inheritsFrom(human);

		assert transformerChildrenGames.inheritsFrom(childrenGames);
		assert transformerChildrenGames.getSupers().contains(childrenGames) : transformerChildrenGames.info();
		assert transformerChildrenGames.getSupers().contains(transformer);
		assert transformerChildrenGames.getInheritings().size() == 0;
		assert transformerChildrenGames.getComposites().size() == 0;

		assert childrenGames.getSupers().contains(games);
		assert childrenGames.getSupers().contains(children);
		assert childrenGames.getInheritings().contains(transformerChildrenGames);
		assert childrenGames.getComposites().size() == 0;

		assert transformer.getSupers().contains(vehicle);
		assert transformer.getSupers().contains(human);
		assert transformer.getInheritings().contains(transformerChildrenGames);
		assert transformer.getComposites().size() == 0;
	}

	public void test014_MultiInheritanceComplexValue() {
		Root engine = new Root(MyGames.class, MyChildren.class, MyVehicle.class, Myck.class, MyChildrenGames.class, ChildrenGames.class, MyTransformer.class, Transformer.class, TransformerChildrenGames.class, MyTransformerChildrenGames.class);
		Vertex myGames = engine.find(MyGames.class);
		Vertex myChildren = engine.find(MyChildren.class);
		Vertex myVehicle = engine.find(MyVehicle.class);
		Vertex myck = engine.find(Myck.class);
		Vertex myChildrenGames = engine.find(MyChildrenGames.class);
		Vertex childrenGames = engine.find(ChildrenGames.class);
		Vertex myTransformer = engine.find(MyTransformer.class);
		Vertex transformer = engine.find(Transformer.class);
		Vertex transformerChildrenGames = engine.find(TransformerChildrenGames.class);
		Vertex myTransformerChildrenGames = engine.find(MyTransformerChildrenGames.class);

		assert myTransformerChildrenGames.isInstanceOf(transformerChildrenGames) : myTransformerChildrenGames.info() + transformerChildrenGames.info();

		assert !myTransformerChildrenGames.inheritsFrom(myGames);
		assert !myTransformerChildrenGames.inheritsFrom(myChildren);
		assert !myTransformerChildrenGames.inheritsFrom(myVehicle);
		assert !myTransformerChildrenGames.inheritsFrom(myck);
		assert !myTransformerChildrenGames.inheritsFrom(myChildrenGames);
		assert !myTransformerChildrenGames.inheritsFrom(myTransformer);
		assert myTransformerChildrenGames.getSupers().size() == 0;
		assert myTransformerChildrenGames.getInheritings().size() == 0;
		assert myTransformerChildrenGames.getComposites().size() == 0;

		assert transformer.getSupers().size() == 2;
		assert transformer.getSupers().contains(engine.find(Human.class));
		assert transformer.getSupers().contains(engine.find(Vehicle.class));

		assert transformerChildrenGames.getInstances().contains(myTransformerChildrenGames);
		assert myTransformerChildrenGames.isInstanceOf(transformerChildrenGames);

		assert transformerChildrenGames.getSupers().size() == 2;
		assert transformerChildrenGames.getSupers().contains(transformer);
		assert transformerChildrenGames.getSupers().contains(childrenGames);

		assert !myChildrenGames.inheritsFrom(myGames);
		assert !myChildrenGames.inheritsFrom(myChildren);
		assert myChildrenGames.getSupers().size() == 0;// .contains(childrenGames);
		assert myChildrenGames.getInheritings().size() == 0;
		assert myChildrenGames.getComposites().size() == 0;

		assert childrenGames.getSupers().size() == 2;
		assert childrenGames.getSupers().contains(engine.find(Games.class));
		assert childrenGames.getSupers().contains(engine.find(Children.class));

		assert childrenGames.getInstances().contains(myChildrenGames);
		assert myChildrenGames.isInstanceOf(childrenGames);

		assert !myTransformer.inheritsFrom(myVehicle);
		assert !myTransformer.inheritsFrom(myck);
		assert myTransformer.getSupers().size() == 0;// .contains(transformer);
		assert myTransformer.getInheritings().size() == 0;
		assert myTransformer.getComposites().size() == 0;

		assert transformer.getInstances().contains(myTransformer);
		assert myTransformer.isInstanceOf(transformer);
	}

	public void test015_propertyConstraint() {
		Root engine = new Root(Vehicle.class, Puissance.class);
		Vertex voiture = engine.find(Vehicle.class);
		Vertex puissance = engine.find(Puissance.class);

		assert puissance.isPropertyConstraintEnabled();
	}

	// public void test016_requiredConstraint() {
	// Root engine = new Root(Vehicle.class, Puissance.class);
	// Vertex voiture = engine.find(Vehicle.class);
	// Vertex puissance = engine.find(Puissance.class);
	//
	// assert puissance.isRequiredConstraintEnabled(Statics.NO_POSITION);
	// }

	public void test017_singularConstraint() {
		Root engine = new Root(Vehicle.class, Puissance.class);
		Vertex voiture = engine.find(Vehicle.class);
		Vertex puissance = engine.find(Puissance.class);

		assert puissance.isSingularConstraintEnabled(0);
	}

	public void test018_uniqueValueConstraint() {
		Root engine = new Root(Vehicle.class, Puissance.class);
		Vertex voiture = engine.find(Vehicle.class);
		Vertex puissance = engine.find(Puissance.class);

		assert puissance.isUniqueValueEnabled();
	}

	public void test019_uniqueClassConstraint() {
		Root engine = new Root(Vehicle.class, Puissance.class);
		Vertex voiture = engine.find(Vehicle.class);
		Vertex puissance = engine.find(Puissance.class);

		assert puissance.getClassConstraint().equals(Integer.class);
	}

	public void test020_dependencies() {
		Root engine = new Root(Voiture.class);
		Vertex puissance = engine.find(Puissance.class);
		Vertex couleur = engine.find(Couleur.class);
		assert puissance instanceof Puissance;
		assert couleur instanceof Couleur;
	}

	@SystemGeneric
	public static class Games extends Vertex {}

	@SystemGeneric
	@Meta(Games.class)
	public static class MyGames extends Vertex {}

	@SystemGeneric
	@Meta(Games.class)
	public static class MyGames2 extends Vertex {}

	@SystemGeneric
	public static class Children extends Vertex {}

	@SystemGeneric
	@Meta(Children.class)
	public static class MyChildren extends Vertex {}

	@SystemGeneric
	@Supers({ Games.class, Children.class })
	public static class ChildrenGames extends Vertex {}

	@SystemGeneric
	@Meta(ChildrenGames.class)
	public static class MyChildrenGames extends Vertex {}

	@SystemGeneric
	@Supers({ Human.class, Vehicle.class })
	public static class Transformer extends Vertex {}

	@SystemGeneric
	@Meta(Transformer.class)
	public static class MyTransformer extends Vertex {}

	@SystemGeneric
	@Supers({ Transformer.class, ChildrenGames.class })
	public static class TransformerChildrenGames extends Vertex {}

	@SystemGeneric
	@Meta(TransformerChildrenGames.class)
	public static class MyTransformerChildrenGames extends Vertex {}

	@SystemGeneric
	public static class GraphicComposite extends Vertex {

	}

	@SystemGeneric
	@Components(GraphicComposite.class)
	public static class Size extends Vertex {

	}

	@SystemGeneric
	@Supers(GraphicComposite.class)
	public static class Window extends GraphicComposite {

	}

	@SystemGeneric
	@Supers(GraphicComposite.class)
	public static class Selectable extends Vertex {

	}

	@SystemGeneric
	@Components(Selectable.class)
	public static class Selected extends Vertex {

	}

	@SystemGeneric
	@Supers({ Selectable.class, Window.class })
	public static class SelectableWindow extends Vertex {

	}

	@SystemGeneric
	@Meta(SelectableWindow.class)
	public static class MySelectableWindow extends Vertex {

	}

	@SystemGeneric
	public static class Vehicle extends Vertex {

	}

	@SystemGeneric
	public static class OtherVehicle {

	}

	@SystemGeneric
	@Meta(Vehicle.class)
	public static class MyVehicle extends Vertex {}

	@SystemGeneric
	@Components(Vehicle.class)
	public static class Power extends Vertex {

	}

	@SystemGeneric
	@Components(Vehicle.class)
	@PropertyConstraint
	@SingularConstraint(ApiStatics.BASE_POSITION)
	// @RequiredConstraint
	@UniqueValueConstraint
	@InstanceValueClassConstraint(Integer.class)
	@Dependencies(Couleur.class)
	public static class Puissance extends Vertex {

	}

	public static class Couleur extends Vertex {

	}

	@Dependencies(Puissance.class)
	public static class Voiture extends Vertex {

	}

	@SystemGeneric
	@Meta(Power.class)
	@Components(MyVehicle.class)
	@IntValue(123)
	public static class V123 extends Vertex {

	}

	@SystemGeneric
	@Supers(Vehicle.class)
	public static class Car extends Vertex {

	}

	@SystemGeneric
	@Meta(Car.class)
	public static class myCar extends Vertex {}

	@SystemGeneric
	@Components(Car.class)
	@Supers(Power.class)
	public static class ElectrikPower extends Vertex {

	}

	@SystemGeneric
	@Components(ElectrikPower.class)
	public static class Unit extends Vertex {

	}

	@SystemGeneric
	public static class Human extends Vertex {}

	@SystemGeneric
	public static class Man extends Human {}

	@SystemGeneric
	@Meta(Human.class)
	public static class Myck extends Vertex {}

	@SystemGeneric
	public static class Time extends Vertex {}

	@SystemGeneric
	@Components({ Human.class, Vehicle.class })
	public static class HumanPossessVehicle extends Vertex {}

	@SystemGeneric
	@Components({ Human.class, Car.class })
	@Supers(HumanPossessVehicle.class)
	public static class HumanPossessCar extends HumanPossessVehicle {}

	@SystemGeneric
	@Components({ Man.class, Car.class })
	@Supers(HumanPossessVehicle.class)
	public static class ManPossessCar extends HumanPossessVehicle {}

	@SystemGeneric
	@Components({ Human.class, Vehicle.class, Time.class })
	public static class HumanPossessVehicleTime extends Vertex {}

}
