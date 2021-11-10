package Items;

import Players.Player;

public abstract class UsableItem extends Item {
    public UsableItem(){};
    public UsableItem(String name, String description, double weight, double value, boolean scenery, int uses) {
        super(name, description, weight, value, scenery);
        setUsesLeft(uses);
    }

    public int getUsesLeft() {
        return usesLeft;
    }

    public void setUsesLeft(int uses) {
        usesLeft = uses;
    }

    //If -1, unlimited number of uses before destroying
    protected int usesLeft = -1;

    public abstract void useItem(Player player);

}
