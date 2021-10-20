package Items;

import Players.Player;

/**
 * Applies damage to a player when added to their inventory
 */
public class HealthItem extends UsableItem {

    double health;

    public HealthItem(String name, String description, double weight, double value, boolean scenery, double health) {
        super(name,description,weight,value,scenery);
        setHealth(health);
    }

    public void setHealth(double health) {
        this.health=health;
    }

    public double getHealth() {
        return health;
    }

    @Override
    public void useItem(Player player) {
        player.setHealth(player.getHealth() + health);
        usesLeft--;
    }
}
