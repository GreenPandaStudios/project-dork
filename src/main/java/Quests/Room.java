package Quests;
import Items.Item;
import  Misc.Vector3;

import java.util.HashMap;
import java.util.Iterator;

public class Room implements Interfaces.IDescriptable, Interfaces.IName{





    public Room(Vector3 location){
        setLocation(location);
    }

    /**
     * The items contained in this room, referenced by there unique name
     */
    HashMap<String, Item> items  = new HashMap<String, Item>();


    private Doorway[] doorways = {null, null, null, null, null, null};

    /**
     * Sets the doorway in the provided direction
     * @param doorway
     * @param direction
     * @return The room with updated doorways
     */
    public Room setDoorway(Doorway doorway, Directions direction){
        doorways[direction.ordinal()] = doorway;
        return  this;
    }


    public Vector3 getLocation() {
        return location;
    }

    public void setLocation(Vector3 location) {
        this.location = location;
    }

    private  Vector3 location;
    private  String description;
    private String name;

    //TODO
    @Override
    public String Description() {
        String descr = description;


        //now print out the doorways
        if (doorways[Directions.North.ordinal()] != null){
            descr+="\nTo the north you see " + doorways[Directions.North.ordinal()].Description().toLowerCase();
        }
        if (doorways[Directions.South.ordinal()] != null){
            descr+="\n\nTo the south you see " + doorways[Directions.South.ordinal()].Description();
        }
        if (doorways[Directions.East.ordinal()] != null){
            descr+="\n\nTo the east you see " + doorways[Directions.East.ordinal()].Description();
        }
        if (doorways[Directions.West.ordinal()] != null){
            descr+="\n\nTo the west you see " + doorways[Directions.West.ordinal()].Description();
        }
        if (doorways[Directions.Up.ordinal()] != null){
            descr+="\n\nLooking up, you see " + doorways[Directions.Up.ordinal()].Description();
        }
        if (doorways[Directions.Down.ordinal()] != null){
            descr+="\n\nLooking down, you see " + doorways[Directions.Down.ordinal()].Description();
        }


        //print items
        if (!items.isEmpty()){
            descr += "\n\n\nYou also see the following items:\n";
            for (Item i :items.values()){
                descr += "\t-" + i.getName() + "\n";
            }
        }

        return descr;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds the provided item to the room if it is not already contained
     * @param item
     * @return The room with updated items
     */
    public Room addItem(Item item){
        if (!items.containsKey(item.getName())){
            items.put(item.getName().toLowerCase(),item);
        }
        return this;
    }

    /**
     * Removes an item from the room and returns it
     * @param itemName
     * @return
     */
    public Item removeItem(String itemName){
        if (items.containsKey(itemName.toLowerCase())) {
            return items.remove(itemName.toLowerCase());
        }
        return null;
    }

    /**
     * returns an item with the given name, or null if it doesn't exist
     * @param itemName
     * @return
     */
    public Item peekItem(String itemName){
        if (items.containsKey(itemName.toLowerCase())) {
            return items.get(itemName.toLowerCase());
        }
        return null;
    }

    public Doorway getDoorway(Directions direction) {
        return doorways[direction.ordinal()];
    }
}
