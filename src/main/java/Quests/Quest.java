package Quests;
import  Players.Player;

import java.util.ArrayList;

public class Quest {

    ArrayList<Player> players = new ArrayList<>();
    private int turnIndex;

    public int nextTurn(){
        return turnIndex = (turnIndex + 1) % players.size();
    }


    public  Player currentTurn(){
        return  players.get(turnIndex);
    }
}
