package Items;

public abstract class UsableItem extends Item{

    public int getUsesLeft() {
        return usesLeft;
    }

    //If -1, unlimited number of uses before destroying
    protected int usesLeft = -1;

    public abstract void useItem();

}
