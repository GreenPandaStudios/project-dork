package Quests;
import  Players.Player;

import java.util.ArrayList;

public class Quest {

    public Quest(Map map, TurnManager turnManager){
        this.map = map;
        this.turnManager = turnManager;
    }
    private TurnManager turnManager;

    public Map getMap() {
        return map;
    }

    private Map map;

    /**
     * Returns the room of the players who's turn it currently is.
     * @return
     */
    public Room currentRoom(){
        return turnManager.currentTurn().getRoom();
    }

    /**
     * Starts the quest by initializing players stats and other things
     */
    public void startQuest(){
        //put all the players in the starting room
        for (Player p: turnManager.getPlayers()){
            p.setRoom(map.startingRoom);
        }
    }

    public void winQuest() {
        endQuest();
    }

    public void failQuest() {
        endQuest();
    }

    private void endQuest() {
        for(Player p: turnManager.getPlayers()) turnManager.removePlayer(p);
        this.map=null;
    }

}
