import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.server.Server;

public class Bot {
    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    //the server this bot is on
    private Server server;

    public DiscordApi getApi() {
        return api;
    }

    public void setApi(DiscordApi api) {
        this.api = api;
    }

    private DiscordApi api;

    public String getBotChannelName() {
        return BotChannelName;
    }

    public void setBotChannelName(String botChannelName) {
        BotChannelName = botChannelName;
    }

    //the channel name the bot is active in
    private String BotChannelName;

    public MessageParser getParser() {
        return parser;
    }

    //the message parser this bot uses
    private MessageParser parser;

    public Bot(Server server, DiscordApi api, String BotChannelName){


        this.server = server;
        this.api = api;
        this.BotChannelName = BotChannelName;

        if (!server.hasPermission(api.getYourself(), PermissionType.ADMINISTRATOR)){
            System.out.println("The Bot requires Administrator permission.");
            //give the bot all permissions, this is the link to use
            System.out.println(api.createBotInvite(Permissions.fromBitmask(8)));
        }


        //see if the channel already exists
        ServerTextChannel botTextChannel;
        if (server.getTextChannelsByName(BotChannelName).size() > 0){
            botTextChannel = server.getTextChannelsByName(BotChannelName).get(0);
        }
        else {
            //create a text channel for the bot
            botTextChannel = new ServerTextChannelBuilder(server).
                    setName(BotChannelName).create().join();
        }



        //create a message parser for the bot
        parser = new MessageParser(api, botTextChannel, server);
    }


}
