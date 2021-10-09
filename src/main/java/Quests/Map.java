package Quests;
import java.util.HashMap;
import Misc.Vector3;
public class Map {

    public  Map(Room startingRoom, Room endingRoom){
        this.startingRoom = startingRoom;
        this.endingRoom = endingRoom;
        addRooom(startingRoom);
        addRooom(endingRoom);
    }

    Room startingRoom;
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
