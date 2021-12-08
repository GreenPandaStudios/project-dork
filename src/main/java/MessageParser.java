import Characters.Character;
import Items.*;
import Players.Inventory;
import Players.Player;
import Quests.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageSet;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Characters.*;
public class MessageParser {

    private final Server server;
    private final TextChannel validTextChannel;
    private final TurnManager turnManager = new TurnManager();
    private final DefaultQuestLoader defaultQuestLoader = new DefaultQuestLoader();
    private final Pattern inspectObjectPattern = Pattern.compile("^(look|examine|study|inspect|peek)(\\s*)(?<item>.*)$");
    private final Pattern takePattern = Pattern.compile("^(grab|collect|store|steal|take)(\\s+)(?<item>.*)$");

    //////////////////////////////REGEX constants
    private final Pattern movePattern = Pattern.compile("^(run|walk|go|travel|move)(\\s+)(?<direction>.*)$");
    private final Pattern dropPattern = Pattern.compile("^(drop|throw|remove|leave)(\\s+)(?<item>.*)$");
    private final Pattern endTurnPattern = Pattern.compile("^(end|done|next|finish)(\\s+turn|my turn|move|my move|\\s)*$");
    private final Pattern usePattern = Pattern.compile("^(use|activate)(\\s+)(?<item>.*)$");
    private final Pattern inventoryPattern = Pattern.compile("^(inventory|i|items)$");
    private final Pattern statusPattern = Pattern.compile("^(what is my)?(status|health)$");
    private final Pattern helpPattern = Pattern.compile("^(((I (need|want))?help)|(I'm)?confused|(What are the)?commands)$([?])?");
    private final Pattern givePattern = Pattern.compile("^(give)(\\s+)(?<item>.*)(?= to )( to )(?<player>.*)$");
    private final Pattern startPattern = Pattern.compile("^(start quest)(\\s+)(?<quest>.*)|(start quest)$");
    private final Pattern attackPattern1 = Pattern.compile("^(attack)(\\s+)(?<target>.*)(?= with)( with )(?<weapon>.*)$");
    //private final Pattern attackPattern2 = Pattern.compile("^(attack)(\\s+)(?<target>.*)$");
    private final Pattern sellPattern = Pattern.compile("^sell(\\s+)(?<item>.+)(\\s+)to(\\s+)(?<merchant>.+)");
    private final Pattern buyPattern = Pattern.compile("^buy(\\s+)(?<item>.+)(\\s+)from(\\s+)(?<merchant>.+)");


    DiscordApi api;
    private Quest currentQuest = null;
    /////////////////////////////////////////


    public MessageParser(DiscordApi api, TextChannel validTextChannel, Server server) {

        // Hook up the listeners
        api.addMessageCreateListener(this::onMessageCreate);

        this.validTextChannel = validTextChannel;
        this.api = api;
        this.server = server;
    }

    // The listener for each message coming in
    public void onMessageCreate(MessageCreateEvent event) {
        // First, validate the channel this message was typed in
        if (validTextChannel != event.getChannel()) {
            return;
        }

        // Now make sure it is not the bot sending a message
        if (event.getMessageAuthor().isYourself()) {
            return;
        }

        // There is no active quest
        if (currentQuest == null) {

            preQuestPartyManagement(event);
        } else {
            // If it is this player's turn
            if (event.getMessageAuthor().asUser().get().equals(turnManager.currentTurn().getDiscordUser())) {
                parseUsingRegex(packageMessage(event.getMessageContent()), event);
                // Not this users turn
            } else {
                // Delete message
                event.deleteMessage();
                // Send them a direct message letting them know it's not their turn, as to not clog the chat
                event.getMessageAuthor().asUser().get().openPrivateChannel().thenApplyAsync(channel -> channel.sendMessage(
                        event.getMessageAuthor().asUser().get().getDisplayName(server) + ", please refrain from sending messages while it is not your turn."));
            }
        }
    }


    private void parseUsingRegex(String message, MessageCreateEvent event) {
        Matcher m;
        //help
        if ((m = helpPattern.matcher(message)).find()) {
            displayHelp(event);
            return;
        }

        if ((m = givePattern.matcher(message)).find()) {

            //Loop through all players
            for (Player receiver : turnManager.getPlayers()) {
                //If player name matches provided name, attempt to give item to reciever
                if (receiver.getDiscordUser().getDisplayName(server).toLowerCase().equals(m.group("player"))) {
                    if (turnManager.currentTurn().getRoom() == receiver.getRoom()) {
                        sendMessage(turnManager.currentTurn().getInventory().giveItem(m.group("item"), receiver, server));
                        return;
                    }
                    sendMessage("The two of you aren't in the same room!\n");
                    return;
                }
            }

            sendMessage("No player with that name is currently in the game.\n");
            return;
        }

        //inspect object
        if ((m = inspectObjectPattern.matcher(message)).find()) {
            if (!m.group().equals("")) {
                if (m.group(3).equals("inventory")) {
                    displayInventory();
                } else {
                    inspectAction(m.group(3));
                }
            }
            return;
        }
        //take object
        if ((m = takePattern.matcher(message)).find()) {
            if (!m.group(3).equals("")) {
                takeAction(m.group(3));
            } else {
                sendMessage("What would you like to take?");
            }
            return;
        }
        //drop object
        if ((m = dropPattern.matcher(message)).find()) {
            if (!m.group("item").equals("")) {
                removeAction(m.group("item"));
            } else {
                sendMessage("What would you like to remove from your inventory?");
            }
            return;
        }
        //use object
        if ((m = usePattern.matcher(message)).find()) {
            if (!m.group("item").equals("")) {
                useAction(m.group("item"));
            } else {
                sendMessage("What would you like to use?");
            }
            return;
        }

        //move
        if ((m = movePattern.matcher(message)).find()) {
            if (!m.group("direction").equals("")) {
                moveAction(m.group("direction"));
            } else {
                sendMessage("Where would you like to move to?");
            }
            return;
        }
        //buyAction
        if ((m = buyPattern.matcher(message)).find()){

            //check that the merchant is in the room
            Characters.Character merchant = currentQuest.currentRoom().getCharater(m.group("merchant"));
            String itemName = m.group("item");
            if (merchant == null){
                sendMessage("There is no " + m.group("merchant") + " here.");
                return;
            }
            if (merchant instanceof Merchant){
                //check if the merchant has the item
                if (merchant.getInventory().peekItem(itemName) == null) {
                    sendMessage("The merchant does not have " + itemName + ".");
                    return;
                }
                //check that we have enough gold to purchase it
                if (merchant.getInventory().peekItem(itemName).getValue() > turnManager.currentTurn().getInventory().getGold()){
                    sendMessage("You do not have enough gold to purchase " + itemName + ".");
                    return;
                }

                //buy the item
                ((Merchant) merchant).Buy(turnManager.currentTurn().getInventory(), itemName);
                sendMessage("You purchased " + itemName + ".");
                return;
            }
            else{
                sendMessage(m.group("merchant") + " is not a Merchant.");
                return;
            }

        }
        //sellAction
        if ((m = sellPattern.matcher(message)).find()){
            //check that the merchant is in the room
            Characters.Character merchant = currentQuest.currentRoom().getCharater(m.group("merchant"));
            String itemName = m.group("item");
            if (merchant == null){
                sendMessage("There is no " + m.group("merchant") + " here.");
                return;
            }
            if (merchant instanceof Merchant){
                //check if we have the item
                if (turnManager.currentTurn().getInventory().peekItem(itemName) == null) {
                    sendMessage("You do not have " + itemName + ".");
                    return;
                }
                //check that the merchant has enough gold to purchase it
                if (turnManager.currentTurn().getInventory().peekItem(itemName).getValue()
                        > merchant.getInventory().getGold()){
                    sendMessage("The merchant does not have enough gold to purchase " + itemName + ".");
                    return;
                }

                //sell the item
                ((Merchant) merchant).Sell(turnManager.currentTurn().getInventory(), turnManager.currentTurn().getInventory().peekItem(itemName));
                sendMessage("You sold " + itemName + " to " + merchant.getName() + ".");
                return;
            }
            else{
                sendMessage(m.group("merchant") + " is not a Merchant.");
                return;
            }

        }



        //status
        if ((m = statusPattern.matcher(message)).find()) {
            statusAction();
            return;
        }
        if ((m = endTurnPattern.matcher(message)).find()) {
            endTurn();
            return;
        }
        if ((m = inventoryPattern.matcher(message)).find()) {
            displayInventory();
            return;
        }
        if((m = attackPattern1.matcher(message)).find()){
            attackAction(m.group("target"), m.group("weapon"));
            return;
        }
        /*if((m = attackPattern2.matcher(message)).find()){
            attackAction(m.group("target"), "default");
            return;
        }*/

        sendMessage("I don't understand " + "\"" + message + "\"");
    }

    /**
     * Packages a message to be valid and be able to be parsed by the
     * parseValidMessage Method
     *
     * @param message
     * @return
     */
    private String packageMessage(String message) {
        message = message.toLowerCase();

        message = message.replaceAll("\\s+(the|an|a|at|my)+\\s+", " ");


        return message;
    }


    private String remove(String str, String regexToRemove) {
        return str.replaceAll(regexToRemove, "");

    }

    //Depreciated
    /*
    private Quest createDefaultQuest() {
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


        startingRoom.setDescription("You are standing in a dark stone chamber. There is a single torch on the wall beside you.");


        Map m = new Map(startingRoom, endingRoom);

        return new Quest(m, turnManager);
    }
     */

    //DEPRECATED
    /* DEPRECATED
    private void parseValidMessage(String[] words) {
        if (words.length > 0) {
            switch (words[0]) {
                //"inspect" synonyms
                case "look":
                case "examine":
                case "study":
                case "peek":
                case "inspect":
                    if (words.length > 1) {
                        if (words[1].equals("inventory")) {
                            displayInventory();
                        } else {
                            inspectAction(words[1]);
                        }
                    } else {
                        //assume we are talking about the room
                        sendMessage("You take in your surroundings. " + currentQuest.currentRoom().Description());
                    }
                    break;
                //"take" synonyms
                case "grab":
                case "collect":
                case "store":
                case "steal":
                case "take":
                    if (words.length > 1) {
                        takeAction(words[1]);
                    } else {
                        sendMessage("What would you like to take?");
                    }
                    break;
                //"use" synonyms
                case "use":
                case "activate":
                    if (words.length > 1) {
                        useAction(words[1]);
                    } else {
                        sendMessage("What would you like to use?");
                    }
                    break;
                //"move" synonyms
                case "run":
                case "walk":
                case "go":
                case "travel":
                case "move":
                    if (words.length > 1) {
                        moveAction(words[1]);
                    } else {
                        sendMessage("Where would you like to move to?");
                    }
                    break;
                //"remove" synonyms
                case "drop":
                case "throw":
                case "remove":
                case "leave":
                    if (words.length > 1) {
                        removeAction(words[1]);
                    } else {
                        sendMessage("What would you like to remove from your inventory?");
                    }
                    break;
                case "end":
                case "done":
                case "next":
                case "finish":
                    endTurn();
                    break;
                case "inventory":
                case "i":
                case "items":
                    displayInventory();
                    break;
                default:
                    sendMessage("I don't understand \"" + words[0] + "\"");
                    break;
            }
        } else {
            sendMessage("Please enter something.");
        }
    }
    */

    /**
     * @param inspectWhat
     */
    private void inspectAction(String inspectWhat) {
        if (inspectWhat != null) {
            inspectWhat = inspectWhat.toLowerCase();
            if (currentQuest.currentRoom().peekItem(inspectWhat) != null) {

                Item i = currentQuest.currentRoom().peekItem(inspectWhat);

                sendMessage("You take a closer look at the " + i.getName() + ".\n" + i.Description() + ".");
            } else if (turnManager.currentTurn().getInventory().peekItem(inspectWhat) != null) {

                Item i = turnManager.currentTurn().getInventory().peekItem(inspectWhat);

                sendMessage("You take a closer look at your " + i.getName() + ".\n" + i.Description() + ".");
            } else if (currentQuest.currentRoom().getCharater(inspectWhat) != null) {
                sendMessage(inspectWhat + " has the following items: \n");
                sendMessage(currentQuest.currentRoom().getCharater(inspectWhat).getInventory().displayItems());
            } else {
                //assume we are talking about the room
                sendMessage("You take in your surroundings.\n" + currentQuest.currentRoom().Description());
                sendImage(currentQuest.currentRoom());
            }
        }
    }

    private void takeAction(String takeWhat) {

        if (takeWhat != null) {
            takeWhat = takeWhat.toLowerCase();
            if (currentQuest.currentRoom().peekItem(takeWhat) != null) {

                Item i = currentQuest.currentRoom().peekItem(takeWhat);

                //make sure it is not scenery
                if (i.isScenery()) {
                    sendMessage("You can't take the " + i.getName() + ". ");
                    return;
                }

                //try to put the item in the current player's inventory
                if (turnManager.currentTurn().getInventory().addItem(i)) {
                    //remove it from the room
                    currentQuest.currentRoom().removeItem(takeWhat);
                    sendMessage("You take the " + i.getName() + ". ");
                    if (i instanceof HealthItem && ((HealthItem) i).getHealth() < 0) {
                        ((HealthItem) i).useItem(turnManager.currentTurn());
                        sendMessage("You take " + ((HealthItem) i).getHealth() + " damage. You now have " + turnManager.currentTurn().getHealth() + " health remaining.");
                        if (!turnManager.canAct(turnManager.currentTurn())) {
                            endTurn();
                        }
                    }
                } else {
                    sendMessage("You can not fit " + i.getName() + " in your inventory.");
                }
            } else {
                //not in the room
                sendMessage("You see no " + takeWhat + " here.");
            }
        }
    }

    private void useAction(String itemName) {
        Item item = turnManager.currentTurn().getInventory().peekItem(itemName);
        if (item != null) {
            if (item instanceof UsableItem) {
                if (item instanceof HealthItem) {
                    ((UsableItem) item).useItem(turnManager.currentTurn());
                    sendMessage("You use the " + itemName + ". You now have " + turnManager.currentTurn().getHealth() + " / " + turnManager.currentTurn().getMaxHealth() + " health remaining.");
                }
                if (((UsableItem) item).getUsesLeft() <= 0) {
                    turnManager.currentTurn().getInventory().removeItem(itemName);
                    sendMessage("The " + itemName + " is no longer usable. You discard it.");
                }
            } else if (item instanceof KeyItem) {
                boolean doorUnlocked = false;
                if (attemptUnlock(Directions.Down, itemName)) {
                    sendMessage(TextConstants.doorUnlocked + "lower door.");
                    doorUnlocked = true;
                }
                if (attemptUnlock(Directions.Up, itemName)) {
                    sendMessage(TextConstants.doorUnlocked + "upper door.");
                    doorUnlocked = true;
                }
                if (attemptUnlock(Directions.North, itemName)) {
                    sendMessage(TextConstants.doorUnlocked + "northern door.");
                    doorUnlocked = true;
                }
                if (attemptUnlock(Directions.East, itemName)) {
                    sendMessage(TextConstants.doorUnlocked + "eastern door.");
                    doorUnlocked = true;
                }
                if (attemptUnlock(Directions.South, itemName)) {
                    sendMessage(TextConstants.doorUnlocked + "southern door.");
                    doorUnlocked = true;
                }
                if (attemptUnlock(Directions.West, itemName)) {
                    sendMessage(TextConstants.doorUnlocked + "western door.");
                    doorUnlocked = true;
                }

                if (!doorUnlocked) {
                    sendMessage(TextConstants.noDoorsUnlocked);
                }
            } else {
                sendMessage("You cannot use a " + itemName + ".");
            }
        } else {
            sendMessage("You don't have a \"" + itemName + "\".");
        }
        if (!turnManager.canAct(turnManager.currentTurn())) {
            endTurn();
        }
    }

    //attempts to unlock a door in the given direction with the given key, returns true if successful
    private boolean attemptUnlock(Directions direction, String keyName) {
        if (turnManager.currentTurn().getRoom().getDoorway(direction) != null) {
            if (turnManager.currentTurn().getRoom().getDoorway(direction).getLocked()) {
                if (turnManager.currentTurn().getRoom().getDoorway(direction).getKeyName().toLowerCase().equals(keyName)) {
                    turnManager.currentTurn().getRoom().getDoorway(direction).setLocked(false);
                    return true;
                }
            }
        }
        return false;
    }

    private void statusAction() {
        sendMessage("Health: " + turnManager.currentTurn().getHealth() + " / " + turnManager.currentTurn().getMaxHealth());
    }


    private void moveAction(String direction) {
        Directions d;

        d = parseDirection(direction);
        if (d == null) {
            sendMessage(direction + " is not a valid direction.");
            return;
        }

        //we have a valid direction
        Doorway door = currentQuest.currentRoom().getDoorway(d);
        if (door != null) {
            if (currentQuest.currentRoom().getDoorway(d).getLocked()) {
                sendMessage("That way is locked.");
            } else {
                //move the player the correct direction

                sendMessage("You move " + d.name() + ".");
                if (door.getTrap() != null) {
                    if (door.getTrap().getUsesLeft() != 0) {
                        door.getTrap().useItem(turnManager.currentTurn());
                        sendMessage("The doorway is trapped! " + door.getTrap().getTrapMessage());
                    }
                }
                turnManager.currentTurn().setRoom(door.getToRoom());


                endTurn();
            }
        } else {
            sendMessage("There is nothing that way.");
        }
    }

    private void disarmAction(String direction, Item item) {
        Directions d;

        d = parseDirection(direction);
        if (d == null) {
            sendMessage(direction + " is not a valid direction.");
            return;
        }
        //we have a valid direction
        Doorway door = currentQuest.currentRoom().getDoorway(d);
        if (door != null) {
            if (currentQuest.currentRoom().getDoorway(d).getLocked()) {
                sendMessage("That way is locked.");
            } else {
                //attempt to disarm the trap, will always take a turn
                if (door.getTrap() != null) {
                    if (door.getTrap().attemptDisarm(item.getName())) {
                        sendMessage("It takes a while, but you successfully disarm the " + door.getTrap().getName());
                    } else {
                        sendMessage("You spent a while carefully trying, but you couldn't disarm the " + door.getTrap().getName() + " with that item.");
                    }
                } else {
                    sendMessage("You spend a while carefully searching the doorway, but you can't find anything to disarm in the first place.");
                }
                endTurn();
            }
        } else {
            sendMessage("There is nothing that way.");
        }
    }

    //Gets a direction from a string input, returns null if input is not valid
    private Directions parseDirection(String direction) {
        switch (direction.toLowerCase()) {
            case "north":
            case "n":
                return Directions.North;
            case "south":
            case "s":
                return Directions.South;
            case "east":
            case "e":
                return Directions.East;
            case "up":
            case "u":
                return Directions.Up;
            case "down":
            case "d":
                return Directions.Down;
            case "west":
            case "w":
                return Directions.West;
            default:
                return null;
        }
    }

    private void removeAction(String item) {
        if (item != null) {
            //make sure we have the thing in our inventory
            Item i = turnManager.currentTurn().getInventory().removeItem(item);
            if (i != null) {
                //put the item in this room
                currentQuest.currentRoom().addItem(i);
                sendMessage("You removed " + i.getName() + " from your inventory.");
            } else {
                sendMessage("You don't have a \"" + item + "\".");
            }
        }
    }

    /**
     * Sends the message to Discord
     *
     * @param message to be sent
     */
    private void sendMessage(String message) {
        validTextChannel.sendMessage(message);
    }

    /**
     * Sends an image to Discord
     *
     * @param room to describe with an image
     */
    private void sendImage(Room room) {
        try {
            if (room.getImgUrl() != null) {
                BufferedImage image = ImageIO.read(new URL(room.getImgUrl()));
                new MessageBuilder()
                        .addAttachment(image, "out.png")
                        .send(validTextChannel);
            }
        } catch (MalformedURLException mue) {
            System.out.println("This shouldn't happen");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    /**
     * Call this on a valid turn.
     * It will increment the turn index and tell all user's who's turn it is now
     */
    private void endTurn() {
        //clearAllMessages();
        if (turnManager.canAct(turnManager.currentTurn())) {
            sendMessage(turnManager.currentTurn().getDiscordUser().getDisplayName(server) + " ends their turn.");
        } else {
            sendMessage(TextConstants.playerDies);
            currentQuest.failQuest();
            currentQuest = null;
            return;
        }

        if (currentQuest.getMap().getEndingRoom().equals(turnManager.currentTurn().getRoom())) {
            if (turnManager.currentTurn().getRoom().getPlayerCount() == turnManager.numberOfPlayers()) {
                sendMessage(TextConstants.allPlayersAtExit);
                currentQuest.winQuest();
                currentQuest = null;
            } else {
                sendMessage(turnManager.currentTurn().getDiscordUser().getDisplayName(server) + " is waiting at the exit.");
            }
        }
        turnManager.nextTurn();
        AudioManager.playNewSound(AudioSourceType.LOCAL_SOUND_EFFECT, AudioManager.getLocalPath("nextTurn.ogg"));
        sendMessage(TextConstants.inspectRoomOnTurnStart + currentQuest.currentRoom().Description());
        sendMessage("It is now " + turnManager.currentTurn().getDiscordUser().getDisplayName(server) + "'s turn.");
    }

    /**
     * Returns a string of all the current players who have joined this quest
     *
     * @return
     */
    private String getPartyMembers() {
        String s = "";
        for (Player p : turnManager.getPlayers()) {
            s += "\t" + p.getDiscordUser().getDisplayName(server) + "\n";
        }
        return s;
    }

    /**
     * provides the interface to add and
     * removes players on a party and to load
     * a quest, and start a quest
     * <p>
     * Must be done before the quest begins
     */
    public void preQuestPartyManagement(MessageCreateEvent event) {

        String messageInput = event.getMessageContent();
        User discordUser = event.getMessageAuthor().asUser().get();

        Matcher ma;


        if (/*messageInput.equalsIgnoreCase("start quest")*/ (ma = startPattern.matcher(messageInput)).find()) {
            sendMessage("Loading quest!");

            //check if anyone has joined to play
            if (turnManager.numberOfPlayers() == 0) {
                sendMessage(TextConstants.cannotStartQuest);
            } else {


                //do we make a new quest with a file?
                if (event.getMessageAttachments().size() > 0) {
                    for (int i = 0; i < event.getMessageAttachments().size(); i++) {
                        MessageAttachment attachment = event.getMessageAttachments().get(0);
                        String filename = attachment.getFileName();
                        String extension = filename.substring(filename.length() - 6);
                        ArrayList<String> text = new ArrayList<String>();
                        if (extension.compareTo(".quest") == 0) {
                            try {
                                InputStream stream = attachment.downloadAsInputStream();
                                Scanner scan = new Scanner(stream);
                                while (scan.hasNext()) {
                                    text.add(text.size(), scan.nextLine());
                                }


                                //try to load the quest
                                MapLoader loader = new MapLoader();
                                Map m = loader.LoadMap(text);
                                if (m == null) {
                                    //error loading the quest
                                    sendMessage("There was an error loading the Quest: " + loader.getErrorCode());
                                    return;
                                } else {
                                    //we have a new valid quest
                                    sendMessage("Quest loaded successfully!");
                                    currentQuest = new Quest(m, turnManager);
                                }


                            } catch (IOException ignored) {
                                System.out.println("IOException during quest download");
                                sendMessage("Sorry, but I couldn't load that quest.");
                            }
                        }
                    }
                } else {
                    System.out.println(ma.group("quest"));
                    currentQuest = defaultQuestLoader.loadDefaultQuest(ma.group("quest"), turnManager);
                }

                //if there was an error loading the quest file
                if (currentQuest != null)
                    clearAllMessages();
                sendMessage("Starting a new Quest with the following players:\n" + getPartyMembers());
                currentQuest.startQuest();
                sendMessage(currentQuest.getMap().getStartingRoom().Description());
                sendMessage("It is now " + turnManager.currentTurn().getDiscordUser().getDisplayName(server) + "'s turn.");
            }

        } else if (messageInput.equalsIgnoreCase("join")) {
            //add this user to the list of users
            if (turnManager.getByUser(discordUser) == null) {
                turnManager.addPlayer(new Player(discordUser));
                sendMessage(discordUser.getDisplayName(server) + " has joined the party.");
            } else {
                sendMessage(TextConstants.alreadyJoined);
            }
            sendMessage(TextConstants.partyMembersHeader + getPartyMembers());
        } else if (messageInput.equalsIgnoreCase("leave")) {
            //add this user to the list of users
            if (turnManager.getByUser(discordUser) != null) {
                sendMessage(discordUser.getDisplayName(server) + " has left the party.");
                turnManager.removePlayer(turnManager.getByUser(discordUser));

            } else {
                sendMessage(TextConstants.notAPartyMember);
            }
            if (getPartyMembers().isEmpty()) {
                sendMessage(TextConstants.noPartyMembers);
            } else {
                sendMessage(TextConstants.partyMembersHeader + getPartyMembers());
            }
        } else {
            sendMessage(TextConstants.noActiveQuest);
        }
    }

    void displayHelp(MessageCreateEvent event) {

        // Delete message
        event.deleteMessage();
        // Send them a direct message with the help
        event.getMessageAuthor().asUser().get().openPrivateChannel().thenApplyAsync(channel -> channel.sendMessage(
                TextConstants.helpOutput));

    }

    void displayInventory() {
        sendMessage(turnManager.currentTurn().getInventory().displayItems());
        sendMessage(turnManager.currentTurn().getInventory().displayWeight());
    }

    void clearAllMessages() {
        validTextChannel.getMessages(Integer.MAX_VALUE).thenApplyAsync(MessageSet::deleteAll);
    }

    void attackAction(String target, String weapon){
        Character targetChar = null;
        for(Player p : turnManager.getPlayers()){
            if(target.compareToIgnoreCase(p.getDiscordUser().getDisplayName(server))==0){
                targetChar=currentQuest.currentRoom().getCharater(p.getDiscordUser().getIdAsString());
            }
        }
        if(targetChar==null){
            targetChar = turnManager.currentTurn().getRoom().getCharater(target);
        }
        if(targetChar==null){
            sendMessage(target+" is not in this room.");
        }else if(targetChar.getHealth()<=0){
            sendMessage(target+" is already incapacitated.");
        } else {
            WeaponItem weaponItem = null;
            Item item = turnManager.currentTurn().getInventory().peekItem(weapon);
            if(item instanceof WeaponItem) {
                weaponItem = (WeaponItem) turnManager.currentTurn().getInventory().peekItem(weapon);
            } else {
                sendMessage(weapon+" is not a weapon.");
                return;
            }
            if(weaponItem==null){
                sendMessage("You do not have a(n) "+weapon);
            } else {
                sendMessage("You attack " +target+ " with your " + weapon + ", dealing " + weaponItem.attack(targetChar) + " damage.");
                if(targetChar.getHealth()==0){
                    sendMessage(target+" is now incapacitated!");
                    Inventory i = targetChar.getInventory();
                    for(Item it : i.getItems().values()){
                        currentQuest.currentRoom().addItem(it);
                        i.removeItem(it.getName());
                    }
                }
            }

        }
    }
}
