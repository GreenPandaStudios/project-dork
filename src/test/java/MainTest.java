import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;

import java.util.concurrent.TimeUnit;

public class MainTest {

    static final String BotChannelName = Main.BotChannelName;
    static final String TestBotToken = "OTAwNzk2NDgzMzQ1OTczMzcw.YXGiGQ.Xo4H4bKJ8murCf-CKWtVfxxmoD0";
    static TestBot testBot;

    public static void main(String[] args) throws InterruptedException {
        // Starting 'Dork-Master'
        Main.main(new String[0]);

        // Giving some time for 'Dork-Master' to login and get set up
        TimeUnit.SECONDS.sleep(3);

        // Creating and logging in the test bot
        DiscordApi testApi = new DiscordApiBuilder()
                .setToken(TestBotToken)
                .login().join();

        for (Server server : testApi.getServers()) {
            testBot = new TestBot(server, BotChannelName);
            break;
        }

        // *** Start all tests here ***
//        testBot.sendMessage("I'm testing now");
//        testBot.delay(3);
//        testBot.clearAllMessages();
//        testBot.sendMessage("FIRST MESSAGE");
    }
}

class TestBot {
    private final Server server;
    private final String BotChannelName;

    TestBot(Server server, String BotChannelName) {
        this.server = server;
        this.BotChannelName = BotChannelName;
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