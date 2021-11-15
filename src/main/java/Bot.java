import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.ServerVoiceChannelBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.server.Server;

public class Bot {
    // The server this bot is on
    private Server server;
    // The Discord bot
    private DiscordApi api;
    // The channel name the bot is active in
    private String BotChannelName;
    // The voice channel the bot will connect to
    private String BotVoiceChannelName;
    // The message parser this bot uses
    private MessageParser parser;

    public Bot(Server server, DiscordApi api, String BotChannelName, String BotVoiceChannelName) {


        this.server = server;
        this.api = api;
        this.BotChannelName = BotChannelName;
        this.BotVoiceChannelName = BotVoiceChannelName;

        if (!server.hasPermission(api.getYourself(), PermissionType.ADMINISTRATOR)) {
            System.out.println("The Bot requires Administrator permission.");
            //give the bot all permissions, this is the link to use
            System.out.println(api.createBotInvite(Permissions.fromBitmask(8)));
        }


        // Set up text channel
        ServerTextChannel botTextChannel;
        if (server.getTextChannelsByName(BotChannelName).size() > 0) {
            // Text channel already exists
            botTextChannel = server.getTextChannelsByName(BotChannelName).get(0);
        } else {
            // Create a new text channel
            botTextChannel = new ServerTextChannelBuilder(server).
                    setName(BotChannelName).create().join();
        }

        // Set up voice channel
        ServerVoiceChannel botVoiceChannel;
        if (server.getVoiceChannelsByName(BotVoiceChannelName).size() > 0) {
            // Voice channel already exists
            botVoiceChannel = server.getVoiceChannelsByName(BotVoiceChannelName).get(0);
        } else {
            // Create a new voice channel
            botVoiceChannel = new ServerVoiceChannelBuilder(server).setName(BotVoiceChannelName).create().join();
        }

        // Start audio
        AudioManager.startAudio(api, botVoiceChannel);


        // Create a message listener to parse messages
        parser = new MessageParser(api, botTextChannel, server);
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public DiscordApi getApi() {
        return api;
    }

    public void setApi(DiscordApi api) {
        this.api = api;
    }

    public String getBotChannelName() {
        return BotChannelName;
    }

    public void setBotChannelName(String botChannelName) {
        BotChannelName = botChannelName;
    }

    public MessageParser getParser() {
        return parser;
    }
}
