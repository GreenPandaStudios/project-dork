package Items;

public class Item implements Interfaces.IName, Interfaces.IDescriptable {


    public Item(String name, String description, double weight, double value){
        setValue(value);
        setDescription(description);
        setWeight(weight);
        setName(name);
    }

    private double weight;
    private double value;
    private String name;
    private String description;



    @Override
    public String Description() {
        return description;
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
