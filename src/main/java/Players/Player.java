package Players;

import Characters.Character;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class Player extends Character {

    //The Discord user associated with this player
    private User discordUser;

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

    public User getDiscordUser() {
        return discordUser;
    }

    public void setDiscordUser(User discordUser) {
        this.discordUser = discordUser;
    }

    @Override
    public String getName() {
        return discordUser.getIdAsString();
    }

    public String getUserName(Server server) {
        return discordUser.getDisplayName(server);
    }
}