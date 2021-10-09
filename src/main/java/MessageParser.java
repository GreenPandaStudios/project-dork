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
            parseValidMessage(event.getMessageContent().toLowerCase().split(" "));
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

        Doorway d = new Doorway(endingRoom, false);
        Doorway d1 = new Doorway(endingRoom, false);

        d.setLockedDesc("an old rusty and heavy looking door with a large padlock.");
        d.setUnlockedDesc("An old heavy door leaning open. There is a padlock on the ground beside it.");
        d1.setUnlockedDesc("Stairs winding down to nearly complete darkness. There is an ever-so faint light just beyond the point of complete darkness.");

        startingRoom.setDoorway(d, Directions.South);
        startingRoom.setDoorway(d1, Directions.Down);

        startingRoom.setDescription("You are standing in a dark stone chamber. There is a single torch on the wall beside you.");



        Map m = new Map(startingRoom,endingRoom);

        return  new Quest(m, turnManager);
    }

    private void parseValidMessage(String[] words){
        if (words.length > 0){
            switch (words[0]){
                case "inspect":
                    if (words.length > 1){
                        inspectAction(words[1]);
                    }
                    else{
                        sendMessage("What would you like to inspect?");
                    }
                    break;
                default:
                    sendMessage("I don't understand \"" + words[0] + "\"");
                    break;
            }
        }
        else{
            sendMessage("Please enter something.");
        }
    }

    /**
     *
     * @param inspectWhat
     */
    private void inspectAction(String inspectWhat){
        if (inspectWhat != null){
            if (currentQuest.currentRoom().peekItem(inspectWhat)!=null){

                Item i = currentQuest.currentRoom().peekItem(inspectWhat);

                sendMessage("You take a closer look at the "+i.getName()+". " + i.Description());
            }
            else{
               sendMessage("You see no " + inspectWhat +" here.");
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
