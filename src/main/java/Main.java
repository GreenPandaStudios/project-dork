import org.javacord.api.AccountType;
import org.javacord.api.BotInviteBuilder;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.server.ServerBuilder;

public class Main {

    //this is the only channel the bot will react in
    public static final String BotChannelName = "dork-domain";
    public static final String BotToken = "ODk2MTA1OTE5MTcwMTA1NDE1.YWCRqw.rHVwkUqm1UvuEeHV2iKvbSxsHck";

    public static void main(String[] args) {
        // Log the bot in
        DiscordApi api = new DiscordApiBuilder()
                .setToken(BotToken)
                .login().join();


        //for each server we are attached to, create a bot
        for (Server server : api.getServers()) {
            new Bot(server, api, BotChannelName);
        }


    }
}
