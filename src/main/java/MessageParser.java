import Items.HealthItem;
import Items.Item;
import Items.UsableItem;
import Players.Player;
import Quests.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.regex.*;

import java.util.Objects;

public class MessageParser {

    private final Server server;
    private final TextChannel validTextChannel;
    private final TurnManager turnManager = new TurnManager();
    DiscordApi api;
    private Quest currentQuest = null;

    //////////////////////////////REGEX constants

    private final Pattern inspectObjectPattern = Pattern.compile("(look|examine|study|inspect|peek)(\\s*)(?<item>.*)");

    private final Pattern takePattern = Pattern.compile("(grab|collect|store|steal|take)(\\s*)(?<item>.*)");
    private final Pattern movePattern = Pattern.compile("(run|walk|go|travel|move)(\\s*)(?<direction>.*)");
    private final Pattern dropPattern = Pattern.compile("(drop|throw|remove|leave)(\\s*)(?<item>.*)");
    private final Pattern endTurnPattern = Pattern.compile("end|done|next|finish(\\s*turn|my turn|move|my move|\\s)*");
    private final Pattern usePattern = Pattern.compile("(use|activate)(\\s*)(?<item>.*)");
    private final Pattern inventoryPattern = Pattern.compile("(inventory|i|items)");
    private final Pattern statusPattern = Pattern.compile("(what is my)?(status|health)");
    private final Pattern helpPattern = Pattern.compile("(((I (need|want))?help)|(I'm)?confused|(What are the)?commands)([?])?");
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
            preQuestPartyManagement(event.getMessageContent(), event.getMessageAuthor().asUser().get());

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
        if ((m = helpPattern.matcher(message)).find()){
            displayHelp(event);
            return;
        }


        //inspect object
        if ((m = inspectObjectPattern.matcher(message)).find()) {
            if (!m.group(3).equals("")) {
                if (m.group(3).equals("inventory")) {
                    displayInventory();
                } else {
                    inspectAction(m.group(3));
                }
            } else {
                //assume we are talking about the room
                sendMessage("You take in your surroundings. " + currentQuest.currentRoom().Description());
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
        //status
        if ((m = statusPattern.matcher(message)).find()){
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

        endingRoom.addItem(new Item("Golden-Apple", "A curious golden apple.", 50, 1000, false));
        endingRoom.setDescription("You are in a very dark room.");
        Doorway backUp = new Doorway(startingRoom, false);
        Doorway d = new Doorway(hallway, false);
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

                sendMessage("You take a closer look at the " + i.getName() + ". " + i.Description());
            } else if (turnManager.currentTurn().getInventory().peekItem(inspectWhat) != null) {

                Item i = turnManager.currentTurn().getInventory().peekItem(inspectWhat);

                sendMessage("You take a closer look at your " + i.getName() + ". " + i.Description());
            } else {
                sendMessage("You see no " + inspectWhat + " here.");
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
                ((UsableItem) item).useItem(turnManager.currentTurn());
                sendMessage("You use the " + itemName + ". You now have " + turnManager.currentTurn().getHealth() + " / "+ turnManager.currentTurn().getMaxHealth() + " health remaining.");
                if (((UsableItem) item).getUsesLeft() <= 0) {
                    turnManager.currentTurn().getInventory().removeItem(itemName);
                    sendMessage("The " + itemName + " is no longer usable. You discard it.");
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

    private void statusAction() {
        sendMessage("Health: "+turnManager.currentTurn().getHealth() + " / "+ turnManager.currentTurn().getMaxHealth());
    }


    private void moveAction(String direction) {
        Directions d;

        switch (direction.toLowerCase()) {
            case "north":
            case "n":
                d = Directions.North;
                break;
            case "south":
            case "s":
                d = Directions.South;
                break;
            case "east":
            case "e":
                d = Directions.East;
                break;
            case "up":
            case "u":
                d = Directions.Up;
                break;
            case "down":
            case "d":
                d = Directions.Down;
                break;
            case "west":
            case "w":
                d = Directions.West;
                break;
            default:
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
                turnManager.currentTurn().setRoom(door.getToRoom());
                sendMessage("You move " + d.name() + ".");

                endTurn();
            }
        } else {
            sendMessage("There is nothing that way.");
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
     * Call this on a valid turn.
     * It will increment the turn index and tell all user's who's turn it is now
     */
    private void endTurn() {
        clearAllMessages();
        if (turnManager.canAct(turnManager.currentTurn())) {
            sendMessage(turnManager.currentTurn().getDiscordUser().getDisplayName(server) + " ends their turn.");
        } else {
            sendMessage("A player has fallen. The dungeon closes...");
            currentQuest.failQuest();
            currentQuest = null;
            return;
        }

        if (currentQuest.getMap().getEndingRoom().equals(turnManager.currentTurn().getRoom())) {
            if (turnManager.currentTurn().getRoom().getPlayerCount() == turnManager.numberOfPlayers()) {
                sendMessage("All players have made it to the exit. Great job!");
                currentQuest.winQuest();
                currentQuest = null;
            } else {
                sendMessage(turnManager.currentTurn().getDiscordUser().getDisplayName(server) + " is waiting at the exit.");
            }
        }
        turnManager.nextTurn();
        sendMessage("You take in the new room. " + currentQuest.currentRoom().Description());
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
    public void preQuestPartyManagement(String messageInput, User discordUser) {
        if (messageInput.equalsIgnoreCase("start quest")) {


            //check if anyone has joined to play
            if (turnManager.numberOfPlayers() == 0) {
                sendMessage("You must join before starting a quest. Type \"join\" to join.");
            } else {
                clearAllMessages();
                sendMessage("Starting a new Quest with the following players:\n" + getPartyMembers());

                currentQuest = createDefaultQuest();
                currentQuest.startQuest();
                sendMessage(currentQuest.getMap().getStartingRoom().Description());
            }


        } else if (messageInput.equalsIgnoreCase("join")) {
            //add this user to the list of users
            if (turnManager.getByUser(discordUser) == null) {
                turnManager.addPlayer(new Player(discordUser));
                sendMessage(discordUser.getDisplayName(server) + " has joined the party.");
            } else {
                sendMessage("You have already joined the party.");
            }
            sendMessage("The current party members are:\n" + getPartyMembers());
        } else if (messageInput.equalsIgnoreCase("leave")) {
            //add this user to the list of users
            if (turnManager.getByUser(discordUser) != null) {
                sendMessage(discordUser.getDisplayName(server) + " has left the party.");
                turnManager.removePlayer(turnManager.getByUser(discordUser));

            } else {
                sendMessage("You are not a member of this party.\n");
            }
            if (getPartyMembers().isEmpty()) {
                sendMessage("There are no current part members.");
            } else {
                sendMessage("The current party members are:\n" + getPartyMembers());
            }
        } else {
            sendMessage("No active quest!\nPlease create a new quest with \"Start Quest\"");
        }
    }
    void displayHelp(MessageCreateEvent event){

        // Delete message
        event.deleteMessage();
        // Send them a direct message with the help
        event.getMessageAuthor().asUser().get().openPrivateChannel().thenApplyAsync(channel -> channel.sendMessage(
                "Help is currently in progress. :("));

    }
    void displayInventory() {
        sendMessage(turnManager.currentTurn().getInventory().displayItems());
        sendMessage(turnManager.currentTurn().getInventory().displayWeight());
    }

    void clearAllMessages() {
        validTextChannel.getMessages(Integer.MAX_VALUE).thenApplyAsync(messages -> messages.deleteAll());
    }
}
