package Players;

import Items.Item;

import java.util.ArrayList;
import java.util.HashMap;

public class Inventory {

    public Inventory(double maxWeight) {
        setMaxWeight(maxWeight);
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    private double maxWeight;

    public double getCurrentWeight() {
        return currentWeight;
    }

    private double currentWeight;
    private HashMap<String, Item> items = new HashMap<>();

    /**
     * Try to add the item to the inventory
     * returns true if successful and false if not
     *
     * @return
     */
    public boolean addItem(Item item) {
        if (items.containsKey(item.getName().toLowerCase())) {
            //we already have this item, don't add it
            return false;
        }
        //do we have enough space?
        if (getCurrentWeight() + item.getWeight() > maxWeight) {
            //we don't have enough space
            return false;
        }

        //add the item, it is safe
        items.put(item.getName().toLowerCase(), item);
        //increment weight
        currentWeight += item.getWeight();
        return true;
    }

    /**
     * Removes and returns the item with the given name, if we have it
     * If we don't have it, returns null
     *
     * @return
     */
    public Item removeItem(String itemName) {
        //do we have an item with this name?
        if (items.containsKey(itemName.toLowerCase())) {
            //return the item
            Item i = items.remove(itemName.toLowerCase());
            //decrement weight
            currentWeight -= i.getWeight();
            return i;
        }
        return null;
    }

    /**
     * Returns a string of the inventory's contents
     * @return A string of the inventory's content
     */
    public String displayItems() {
        if(items.isEmpty()){
            return "Your inventory is empty!\n";
        }
        String listOfItems = "Your inventory contains:\n";
        for (Item i : items.values()) {
            listOfItems += "\t-" + i.getName() + "\n"
            + "\t\t Weight: " + i.getWeight() + ", Value: " + i.getValue() + "\n";
        }
        return listOfItems;
    }

    public String displayWeight(){
        return "Current Weight: " + getCurrentWeight() + " / " + getMaxWeight() + "\n";
    }

    public Item peekItem(String itemName) {
        if (items.containsKey(itemName.toLowerCase())) {
            return items.get(itemName.toLowerCase());
        }
        return null;
    }

}
