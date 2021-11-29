package Players;

import Characters.Character;
import Interfaces.IName;
import Quests.Room;
import org.javacord.api.entity.user.User;

public class Player extends Character {

    public Player(User user) {
        setDiscordUser(user);
        this.maxHealth = 20;
        setHealth(maxHealth);
    }

    // testing constructor that doesn't require a discord user
    public Player() {
        this.maxHealth = 20;
        setHealth(maxHealth);
    }

    /**
     * Sets the current Players's room to this room and updates the room with that info
     * @param room
     */
    @Override
    public void setRoom(Room room) {
        if (this.room != null) {
            this.room.setPlayerCount(this.room.getPlayerCount() - 1);
            this.room.removeCharacter(this);
        }
        this.room = room;
        this.room.setPlayerCount(this.room.getPlayerCount() + 1);
        this.room.addCharacter(this);
    }

    public User getDiscordUser() {
        return discordUser;
    }

    public void setDiscordUser(User discordUser) {
        this.discordUser = discordUser;
    }

    //The Discord user associated with this player
    private User discordUser;


    @Override
    public String getName() {
        return discordUser.getIdAsString();
    }
}
