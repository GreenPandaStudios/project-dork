package Quests;

import Players.Player;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;
import java.util.List;

public class TurnManager {


    public ArrayList<Player> getPlayers() {
        return players;
    }

    ArrayList<Player> players = new ArrayList<>();
    private int turnIndex = 0;

    public int nextTurn() {
        return turnIndex = (turnIndex + 1) % players.size();
    }


    public Player currentTurn() {
        return players.get(turnIndex);
    }

    /**
     * Adds a player to the turn manager
     *
     * @param player
     */
    public void addPlayer(Player player) {
        //is this player added
        players.add(players.size(), player);
        turnIndex = (turnIndex) % players.size();
    }

    /**
     * removes a player from the turn manager
     *
     * @param player
     */
    public void removePlayer(Player player) {
        players.remove(player);
        //refactor the turn index
        turnIndex = (turnIndex) % players.size();
    }

    public int numberOfPlayers() {
        return players.size();
    }

    /**
     * Get a player from a discord user
     *
     * @param user
     * @return
     */
    public Player getByUser(User user) {
        for (Player p : players) {
            if (p.getDiscordUser().equals(user)) {
                return p;
            }

        }
        return null;
    }

}
