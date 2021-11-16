package Quests;

import Items.HealthItem;
import Items.Item;
import Items.KeyItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class DefaultQuestLoader {

    public Quest loadDefaultQuest(String questToLoad, TurnManager turnManager){

        String fileName = "src/main/resources/defaultQuests/";

        if(questToLoad == null){
            fileName += "DefaultQuest1";
        } else {
            switch (questToLoad) {
                case ("1"):
                case ("default"):
                case (""):
                    fileName += "DefaultQuest1";
                    break;
                default:
                    fileName += "DefaultQuest1";
                    break;
            }
        }
        fileName += ".txt";

        ArrayList<String> text = new ArrayList<>();
        try{
            File file = new File(fileName);
            Scanner scan = new Scanner(file);
            while (scan.hasNext()) {
                text.add(text.size(), scan.nextLine());
            }

            MapLoader loader = new MapLoader();
            Map m = loader.LoadMap(text);
            System.out.println(loader.getErrorCode());
            if (m == null) {
                return createDefaultQuest(turnManager);
            } else {
                m.locateImages();
                return new Quest(m, turnManager);
            }
        } catch (IOException e) {
            return createDefaultQuest(turnManager);
        }
    }



    public Quest createDefaultQuest(TurnManager turnManager) {
        Room startingRoom = new Room("Starting Room").addItem(new Item("Sword",
                "A heavy well-made sword",
                10.5,
                10, false))
                .addItem(new HealthItem("Amulet",
                        "A scary looking amulet",
                        2,
                        20, false,
                        1, -10))
                .addItem(new HealthItem("Potion",
                        "Restores 5 health",
                        3,
                        15, false,
                        1, 5));
        Room endingRoom = new Room(
                "Ending Room"
        );


        Room hallway = new Room("Hallway");
        hallway.setDescription("A long expanding hallway covered in paintings");
        Item paintings = new Item();
        paintings.setName("paintings");
        hallway.addItem(paintings);
        paintings.setScenery(true);
        paintings.setDescription("They are paintings of people. Their eyes seem to follow you as you move.");

        Doorway hallDoorway1 = new Doorway();
        hallDoorway1.setUnlockedDesc("it is open.");
        hallDoorway1.setToRoom(endingRoom);

        Doorway hallDoorway2 = new Doorway();
        hallDoorway2.setToRoom(startingRoom);
        hallDoorway2.setUnlockedDesc("nothing blocks your way.");

        hallway.setDoorway(hallDoorway1, Directions.South);
        hallway.setDoorway(hallDoorway2, Directions.North);


        startingRoom.addItem(new Item("torch", "A flickering torch cemented firmly into the wall.", 0, 0, true));

        hallway.addItem(new Item("Golden Apple", "A curious golden apple.", 50, 1000, false));
        endingRoom.setDescription("You are in a very dark room.");
        Doorway backUp = new Doorway(startingRoom, false);
        Doorway d = new Doorway(hallway, true, "RustyKey");
        startingRoom.addItem(new KeyItem("RustyKey", "A heavy, rusty key.", 1.0, 1.0, false));
        Doorway d1 = new Doorway(hallway, false);
        backUp.setUnlockedDesc("A stair-case winds its way upwards.");
        d.setLockedDesc("an old rusty and heavy looking door with a large padlock.");
        d.setUnlockedDesc("an old heavy door leaning open. There is a padlock on the ground beside it.");
        d1.setUnlockedDesc("Stairs winding down to nearly complete darkness. There is an ever-so faint light just beyond the point of complete darkness.");

        startingRoom.setDoorway(d, Directions.South);
        startingRoom.setDoorway(d1, Directions.Down);
        endingRoom.setDoorway(backUp, Directions.North);


        startingRoom.setDescription(/*"DEBUG NOTE FOR SEAN: Quest title is: " + quest + "\n" + */"You are standing in a dark stone chamber. There is a single torch on the wall beside you.");


        Map m = new Map(startingRoom, endingRoom);
        m.AddTag("dungeon");
        m.locateImages();

        return new Quest(m, turnManager);
    }
}
