import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;

public class Main {

    // This is the only channel the bot will react in
    public static final String BotChannelName = "dork-domain";
    public static final String BotToken = "ODk2MTA1OTE5MTcwMTA1NDE1.YWCRqw.BNXu8DQZO20sQDV0GgdHGpIvKcY";

    public static void main(String[] args) {
        // Log the bot in
        DiscordApi api = new DiscordApiBuilder()
                .setToken(BotToken)
                .login().join();

        // For each server we are attached to, create a bot
        for (Server server : api.getServers()) {
            new Bot(server, api, BotChannelName);
        }


    }
}
