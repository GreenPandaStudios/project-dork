package Players;

import Quests.Room;
import org.javacord.api.entity.user.User;

public class Player {

    public Player(User user){
        setDiscordUser(user);
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
        this.room = room;
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



}
