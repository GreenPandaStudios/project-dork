import org.javacord.api.entity.server.Server;
import java.util.concurrent.TimeUnit;

class TestBot {
    private final Server server;
    private final String BotChannelName;

    TestBot(Server server, String BotChannelName) {
        this.server = server;
        this.BotChannelName = BotChannelName;
    }

    Server getServer() {
        return server;
    }

    public void sendMessage(String message) {
        server.getTextChannelsByName(BotChannelName).get(0).sendMessage(message);
    }

    public void delay(int seconds) throws InterruptedException {
        TimeUnit.SECONDS.sleep(seconds);
    }

    public void clearAllMessages() {
        server.getTextChannelsByName(BotChannelName).get(0).getMessages(Integer.MAX_VALUE).thenApplyAsync(messages -> messages.deleteAll());
    }
}