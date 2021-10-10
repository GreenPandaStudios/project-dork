package Quests;
import java.util.HashMap;
import java.util.List;

import Misc.Vector3;
import Players.Player;

public class Map {


    public Map(){

    };

    public  Map(Room startingRoom, Room endingRoom){
        setStartingRoom(startingRoom);
        setEndingRoom(endingRoom);
    }

    public Room getStartingRoom() {
        return startingRoom;
    }

    public void setStartingRoom(Room startingRoom) {
        this.startingRoom = startingRoom;
        addRooom(startingRoom);
    }

    Room startingRoom;

    public Room getEndingRoom() {
        return endingRoom;
    }

    public void setEndingRoom(Room endingRoom) {

        this.endingRoom = endingRoom;
        addRooom(endingRoom);
    }

    Room endingRoom;

    HashMap<Vector3, Room> rooms = new HashMap<>();

    public Map addRooom(Room room){

        //exception if this location is already taken
        if (rooms.containsKey(room.getLocation())){
            throw new UnsupportedOperationException();
        }

        //update this room's location and add it to the hashmap
        room.setLocation(room.getLocation());
        rooms.put(room.getLocation(), room);
        return  this;
    }



}
