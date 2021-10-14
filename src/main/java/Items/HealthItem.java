package Items;

/**
 * Grants health to the player when placed in their inventory
 */
public class HealthItem extends UsableItem {
    @Override
    public void useItem() {


        usesLeft--;
    }
}
