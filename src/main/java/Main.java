import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;

public class Main {

    // This is the only channel the bot will react in
    public static final String BotChannelName = "dork-domain";
    public static final String BotVoiceChannelName = "dork-domain-music";
    public static final String BotToken = "";

    public static void main(String[] args) {
        // Log the bot in
        DiscordApi api = new DiscordApiBuilder()
                .setToken(BotToken)
                .login().join();

        // For each server we are attached to, create a bot
        for (Server server : api.getServers()) {
            new Bot(server, api, BotChannelName, BotVoiceChannelName);
        }
    }
}
