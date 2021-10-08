import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;


public class MessageParser {


    private TextChannel validTextChannel;
    DiscordApi api;


    public MessageParser(DiscordApi api, TextChannel validTextChannel){

        // Hook up the listeners
        api.addMessageCreateListener(this::onMessageCreate);

        this.validTextChannel = validTextChannel;
        this.api = api;
    }


    public void onMessageCreate(MessageCreateEvent event){
        //first, validate the channel this message was typed in
        if (validTextChannel!= event.getChannel()){
            return;
        }




        if (event.getMessageContent().equalsIgnoreCase("!ping")) {
            event.getChannel().sendMessage("Pong!");
        }
    }

}
