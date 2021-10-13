package Quests;

import Items.Item;

import java.util.Hashtable;

public class MapLoader {
    Hashtable<String, Room> declaredRooms;
    Hashtable<String, Doorway> declaredDoorways;
    Hashtable<String, Item> declaredItems;
    private Map map;

    public MapLoader() {
        map = new Map();
        declaredRooms = new Hashtable<>();
        declaredDoorways = new Hashtable<>();
        declaredItems = new Hashtable<>();
    }

    private void createCommand(String createWhat, String name) {
        switch (createWhat.toLowerCase()) {
            case "room":
                Room r = new Room();
                r.setName(name);
                declaredRooms.put(name, r);
                break;
            case "doorway":
                Doorway d = new Doorway();
                declaredDoorways.put(name, d);
                break;
            case "item":
                Item i = new Item();
                i.setName(name);
                declaredItems.put(name, i);
                break;
        }
    }

}
