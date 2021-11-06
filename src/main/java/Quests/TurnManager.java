package Quests;

import Players.Player;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;

public class TurnManager {


    ArrayList<Player> players = new ArrayList<>();
    private int turnIndex = 0;

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int nextTurn() {
        if (players.size() == 0 ) return 0;

        int turnIndexTemp = (turnIndex + 1) % players.size();
        turnIndex = turnIndexTemp;
        if (players.get(turnIndexTemp).getHealth() == 0) {
            return -1; //return -1 to signal to the message parser that this player is incapacitated
        } else {
            return turnIndex;
        }
    }

    /**
     * Removes all users
     */
    public void purgePlayers() {
        players = new ArrayList<>();
    }

    /**
     * @param player
     * @return bool whether player can act or not
     */
    public boolean canAct(Player player) {
        return player.getHealth() > 0;
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
     * Removes a player from the turn manager
     *
     * @param player
     */
    public void removePlayer(Player player) {
        players.remove(player);
        //refactor the turn index
        turnIndex = players.size() > 0 ?
                (turnIndex) % players.size() : 0;

    }

    /**
     * @return number of players joined
     */
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
