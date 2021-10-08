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




        //for each server we are attached to, do the following
        for (Server server : api.getServers()){


            if (!server.hasPermission(api.getYourself(), PermissionType.ADMINISTRATOR)){
                System.out.println("The Bot requires Administrator permission.");
                //give the bot all permissions, this is the link to use
                System.out.println(api.createBotInvite(Permissions.fromBitmask(8)));
                continue;
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
            MessageParser parser = new MessageParser(api, botTextChannel);

        }


    }
}
