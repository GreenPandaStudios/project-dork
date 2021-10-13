package Players;

import Quests.Room;
import org.javacord.api.entity.user.User;

public class Player {

    // player's current and maximum health, respectively
    private double health;
    private final double maxHealth;

    public Player(User user){
        setDiscordUser(user);
        this.maxHealth=20;
        setHealth(maxHealth);
    }

    // testing constructor that doesn't require a discord user
    public Player(){
        this.maxHealth=20;
        setHealth(maxHealth);
    }

    public User getDiscordUser() {
        return discordUser;
    }

    public void setDiscordUser(User discordUser) {
        this.discordUser = discordUser;
    }

    //The Discord user associated with this player
    private User discordUser;

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room.setPlayerCount(this.room.getPlayerCount()-1);
        this.room = room;
        this.room.setPlayerCount(this.room.getPlayerCount()+1);
    }

    //the Current room the player is in
    private Room room;

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    //The player's inventory
    private  Inventory inventory = new Inventory(100);

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        if(health<0){
            this.health=0;
        } else {
            this.health = Math.min(health, maxHealth);
        }
    }

    public double getMaxHealth() {
        return maxHealth;
    }
}
