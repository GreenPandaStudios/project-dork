package Quests;

import Characters.Character;
import Characters.Enemy;
import Characters.NPC;
import Items.Item;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class Room implements Interfaces.IDescriptable, Interfaces.IName {


    /**
     * The items contained in this room, referenced by their unique name
     */
    HashMap<String, Item> items = new HashMap<String, Item>();
    HashMap<String, Character> characters = new HashMap<String, Character>();
    private Doorway[] doorways = {null, null, null, null, null, null};
    private String imgUrl = null;
    private String description = "";
    private String name = "";
    private int playerCount = 0;
    private int npcCount = 0;
    public ArrayList<String> enemyNames = new ArrayList<>();
    public Room() {
        setName("");
    }

    public Room(String name) {
        setName(name);
    }

    /**
     * @param characterName
     * @return the character if it is in this room or null if not
     */
    public Character getCharacter(String characterName) {
        if (characters.containsKey(characterName)) {
            return characters.get(characterName);
        }
        return null;
    }

    /**
     * Adds a character to the rooms hashmap of characters
     *
     * @param character
     * @return successful
     */
    public boolean addCharacter(Character character) {

        if (characters.containsKey(character.getName().toLowerCase())) {
            return false;
        }
        if (character instanceof NPC) {
            npcCount++;
            if (character instanceof Enemy) {
                enemyNames.add(character.getName().toLowerCase());
            }
        } else {
            playerCount++;
        }
        characters.put(character.getName().toLowerCase(), character);
        return true;
    }

    /**
     * Removes a character from the rooms hashmap of characters
     *
     * @param character
     * @return succesful
     */
    public boolean removeCharacter(Character character) {
        if (!characters.containsKey(character.getName().toLowerCase())) {
            return false;
        }
        if (characters.remove(character.getName().toLowerCase()) instanceof NPC) {
            npcCount--;
            if (character instanceof Enemy) {
                enemyNames.remove(character.getName().toLowerCase());
            }
        } else {
            playerCount--;
        }

        return true;
    }

    public int getNpcCount() {
        return npcCount;
    }

    /**
     * Sets the doorway in the provided direction
     *
     * @param doorway
     * @param direction
     * @return The room with updated doorways
     */
    public Room setDoorway(Doorway doorway, Directions direction) {
        doorways[direction.ordinal()] = doorway;
        return this;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void createImgUrl(ArrayList<String> metaTags) {
        ArrayList<String> descriptors = new ArrayList<>(metaTags);
        Collections.addAll(descriptors, getName().toLowerCase().split(" "));
        StringBuilder searchURL = new StringBuilder("https://www.istockphoto.com/photos/");
        for (int i = 0; i < descriptors.size() - 1; i++) {
            searchURL.append(descriptors.get(i)).append("-");
        }
        searchURL.append(descriptors.get(descriptors.size() - 1));
        try {
            URLConnection connection = new URL(searchURL.toString()).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            Scanner scan = new Scanner(connection.getInputStream());
            while (scan.hasNextLine()) {
                String str = scan.nextLine();
                if (str.contains("class=\"GatewayAsset-module__thumb___wN0AR\"")) {
                    Scanner scan2 = new Scanner(str);
                    while (scan2.hasNext()) {
                        String str2 = scan2.next();
                        if (str2.startsWith("src=\"https://media.istockphoto.com/")) {
                            imgUrl = str2.substring(5, str2.length() - 1);
                            break;
                        }
                    }
                }
            }
        } catch (MalformedURLException mue) {
            System.out.println("This shouldn't happen");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    @Override
    public String Description() {
        String descr = description;


        //now print out the doorways
        if (doorways[Directions.North.ordinal()] != null) {
            descr += "\nTo the north you see " + doorways[Directions.North.ordinal()].Description();
        }
        if (doorways[Directions.South.ordinal()] != null) {
            descr += "\n\nTo the south you see " + doorways[Directions.South.ordinal()].Description();
        }
        if (doorways[Directions.East.ordinal()] != null) {
            descr += "\n\nTo the east you see " + doorways[Directions.East.ordinal()].Description();
        }
        if (doorways[Directions.West.ordinal()] != null) {
            descr += "\n\nTo the west you see " + doorways[Directions.West.ordinal()].Description();
        }
        if (doorways[Directions.Up.ordinal()] != null) {
            descr += "\n\nLooking up, you see " + doorways[Directions.Up.ordinal()].Description();
        }
        if (doorways[Directions.Down.ordinal()] != null) {
            descr += "\n\nLooking down, you see " + doorways[Directions.Down.ordinal()].Description();
        }


        //print items
        if (!items.isEmpty()) {
            //see if we have anything other than scenery
            boolean onlyScenery = true;
            for (Item i : items.values()) {
                if (!i.isScenery()) {
                    onlyScenery = false;
                    break;
                }
            }

            if (!onlyScenery) {
                descr += "\n\n\nYou also see the following items:\n";
                for (Item i : items.values()) {
                    if (!i.isScenery()) {
                        descr += "\t-" + i.getName() + "\n";
                    }

                }
            }

        }
        if (!characters.isEmpty()) {
            //see if we have anything other than players
            boolean onlyPlayers = true;
            for (Characters.Character c : characters.values()) {
                if (c instanceof Characters.NPC) {
                    onlyPlayers = false;
                    break;
                }
            }

            if (!onlyPlayers) {
                descr += "\n\n\nYou also see:\n";
                for (Characters.Character c : characters.values()) {
                    if (c instanceof NPC)
                        descr += "\t-" + c.getName() + "\n";
                }
            }

        }

        return descr;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Adds the provided item to the room if it is not already contained
     *
     * @param item
     * @return The room with updated items
     */
    public Room addItem(Item item) {
        if (!items.containsKey(item.getName())) {
            items.put(item.getName().toLowerCase(), item);
        }
        return this;
    }

    /**
     * Removes an item from the room and returns it
     *
     * @param itemName
     * @return
     */
    public Item removeItem(String itemName) {
        if (items.containsKey(itemName.toLowerCase())) {
            return items.remove(itemName.toLowerCase());
        }
        return null;
    }

    /**
     * returns an item with the given name, or null if it doesn't exist
     *
     * @param itemName
     * @return
     */
    public Item peekItem(String itemName) {
        if (items.containsKey(itemName.toLowerCase())) {
            return items.get(itemName.toLowerCase());
        }
        return null;
    }

    public Doorway getDoorway(Directions direction) {
        return doorways[direction.ordinal()];
    }
}
