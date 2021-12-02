package Items;

import Players.Player;

import java.util.ArrayList;
import java.util.Random;

public class TrapItem extends UsableItem{

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    double damage = 0;

    public void setTrapMessage(String trapMessage) {
        this.trapMessage = trapMessage;
    }

    String trapMessage = "";

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    double chance = 100;
    ArrayList<String> disarmItems = new ArrayList<>();

    /**
     * Adds the item to the list of disarm items
     * @param itemName
     */
    public void addDisarmItem(String itemName){
        disarmItems.add(itemName);
    }

    /**
     * Removes the item from the list of disarm items
     * @param itemName
     */
    public void removeDisarmItem(String itemName){
        disarmItems.remove(itemName);
    }
    public TrapItem(String name){
        super();
        this.setName(name);
        damage = 0;
        trapMessage = "";
        chance = 100;
        disarmItems = new ArrayList<String>();
    }

    public TrapItem(String name, String description, String trapMessage, int uses, double damage) {
        this(name, description, trapMessage, uses, damage, 100);
    }

    public TrapItem(String name, String description, String trapMessage, int uses, double damage, double chance) {
        super(name, description, 0, 0, true, uses);
        this.damage = damage;
        this.trapMessage = trapMessage;
        this.chance = chance;
    }

    public void addDisarmItemName(String itemName) {
        disarmItems.add(itemName);
    }

    public boolean attemptDisarm(String itemName) {
        for (String disarmItem : disarmItems) {
            if(itemName.equalsIgnoreCase(disarmItem)) {
                usesLeft = 0;
                return true;
            }
        }
        return false;
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
