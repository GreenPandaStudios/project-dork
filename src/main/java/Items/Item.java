package Items;

public class Item implements Interfaces.IName, Interfaces.IDescriptable {


    public Item(String name, String description, double weight, double value, boolean scenery) {
        setValue(value);
        setDescription(description);
        setWeight(weight);
        setName(name);
        setScenery(scenery);
    }

    public Item() {
        weight = value = 0;
        name = description = "";
    }

    public Item(String name) {
        weight = value = 0;
        description = "";
        setName(name);
    }

    private double weight = 0;
    private double value = 0;
    private String name = "";
    private String description = "";

    /**
     * If something is scenery it cannot be taken
     * And cannot be moved
     * and will not be printed out in the description
     *
     * @return
     */
    public boolean isScenery() {
        return scenery;
    }

    public Item setScenery(boolean scenery) {
        this.scenery = scenery;
        return this;
    }

    private boolean scenery = false;


    @Override
    public String Description() {
        String desc = description;
        if (!scenery)
            desc += ".\n" + "It weighs " + getWeight() + " lbs. and has a value of " + getValue();
        return desc;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        //don't let a name contain spaces
        name.replaceAll(" ", "-");
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
