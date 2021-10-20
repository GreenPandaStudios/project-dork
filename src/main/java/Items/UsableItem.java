package Items;

import Players.Player;

public abstract class UsableItem extends Item{

    public UsableItem(String name, String description, double weight, double value, boolean scenery) {
        super(name,description,weight,value,scenery);
    }

    public int getUsesLeft() {
        return usesLeft;
    }

    //If -1, unlimited number of uses before destroying
    protected int usesLeft = -1;

    public abstract void useItem(Player player);

}
