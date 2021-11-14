package Quests;
import java.util.ArrayList;
import java.util.HashMap;

public class Map {

    //These are the "tags" for images/audio associated with this map
    private ArrayList<String> metaTags = new ArrayList<>();
    public void AddTag(String tag){
        metaTags.add( tag);
    }
    public void RemoveTag(String tag){
        metaTags.remove(tag);
    }
    public Map(){

    };

    public ArrayList<String> getMetaTags() {
        return metaTags;
    }

    public  Map(Room startingRoom, Room endingRoom){
        setStartingRoom(startingRoom);
        setEndingRoom(endingRoom);
    }

    public Room getStartingRoom() {
        return startingRoom;
    }

    public void setStartingRoom(Room startingRoom) {
        this.startingRoom = startingRoom;
        addRoom(startingRoom);
    }

    Room startingRoom;

    public Room getEndingRoom() {
        return endingRoom;
    }

    public void setEndingRoom(Room endingRoom) {

        this.endingRoom = endingRoom;
        addRoom(endingRoom);
    }

    Room endingRoom;

    HashMap<String, Room> rooms = new HashMap<>();

    public Map addRoom(Room room){

        //exception if this location is already taken
        if (rooms.containsKey(room.getName())){
            return this;
        }

        //update this room's location and add it to the hashmap
        rooms.put(room.getName(), room);
        return  this;
    }



}
