package Quests;

import Items.Item;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapLoader {


    //////////////////////////////REGEX constants

    private final Pattern codeCommentPattern = Pattern.compile("(^(\\s*(//)))");
    private final Pattern whitespace = Pattern.compile("^\\s*\\s*$\\s*");

    private final Pattern createRoomPattern = Pattern.compile("\\s*create\\s+room\\s+(?<roomName>.+)");
    private final Pattern setStartRoomPattern = Pattern.compile("\\s*set\\s+start\\s+room\\s+to\\s+(?<roomName>.+)");
    private final Pattern setEndRoomPattern = Pattern.compile("\\s*set\\s+end\\s+room\\s+to\\s+(?<roomName>.+)");
    private final Pattern createItemPattern = Pattern.compile("\\s*create\\s+item\\s+(?<itemName>.+)\\s*");
    private final Pattern createDoorwayPattern = Pattern.compile("\\s*create\\s+doorway\\s+(?<doorwayName>.+)\\s*");
    private final Pattern connectRoomsPattern = Pattern.compile("\\s*set\\s+(?<fromRoom>.+)\\s+direction\\s+(?<direction>.+)\\s+to\\s+(?<toRoom>.+)\\s+via\\s+(?<doorwayName>.+)");
    private final Pattern addItemPattern = Pattern.compile("\\s*add\\s+(?<itemName>.+)\\s+to\\s+(?<roomName>.+)");
    private final Pattern setItemDescription = Pattern.compile("\\s*set\\s+item\\s+(?<itemName>.+)\\s+" +
            "description\\s+to\\s+\"(?<itemDescription>.+)\"");
    private final Pattern setRoomDescription = Pattern.compile("\\s*set\\s+room\\s+(?<roomName>.+)\\s+" +
            "description\\s+to\\s+\"(?<roomDescription>.+)\"");
    private final Pattern setItemWeight = Pattern.compile("\\s*set\\s+item\\s+(?<itemName>.+)\\s+" +
            "weight\\s+to\\s+(?<itemWeight>(^\\d*\\.\\d+|\\d+\\.\\d*$))");
    private final Pattern setItemValue = Pattern.compile("\\s*set\\s+item\\s+(?<itemName>.+)\\s+" +
            "value\\s+to\\s+(?<itemWeight>(^\\d*\\.\\d+|\\d+\\.\\d*$))");
    /////////////////////////////////////////


    private String errorCode;
    public String getErrorCode(){
        return  errorCode;
    }
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

    private int compileLine(String line) {
        Matcher m;


        if ((m = codeCommentPattern.matcher(line)).find()) {
            //ignore, this is a code comment
            return 0;
        }
        if ((m = whitespace.matcher(line)).find()) {
            //ignore, this is whitespace
            return 0;
        }
        //create a new room
        if ((m = createRoomPattern.matcher(line)).find()) {
            //create a new Room
            if (declaredRooms.containsKey(m.group("roomName"))) {

                errorCode = "The Room has already been created";
                return -1;
            }
            declaredRooms.put(m.group("roomName"), new Room(m.group("roomName")));

            //add it to the map
            map.addRoom(declaredRooms.get(m.group("roomName")));
            return 0;
        }
        if ((m = createItemPattern.matcher(line)).find()) {
            //create a new item
            if (declaredItems.containsKey(m.group("itemName"))) {

                errorCode =  "The Item has already been created";
                return -1;
            }
            declaredItems.put(m.group("itemName"), new Item(m.group("itemName")));
            return 0;
        }
        if ((m = connectRoomsPattern.matcher(line)).find()) {

            //make sure the rooms exist
            Room fromRoom;
            Room toRoom;
            Doorway d;
            if (declaredRooms.containsKey(m.group("fromRoom"))) {
                fromRoom = declaredRooms.get(m.group("fromRoom"));
            } else {
                errorCode = "The room \"" + m.group("fromRoom") +"\" does not exist";
                return -1;
            }
            if (declaredRooms.containsKey(m.group("toRoom"))) {
                toRoom = declaredRooms.get(m.group("toRoom"));
            } else {
                errorCode = "The room \"" + m.group("toRoom") +"\" does not exist";
                return -1;
            }

            //the rooms exist
            //See if the doorway exists
            if (declaredDoorways.containsKey(m.group("doorwayName"))) {
                d = declaredDoorways.get(m.group("doorwayName"));
            } else {
                errorCode = "The doorway \"" +  m.group("doorwayName") + "\" does not exist";
                return -1;
            }

            //get the direction
            Directions dir = getDirectionFromString(m.group("direction"));
            if (dir == null) {
                errorCode = "Invalid direction";
                return -1;
            }


            //we have everything we need
            d.setToRoom(toRoom);
            fromRoom.setDoorway(d, dir);
            return 0;
        }
        if ((m = addItemPattern.matcher(line)).find()) {


            //make sure both the item and room exist
            if (!declaredItems.containsKey(m.group("itemName"))) {
                errorCode = "The item does not exist.";
                return -1;
            }
            if (!declaredRooms.containsKey(m.group("roomName"))) {
                errorCode = "The room does not exist";
                return -1;
            }

            //add the item to the room
            declaredRooms.get(m.group("roomName")).
                    addItem(declaredItems.get(m.group("itemName")));

            return 0;
        }
        if ((m = setStartRoomPattern.matcher(line)).find()) {


            //make sure both the item and room exist
            if (!declaredRooms.containsKey(m.group("roomName"))) {
                errorCode = "The room \"" + m.group("roomName") +"\" does not exist.";
                return -1;
            }


            //set the maps starting room
            map.setStartingRoom(declaredRooms.get(m.group("roomName")));

            return 0;
        }
        if ((m = setEndRoomPattern.matcher(line)).find()) {


            //make sure both the item and room exist
            if (!declaredRooms.containsKey(m.group("roomName"))) {
                errorCode = "The room \"" + m.group("roomName") +"\" does not exist.";
                return -1;
            }


            //set the maps starting room
            map.setEndingRoom(declaredRooms.get(m.group("roomName")));

            return 0;
        }
        if ((m = createDoorwayPattern.matcher(line)).find()) {
            if (declaredDoorways.containsKey(m.group("doorwayName"))) {
                errorCode =  "The doorway exists.";
                return -1;
            }

            declaredDoorways.put(m.group("doorwayName"), new Doorway());
            return 0;
        }
        //set item description
        if ((m = setItemDescription.matcher(line)).find()) {
            if (!declaredItems.containsKey(m.group("itemName"))) {
                errorCode = "The item doesn't exist.";
                return -1;
            }

            declaredItems.get(m.group("itemName")).setDescription(m.group("itemDescription"));
            return 0;
        }
        if ((m = setRoomDescription.matcher(line)).find()) {
            if (!declaredRooms.containsKey(m.group("roomName"))) {
                errorCode = "The room doesn't exist.";
                return -1;
            }

            declaredRooms.get(m.group("roomName")).setDescription(m.group("roomDescription"));
            return 0;
        }
        //set item weight
        if ((m = setItemWeight.matcher(line)).find()) {
            if (!declaredItems.containsKey(m.group("itemName"))) {
                errorCode = "The item doesn't exist.";
                return -1;
            }
            try{
                declaredItems.get(m.group("itemName")).setWeight(Double.parseDouble(m.group("itemWeight")));
            }
            catch (Exception e){
                errorCode = "Expected a double for weight value.";
                return -1;
            }

            return 0;
        }
        if ((m = setItemValue.matcher(line)).find()) {
            if (!declaredItems.containsKey(m.group("itemName"))) {
                errorCode = "The item \"" +m.group("itemName") +  "\" doesn't exist.";
                return -1;
            }
            try{
                declaredItems.get(m.group("itemName")).setValue(Double.parseDouble(m.group("itemWeight")));
            }
            catch (Exception e){
                errorCode = "Expected a double for weight value.";
                return -1;
            }

            return 0;
        }

        //ERROR: invalid line
        errorCode = "Invalid line";
        return -1;
    }


    private Directions getDirectionFromString(String string) {
        switch (string.toLowerCase()) {
            case "n":
            case "north":
                return Directions.North;
            case "s":
            case "south":
                return Directions.South;
            case "e":
            case "east":
                return Directions.East;
            case "w":
            case "west":
                return Directions.West;
            case "u":
            case "up":
                return Directions.Up;
            case "d":
            case "down":
                return Directions.Down;
            default:
                return null;
        }
    }



    public Map LoadMap(ArrayList<String> fileLines){
        int lineNumber = 1;
        for (String line : fileLines) {
            if (compileLine(line) == -1){
                //there was an error
                errorCode += "\nLine Number: " + lineNumber + ":  " + line;

                return  null;
            }
            lineNumber++;
        }


        //make sure the map has a starting and ending room
        if (map.getStartingRoom() == null){
            errorCode = "You must specify a start room for a map!";
            return  null;
        }
        if (map.getEndingRoom() == null){
            errorCode = "You must specify an end room for a map!";
            return  null;
        }
        return map;
    }

}
