package Characters;

import Players.Inventory;
import Quests.Room;
import org.javacord.api.entity.user.User;

public abstract class Character {
    // player's current and maximum health, respectively
    protected double health;
    protected double maxHealth;

    // testing constructor that doesn't require a discord user
    public Character() {
        this.maxHealth = 20;
        setHealth(maxHealth);
    }


    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        if (this.room != null) {
            this.room.setPlayerCount(this.room.getPlayerCount() - 1);
        }
        this.room = room;
        this.room.setPlayerCount(this.room.getPlayerCount() + 1);
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


}
