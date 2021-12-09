package Quests;

import Characters.Character;
import Characters.Enemy;
import Characters.Merchant;
import Items.*;
import Players.Inventory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class DefaultQuestLoader {

    public Quest loadDefaultQuest(String questToLoad, TurnManager turnManager) {
        //TODO: Remove
        //TEMPORARY
        return creatFinalDemoQuest(turnManager);



        String fileName = "src/main/resources/defaultQuests/";

        if (questToLoad == null) {
            fileName += "DefaultQuest1";
        } else {
            switch (questToLoad) {
                case ("1"):
                case ("default"):
                case (""):
                    fileName += "DefaultQuest1";
                    break;
                case ("2"):
                case ("maze"):
                    fileName += "DefaultQuest2";
                    break;
                case ("3"):
                case ("bear"):
                    fileName += "DefaultQuest3";
                    break;
                default:
                    fileName += "DefaultQuest1";
                    break;
            }
        }

        ArrayList<String> text = new ArrayList<>();
        try {
            File file = new File(fileName);
            Scanner scan = new Scanner(file);
            while (scan.hasNext()) {
                text.add(text.size(), scan.nextLine());
            }

            MapLoader loader = new MapLoader();
            Map m = loader.LoadMap(text);
            System.out.println(loader.getErrorCode()); //For testing purposes
            if (m == null) {
                return createDefaultQuest(turnManager);
            } else {
                m.locateImages();
                return new Quest(m, turnManager);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return createDefaultQuest(turnManager);
        }
    }


    public Quest createDefaultQuest(TurnManager turnManager) {
        Room startingRoom = new Room("Starting Room").addItem(new WeaponItem("Sword",
                        "A heavy well-made sword",
                        10.5,
                        10, false, 4))
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

        Item[] items = new Item[1];
        items[0] = new Item("Merchant Apple", "A curious golden apple.", 50, 1000, false);
        Character merchant = new Merchant("Test Merchant", 200, items);
        startingRoom.addCharacter(merchant);

        Character enemy = new Enemy("Test Enemy", 100, 10, 90);
        startingRoom.addCharacter(enemy);

        startingRoom.setDescription(/*"DEBUG NOTE FOR SEAN: Quest title is: " + quest + "\n" + */"You are standing in a dark stone chamber. There is a single torch on the wall beside you.");


        Map m = new Map(startingRoom, endingRoom);
        m.AddTag("dungeon");
        m.locateImages();

        return new Quest(m, turnManager);
    }
    public Quest creatFinalDemoQuest(TurnManager turnManager){
        Room startingRoom = new Room("Starting Room");
        Room armory = new Room("Armory");
        startingRoom.setDescription("A cold cellar with a strange man standing inside.");


        WeaponItem sword = new WeaponItem("Sword",
                "A large heavy sword, perfectly balanced at the hilt.",
                10.5,
                10, false, 50);
        WeaponItem dagger = new WeaponItem("Dagger",
                "A small but dangerous looking jagged dagger.",
                2.5,
                10, false, 50);



        armory.setDescription("It appears to be a long-abandoned armory. There are piles of rusted armor and weapons." +
                " While most of the items are too rotted to be much use, a few look as though" +
                "they could still be useful");

        HealthItem potion = new HealthItem("Potion",
                "A small inscription reads:\n'Restores 5 health'",
                3,
                15, false,
                1, 5);



        KeyItem skeletonKey = new KeyItem("skeleton key");
        skeletonKey.setDescription("An old key made of darkened bone.");
        skeletonKey.setValue(500.0);
        skeletonKey.setWeight(1.0);

        Merchant merchant = new Merchant("old traveller");
        merchant.setHealth(100.0);
        merchant.getInventory().setMaxWeight(500000.0);
        merchant.getInventory().addItem(skeletonKey);
        merchant.getInventory().setGold(500.0);
        merchant.getInventory().addItem(potion);
        merchant.setRoom(startingRoom);

        Item goldenApple = new Item("Golden Apple");
        goldenApple.setValue(1000.0);
        goldenApple.setWeight(1.0);


        armory.addItem(dagger).addItem(sword).addItem(goldenApple);



        Room endingRoom = new Room(
                "Ending Room"
        );

        Doorway toArmory = new Doorway();
        TrapItem poisonDart = new TrapItem("darts");
        poisonDart.setTrapMessage("The trap appears broken");
        poisonDart.setChance(100.0);
        poisonDart.setUsesLeft(1);
        poisonDart.setDescription("Darts quickly shoot from the wall.");
        poisonDart.setDamage(1.0);

        toArmory.setTrap(poisonDart);
        toArmory.setUnlockedDesc("an old wooden door frame. The door has long been rotted away.");
        toArmory.setLocked(false);
        toArmory.setToRoom(armory);
        startingRoom.setDoorway(toArmory, Directions.North);


        Doorway fromArmory = new Doorway();
        toArmory.setUnlockedDesc("an old wooden door frame. The door has long been rotted away.");
        toArmory.setLocked(false);
        toArmory.setToRoom(startingRoom);
        armory.setDoorway(fromArmory, Directions.South);


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
        hallDoorway2.setUnlockedDesc("the heavy door is leaning open.");
        hallDoorway2.setLockedDesc("an old and heavy-looking door is locked shut. You try opening it, but it will not move.");
        hallDoorway2.setLocked(true);
        hallDoorway2.setKeyName("skeleton key");

        startingRoom.setDoorway(hallDoorway2, Directions.East);

        hallway.setDoorway(hallDoorway1, Directions.East);
        ////////NICK, ADD ENEMIES HERE
        Enemy enemy1 = new Enemy("enemy", 10.0, 1.0, 1.0);



        enemy1.setRoom(hallway);
        //////////////////

        Map m = new Map(startingRoom, endingRoom);
        m.AddTag("dungeon");
        m.AddTag("cellar");
        m.locateImages();

        return new Quest(m, turnManager);
    }

}
