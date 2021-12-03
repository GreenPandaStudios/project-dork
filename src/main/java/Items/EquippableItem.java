package Items;

public class EquippableItem extends Item {
    int slot;

    public EquippableItem(String name, String description, double weight, double value, boolean scenery, int slot) {
        super(name, description, weight, value, scenery);
        setSlot(slot);
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }
}
