package Quests;

import Characters.*;
import Items.HealthItem;
import Items.Item;
import Items.KeyItem;
import Items.UsableItem;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapLoader {


    //////////////////////////////REGEX constants
    private final Pattern codeCommentPattern = Pattern.compile("(^(\\s*(//)))");
    private final Pattern whitespace = Pattern.compile("^\\s*\\s*$\\s*");
    private final Pattern metaTagPattern = Pattern.compile("^#(?<tag>.+)");
    //region Room commands

    private final Pattern createRoomPattern = Pattern.compile("^\\s*create\\s+room\\s+(?<roomName>.+)$");
    private final Pattern setStartRoomPattern = Pattern.compile("^\\s*set\\s+start\\s+room\\s+to\\s+(?<roomName>.+)$");
    private final Pattern setEndRoomPattern = Pattern.compile("^\\s*set\\s+end\\s+room\\s+to\\s+(?<roomName>.+)$");
    private final Pattern connectRoomsPattern = Pattern.compile("\\s*set\\s+(?<fromRoom>.+)\\s+direction\\s+(?<direction>.+)\\s+to\\s+(?<toRoom>.+)\\s+via\\s+(?<doorwayName>.+)");
    private final Pattern setRoomDescription = Pattern.compile("\\s*set\\s+room\\s+(?<roomName>.+)\\s+" +
            "description\\s+to\\s+\"(?<roomDescription>.+)\"");
    private final Pattern addItemPattern = Pattern.compile("\\s*add\\s+(?<itemName>.+)\\s+to\\s+(?<roomName>.+)");

    //endregion
    //region Item commands

    private final String allItemFunction = "(item|healthItem|key)";

    private final Pattern createItemPattern = Pattern.compile("\\s*create\\s+item\\s+(?<itemName>.+)\\s*");
    private final Pattern createKeyPattern = Pattern.compile("\\s*create\\s+key\\s+(?<itemName>.+)\\s*");
    private final Pattern createHealthItemPattern = Pattern.compile("\\s*create\\s+healthItem\\s+(?<itemName>.+)\\s*");

    private final Pattern setHealth = Pattern.compile("\\s*^set\\s+healthItem\\s+(?<itemName>.+)\\s+health\\s+to\\s+(?<health>(-*\\d+((.|,)\\d+))?)");
    private final Pattern setUses = Pattern.compile("\\s*^set\\s+(?<itemName>.+)\\s+uses\\s+to\\s+(?<uses>(\\d+))");
    private final Pattern setScenery = Pattern.compile("\\s*^set\\s+(?<itemName>.+)\\s+scenery\\s+to\\s+(?<bool>(true|false))$");

    //region Apply to all items
    private final Pattern setItemDescription = Pattern.compile("\\s*set\\s+" + allItemFunction + "\\s+(?<itemName>.+)\\s+" +
            "description\\s+to\\s+\"(?<itemDescription>.+)\"");
    private final Pattern setItemWeight = Pattern.compile("\\s*set\\s+" + allItemFunction + "\\s+(?<itemName>.+)\\s+" +
            "weight\\s+to\\s+(?<itemWeight>(\\d+((.|,)\\d+))?)");
    private final Pattern setItemValue = Pattern.compile("\\s*set\\s+" + allItemFunction + "\\s+(?<itemName>.+)\\s+" +
            "value\\s+to\\s+(?<itemWeight>(^\\d*\\.\\d+|\\d+\\.\\d*$))");

    //endregion


    //endregion Item


    //region Doorway commands
    private final Pattern setDoorwayLocked = Pattern.compile("^\\s*set\\s+(?<doorway>.+)\\s+locked\\s+to\\s+(?<bool>true|false)$");


    private final Pattern createDoorwayPattern = Pattern.compile("\\s*create\\s+doorway\\s+(?<doorwayName>.+)\\s*");


    private final Pattern setlockedDescr = Pattern.compile("\\s*set\\s+(?<doorway>.+)\\s+" +
            "locked\\s+description\\s+to\\s+\"(?<descr>.+)\"");

    private final Pattern setUnlockedDesc = Pattern.compile("\\s*set\\s+(?<doorway>.+)\\s+" +
            "unlocked\\s+description\\s+to\\s+\"(?<descr>.+)\"");

    private final Pattern setKey = Pattern.compile("\\s*set\\s+(?<doorway>.+)\\s+" +
            "key\\s+to\\s+(?<key>.+)");


    //endregion
    //region Character commands
    private final Pattern createMerchant = Pattern.compile("^\\s*create\\s+merchant\\s+(?<merchant>.+)$");
    private final String allCharacterFunction = "(character|merchant|enemy)";

    private final Pattern addCharacterToRoom = Pattern.compile("^\\s*add\\s+" + allCharacterFunction+
            "\\s+(?<character>.+)\\s+to\\s+room\\s+(?<room>.+)$");
    //adds an item to the characters inventory
    private final Pattern addItemToCharacter= Pattern.compile("^\\s*give\\s+item\\s+(?<item>.+)\\s+to\\s+" + allCharacterFunction +"\\s+(?<character>.+)$");
    private final Pattern setCharacterGold = Pattern.compile("^\\s*set\\s+" + allCharacterFunction + "\\s+(?<character>.+)\\s+gold\\s+to\\s+(?<gold>(-*\\d+((.|,)\\d+))$)");

    //endregion

    /////////////////////////////////////////
    Hashtable<String, Room> declaredRooms;
    Hashtable<String, Doorway> declaredDoorways;
    Hashtable<String, Item> declaredItems;
    Hashtable<String, Characters.Character> declaredCharacters;

    private String errorCode;
    private Map map;
    public MapLoader() {
        map = new Map();
        declaredRooms = new Hashtable<>();
        declaredDoorways = new Hashtable<>();
        declaredItems = new Hashtable<>();
        declaredCharacters = new Hashtable<>();
    }

    public String getErrorCode() {
        return errorCode;
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
        if ((m = metaTagPattern.matcher(line)).find()) {
            //this is a meta tag, add it to the map
            map.AddTag(m.group("tag"));
            return 0;
        }
        //create a new merchant
        if ((m = createMerchant.matcher(line)).find()){
            if (declaredCharacters.containsKey(m.group("merchant"))) {

                errorCode = "The character has already been created";
                return -1;
            }
            declaredCharacters.put(m.group("merchant"), new Characters.Merchant(m.group("merchant")));
            return 0;
        }
        //add a character to a room
        if ((m = addCharacterToRoom.matcher(line)).find()){
            if (declaredCharacters.containsKey(m.group("character"))){
                if (declaredRooms.containsKey(m.group("room"))){
                    declaredRooms.get(m.group("room")).addCharacter(
                            declaredCharacters.get(m.group("character"))
                    );
                    return 0;
                }
                else{
                    errorCode = "The room " + m.group("room") + " does not exist!";
                    return -1;
                }
            }
            else{
                errorCode = "The character " + m.group("character") + " does not exist!";
                return -1;
            }
        }

        //add an item to a character's inventory
        if ((m = addItemToCharacter.matcher(line)).find()){
            if (declaredCharacters.containsKey(m.group("character"))) {
                if (declaredItems.containsKey(m.group("item"))){

                    //add the item to the characters inventory
                    declaredCharacters.get(m.group("character")).getInventory()
                            .addItem(declaredItems.get(m.group("item")));
                    return 0;
                }
                else{
                    errorCode = "The item " + m.group("item") + " does not exist!";
                    return -1;
                }
            }
             else{
                    errorCode = "The character " + m.group("character") + " does not exist!";
                    return -1;
             }
        }

        //set a characters gold
        if ((m = setCharacterGold.matcher(line)).find()){
            if (declaredCharacters.containsKey(m.group("character"))) {
                try{
                    Double gold = Double.parseDouble(m.group("gold"));
                    //add the item to the characters inventory
                    declaredCharacters.get(m.group("character")).getInventory()
                            .setGold(gold);
                    return 0;
                }
                catch (Exception e){
                    errorCode = "Gold value should be a double, " + m.group("gold") + "is not a double.";
                    return -1;
                }
            }
            else{
                errorCode = "The character " + m.group("character") + " does not exist!";
                return -1;
            }
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

                errorCode = "The Item has already been created";
                return -1;
            }
            declaredItems.put(m.group("itemName"), new Item(m.group("itemName")));
            return 0;
        }
        if ((m = createKeyPattern.matcher(line)).find()) {
            //create a new key
            if (declaredItems.containsKey(m.group("itemName"))) {

                errorCode = "The key has already been created";
                return -1;
            }
            declaredItems.put(m.group("itemName"), new KeyItem(m.group("itemName")));
            return 0;
        }
        if ((m = createHealthItemPattern.matcher(line)).find()) {
            //create a new item
            if (declaredItems.containsKey(m.group("itemName"))) {

                errorCode = "The Item has already been created";
                return -1;
            }
            declaredItems.put(m.group("itemName"), new HealthItem(m.group("itemName")));
            return 0;
        }
        if ((m = setHealth.matcher(line)).find()) {
            //create a new item
            if (!declaredItems.containsKey(m.group("itemName"))) {

                errorCode = "The health item does not exist";
                return -1;
            }

            if (declaredItems.get(m.group("itemName")) instanceof HealthItem) {
                try {
                    ((HealthItem) declaredItems.get(m.group("itemName"))).setHealth(Double.parseDouble(m.group("health")));
                    return 0;
                } catch (Exception e) {
                    errorCode = "Expected health as double.";
                    return -1;
                }

            } else {
                errorCode = "The item is not a health item.";
                return -1;
            }
        }
        if ((m = setUses.matcher(line)).find()) {
            //create a new item
            if (!declaredItems.containsKey(m.group("itemName"))) {

                errorCode = "The usable item does not exist";
                return -1;
            }

            if (declaredItems.get(m.group("itemName")) instanceof UsableItem) {
                try {
                    ((UsableItem) declaredItems.get(m.group("itemName"))).setUsesLeft(Integer.parseInt(m.group("uses")));
                    return 0;
                } catch (Exception e) {
                    errorCode = "Expected uses as an integer.";
                    return -1;
                }

            } else {
                errorCode = "The item is not a usable item.";
                return -1;
            }
        }
        if ((m = setScenery.matcher(line)).find()) {
            //create a new item
            if (!declaredItems.containsKey(m.group("itemName"))) {

                errorCode = "The Item \"" + m.group("itemName") + "\" does not exist";
                return -1;
            }
            if (m.group("bool").equalsIgnoreCase("true")) {
                declaredItems.get(m.group("itemName")).setScenery(true);
            } else {
                declaredItems.get(m.group("itemName")).setScenery(false);
            }
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
                errorCode = "The room \"" + m.group("fromRoom") + "\" does not exist";
                return -1;
            }
            if (declaredRooms.containsKey(m.group("toRoom"))) {
                toRoom = declaredRooms.get(m.group("toRoom"));
            } else {
                errorCode = "The room \"" + m.group("toRoom") + "\" does not exist";
                return -1;
            }

            //the rooms exist
            //See if the doorway exists
            if (declaredDoorways.containsKey(m.group("doorwayName"))) {
                d = declaredDoorways.get(m.group("doorwayName"));
            } else {
                errorCode = "The doorway \"" + m.group("doorwayName") + "\" does not exist";
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
                errorCode = "The item does" + m.group("itemName") +"  not exist.";
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
                errorCode = "The room \"" + m.group("roomName") + "\" does not exist.";
                return -1;
            }


            //set the maps starting room
            map.setStartingRoom(declaredRooms.get(m.group("roomName")));

            return 0;
        }
        if ((m = setEndRoomPattern.matcher(line)).find()) {


            //make sure both the item and room exist
            if (!declaredRooms.containsKey(m.group("roomName"))) {
                errorCode = "The room \"" + m.group("roomName") + "\" does not exist.";
                return -1;
            }


            //set the maps starting room
            map.setEndingRoom(declaredRooms.get(m.group("roomName")));

            return 0;
        }
        if ((m = createDoorwayPattern.matcher(line)).find()) {
            if (declaredDoorways.containsKey(m.group("doorwayName"))) {
                errorCode = "The doorway exists.";
                return -1;
            }

            declaredDoorways.put(m.group("doorwayName"), new Doorway());
            return 0;
        }
        if ((m = setKey.matcher(line)).find()) {
            if (!declaredDoorways.containsKey(m.group("doorway"))) {
                errorCode = "The doorway \"" + m.group("doorway") + "\" does not exist.";
                return -1;
            }
            if (!declaredItems.containsKey(m.group("key"))) {
                errorCode = "The item \"" + m.group("key") + "\" does not exist.";

                return -1;
            }

            //is this item a key?
            if (!(declaredItems.get(m.group("key")) instanceof KeyItem)) {
                errorCode = "The item \"" + m.group("key") + "\" is not a key.";

                return -1;
            }

            declaredDoorways.get(m.group("doorway")).setKeyName(m.group("key"));
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
            try {
                declaredItems.get(m.group("itemName")).setWeight(Double.parseDouble(m.group("itemWeight")));
            } catch (Exception e) {
                errorCode = "Expected a double for weight value.";
                return -1;
            }

            return 0;
        }
        if ((m = setItemValue.matcher(line)).find()) {
            if (!declaredItems.containsKey(m.group("itemName"))) {
                errorCode = "The item \"" + m.group("itemName") + "\" doesn't exist.";
                return -1;
            }
            try {
                declaredItems.get(m.group("itemName")).setValue(Double.parseDouble(m.group("itemWeight")));
            } catch (Exception e) {
                errorCode = "Expected a double for weight value.";
                return -1;
            }

            return 0;
        }
        if ((m = setDoorwayLocked.matcher(line)).find()) {
            if (!declaredDoorways.containsKey(m.group("doorway"))) {
                errorCode = "Doorway \"" + m.group("doorway") + "\" does not exist.";
                return -1;
            }

            if (m.group("bool").equalsIgnoreCase("true")) {
                declaredDoorways.get(m.group("doorway")).setLocked(true);

            } else if (m.group("bool").equalsIgnoreCase("false")) {
                declaredDoorways.get(m.group("doorway")).setLocked(false);

            } else {
                errorCode = "Expected a boolean value, either 'true' or 'false'.";
                return -1;
            }


            return 0;

        }
        if ((m = setlockedDescr.matcher(line)).find()) {
            if (!declaredDoorways.containsKey(m.group("doorway"))) {
                errorCode = "Doorway \"" + m.group("doorway") + "\" does not exist.";
                return -1;
            }
            declaredDoorways.get(m.group("doorway")).setLockedDesc(m.group("descr"));

            return 0;

        }
        if ((m = setUnlockedDesc.matcher(line)).find()) {
            if (!declaredDoorways.containsKey(m.group("doorway"))) {
                errorCode = "Doorway \"" + m.group("doorway") + "\" does not exist.";
                return -1;
            }
            declaredDoorways.get(m.group("doorway")).setUnlockedDesc(m.group("descr"));

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


    public Map LoadMap(ArrayList<String> fileLines) {
        int lineNumber = 1;
        boolean commentBlock = false;
        for (int i = 0; i < fileLines.size(); i++) {

            String line = fileLines.get(i);

            //are we in a comment block?
            if (commentBlock) {
                if (line.endsWith("*/")) {
                    commentBlock = false;
                }
                continue;
            }
            if (line.startsWith("/*")) {
                if (!line.endsWith("*/")) commentBlock = true;
                continue;
            }


            //should we merge these into one "line"

            if (line.contains("{")) {

                while (i < fileLines.size() && !fileLines.get(i).contains("}")) {
                    line += " " + fileLines.get(++i);
                }
                line = line.replaceAll("[{]", "").replaceAll("[}]", "");
            }


            if (compileLine(line) == -1) {
                //there was an error
                errorCode += "\nLine Number: " + (i + 1) + ":  " + line;

                return null;
            }
        }


        //make sure the map has a starting and ending room
        if (map.getStartingRoom() == null) {
            errorCode = "You must specify a start room for a map!";
            return null;
        }
        if (map.getEndingRoom() == null) {
            errorCode = "You must specify an end room for a map!";
            return null;
        }
        return map;
    }

}
