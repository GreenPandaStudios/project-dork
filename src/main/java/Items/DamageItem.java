package Items;

/**
 * Applies damage to a player when added to their inventory
 */
public class DamageItem extends UsableItem {
    @Override
    public void useItem() {


        usesLeft--;
    }
}
