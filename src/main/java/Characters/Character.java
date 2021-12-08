package Characters;

import Interfaces.IName;
import Items.EquippableItem;
import Players.Inventory;
import Quests.Room;
import org.javacord.api.entity.user.User;

public abstract class Character implements IName {
    // player's current and maximum health, respectively
    protected double health;
    protected double maxHealth;
    protected EquippableItem equipped;

    // testing constructor that doesn't require a discord user
    public Character() {
        this.maxHealth = 20;
        setHealth(maxHealth);
    }

    public EquippableItem equip(EquippableItem item)
    {
        inventory.removeItem(item.getName());
        if(equipped==null){
            equipped=item;
            return null;
        } else {
            EquippableItem temp = equipped;
            equipped=item;
            inventory.addItem(temp);
            return temp;
        }
    }


    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        if (this.room != null) {
            this.room.removeCharacter(this);
        }
        this.room = room;
        this.room.addCharacter(this);
    }

    //the Current room the player is in
    protected Room room;

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    //The player's inventory
    protected Inventory inventory = new Inventory(100);

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        if (health < 0) {
            this.health = 0;
        } else {
            this.health = Math.min(health, maxHealth);
        }
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    //returns true if this character is killed by the damage, false if it is still alive
    public boolean takeDamage(double damage) {
        health -= damage;
        if (health <= 0) {
            return true;
        }
        return false;
    }

}
