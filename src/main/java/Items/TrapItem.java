package Items;

import Players.Player;

import java.util.Random;

public class TrapItem extends UsableItem{

    double damage = 0;
    String trapMessage = "";
    double chance = 100;

    public TrapItem(String name, String description, String trapMessage, int uses, double damage) {
        this(name, description, trapMessage, uses, damage, 100);
    }

    public TrapItem(String name, String description, String trapMessage, int uses, double damage, double chance) {
        super(name, description, 0, 0, true, uses);
        this.damage = damage;
        this.trapMessage = trapMessage;
        this.chance = chance;
    }

    @Override
    public void useItem(Player player) {
        player.setHealth(player.getHealth() - damage);
        usesLeft--;
    }

    public String getTrapMessage() {
        return trapMessage;
    }
}
