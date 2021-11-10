package Items;

import Players.Player;
import Quests.Doorway;

public class KeyItem extends Item{
    public KeyItem(String name, String description, double weight, double value, boolean scenery) {
        super(name, description, weight, value, scenery);
    }
    public KeyItem(String name){
        super(name);
    }
}
