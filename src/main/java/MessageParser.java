import Items.Item;
import Misc.Vector3;
import Players.Player;
import Quests.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import  Misc.Vector3;

import java.util.Iterator;

public class MessageParser {

    private Server server;
    private TextChannel validTextChannel;
    private Quest currentQuest = null;
    private TurnManager turnManager = new TurnManager();


    DiscordApi api;


    public MessageParser(DiscordApi api, TextChannel validTextChannel, Server server){

        // Hook up the listeners
        api.addMessageCreateListener(this::onMessageCreate);

        this.validTextChannel = validTextChannel;
        this.api = api;
        this.server = server;
    }


    public void onMessageCreate(MessageCreateEvent event){
        //first, validate the channel this message was typed in
        if (validTextChannel!= event.getChannel()){
            return;
        }

        //now make sure it is not the bot sending a message
        if (event.getMessageAuthor().isYourself()){
            return;
        }

        if (currentQuest == null){
            if (event.getMessageContent().equalsIgnoreCase("new quest")){


                //check if anyone has joined to play
                if (turnManager.numberOfPlayers() == 0){
                    sendMessage("You must join before starting a quest. Type \"join\" to join.");
                }
                else{
                    sendMessage("Starting a new Quest with the following players: ");

                    currentQuest = createDefaultQuest();
                    currentQuest.startQuest();
                    event.getChannel().sendMessage(currentQuest.getMap().getStartingRoom().Description());
                }


            }
            else if (event.getMessageContent().equalsIgnoreCase("join")){
                //add this user to the list of users
                if (turnManager.getByUser(event.getMessageAuthor().asUser().get())==null) {
                    turnManager.addPlayer(new Player(event.getMessageAuthor().asUser().get()));
                    sendMessage(event.getMessageAuthor().asUser().get().getDisplayName(server) + " has joined the party.");
                }
                else{
                    sendMessage("You have already joined the party.");
                }
                sendMessage("The current party members are:\n" + getPartyMembers());
            }
            else if (event.getMessageContent().equalsIgnoreCase("leave")){
                //add this user to the list of users
                if (turnManager.getByUser(event.getMessageAuthor().asUser().get())!=null){
                    sendMessage(event.getMessageAuthor().asUser().get().getDisplayName(server) + " has left the party.");
                    turnManager.removePlayer(turnManager.getByUser(event.getMessageAuthor().asUser().get()));

                }
                else{
                    sendMessage("You are not a member of this party.\n");
                }
                sendMessage("The current party members are:\n" + getPartyMembers());


            }
            else{
                event.getChannel().sendMessage("No active quest!\nPlease create a new quest with 'New Quest'");
            }
        }
        else{
            //if it is this player's turn
            if (event.getMessageAuthor().asUser().get().equals(turnManager.currentTurn().getDiscordUser())){
                parseValidMessage(event.getMessageContent().toLowerCase().split(" "));
            }
            else{
                sendMessage(event.getMessageAuthor().asUser().get().getDisplayName(server) + ", it is not your turn.");
            }

        }

    }

    private Quest createDefaultQuest(){
        Room startingRoom = new Room( new Vector3(0,0,0) ).addItem(new Item("Sword",
                "A heavy well-made sword",
                10.5,
                10));
        Room endingRoom = new Room(
                new Vector3(0,1,0)
        );

        endingRoom.addItem(new Item("Golden-Apple", "A curious golden apple.", 50, 1000));
        endingRoom.setDescription("You are in a very dark room.");
        Doorway backUp = new Doorway(startingRoom, false);
        Doorway d = new Doorway(endingRoom, false);
        Doorway d1 = new Doorway(endingRoom, false);
        backUp.setUnlockedDesc("A stair-case winds its way upwards.");
        d.setLockedDesc("an old rusty and heavy looking door with a large padlock.");
        d.setUnlockedDesc("an old heavy door leaning open. There is a padlock on the ground beside it.");
        d1.setUnlockedDesc("Stairs winding down to nearly complete darkness. There is an ever-so faint light just beyond the point of complete darkness.");

        startingRoom.setDoorway(d, Directions.South);
        startingRoom.setDoorway(d1, Directions.Down);
        endingRoom.setDoorway(backUp, Directions.Up);


        startingRoom.setDescription("You are standing in a dark stone chamber. There is a single torch on the wall beside you.");



        Map m = new Map(startingRoom,endingRoom);

        return  new Quest(m, turnManager);
    }

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
                        inspectAction(words[1]);
                    } else {
                        //assume we are talking about the room
                        sendMessage("You take in your surroundings. " +currentQuest.currentRoom().Description());
                    }
                    break;
                //"take" synonyms
                case "grab":
                case "collect":
                case "store":
                case "steal":
                case "take":
                    if (words.length > 1){
                        takeAction(words[1]);
                    }
                    else{
                        sendMessage("What would you like to take?");
                    }
                    break;
                 //"move" synonyms
                case "run":
                case "walk":
                case "go":
                case "travel":
                case "move":
                    if (words.length > 1){
                        moveAction(words[1]);
                    }
                    else{
                        sendMessage("Where would you like to move to?");
                    }
                    break;
                //"remove" synonyms
                case "drop":
                case "throw":
                case "remove":
                case "leave":
                    if (words.length > 1){
                        removeAction(words[1]);
                    }
                    else{
                        sendMessage("What would you like to remove from your inventory?");
                    }
                    break;
                case "end":
                case "done":
                case "next":
                case "finish":
                    endTurn();
                    break;
                default:
                    sendMessage("I don't understand \"" + words[0] + "\"");
                    break;
            }
        } else {
            sendMessage("Please enter something.");
        }
    }
    /**
     *
     * @param inspectWhat
     */
    private void inspectAction(String inspectWhat){
        if (inspectWhat != null){
            inspectWhat = inspectWhat.toLowerCase();
            if (currentQuest.currentRoom().peekItem(inspectWhat)!=null){

                Item i = currentQuest.currentRoom().peekItem(inspectWhat);

                sendMessage("You take a closer look at the "+i.getName()+". " + i.Description());
            }
            else{
               sendMessage("You see no " + inspectWhat +" here.");
            }
        }
    }

    private void takeAction(String takeWhat){

        if (takeWhat != null){
            takeWhat = takeWhat.toLowerCase();
            if (currentQuest.currentRoom().peekItem(takeWhat)!=null){

                Item i = currentQuest.currentRoom().peekItem(takeWhat);
                //try to put the item in the current player's inventory
                if (turnManager.currentTurn().getInventory().addItem(i)){
                    //remove it from the room
                    currentQuest.currentRoom().removeItem(takeWhat);
                    sendMessage("You take the " + i.getName());
                }
                else{
                    sendMessage("You can not fit "+i.getName()+" in your inventory.");
                }
            }
            else{
                //not in the room
                sendMessage("You see no " + takeWhat +" here.");
            }
        }
    }

    private void moveAction(String direction){


        Directions d;

        switch (direction.toLowerCase()){
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
        if (door != null){
            if (currentQuest.currentRoom().getDoorway(d).getLocked()){
                sendMessage("That way is locked.");
            }
            else{
                //move the player the correct direction
                turnManager.currentTurn().setRoom(door.getToRoom());
                sendMessage("You move " + d.name()+".");

                endTurn();
            }
        }
        else{
            sendMessage("There is nothing that way.");
        }

    }

    private void removeAction(String item){
        if (item != null){
            //make sure we have the thing in our inventory
            Item i = turnManager.currentTurn().getInventory().removeItem(item);
            if (i != null){
                //put the item in this room
                currentQuest.currentRoom().addItem(i);
                sendMessage("You removed " + i.getName() + " from your inventory.");
            }
            else{
                sendMessage("You don't have a \"" + item +"\".");
            }
        }
    }
    /**
     * Sends the message to Discord
     * @param message
     */
    private void sendMessage(String message){
        validTextChannel.sendMessage(message);
    }

    /**
     * Call this on a valid turn.
     * It will increment the turn index and tell all user's who's turn it is now
     */
    private void endTurn(){

        sendMessage(turnManager.currentTurn().getDiscordUser().getDisplayName(server)+" ends their turn." );

        turnManager.nextTurn();
        sendMessage("It is now " + turnManager.currentTurn().getDiscordUser().getDisplayName(server)+"'s turn.");
    }

    /**
     * Returns a string of all the current players who have joined this quest
     * @return
     */
    private String getPartyMembers(){
        String s = "";
        for (Player p : turnManager.getPlayers()) {
            s+="\t" + p.getDiscordUser().getDisplayName(server)+"\n";
        }
        return s;
    }
}
