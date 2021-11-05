import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class QuestTest {

    final String BotChannelName = Main.BotChannelName;
    final String TestBotToken = "OTAwNzk2NDgzMzQ1OTczMzcw.YXGiGQ.Xo4H4bKJ8murCf-CKWtVfxxmoD0";
    TestBot testBot;
    DiscordApi testApi;

    @BeforeEach
    void setUp() throws InterruptedException {
        // Starting 'Dork-Master'
        Main.main(new String[0]);

        // Giving some time for 'Dork-Master' to login and get set up
        TimeUnit.SECONDS.sleep(3);

        // Creating and logging in the test bot
        testApi = new DiscordApiBuilder()
                .setToken(TestBotToken)
                .login().join();

        for (Server server : testApi.getServers()) {
            testBot = new TestBot(server, BotChannelName);
            break;
        }
    }

    @Test
    public void joinTest() throws InterruptedException {

        testBot.sendMessage("join");

        testBot.delay(2);

        CountDownLatch lock = new CountDownLatch(1);
        lock.await(2, TimeUnit.SECONDS);

        final String[] message = new String[1];

        testBot.getServer().getTextChannelsByName(BotChannelName).get(0).getMessages(1).thenApplyAsync((messages) -> {
            if (messages.getNewestMessage().isPresent()) {
                message[0] = messages.getNewestMessage().get().getContent();
                System.out.println(message[0]);
            }
            assert("The current party members are:\n" + "    DORK Tester" == message[0]);
            return null;
        });

        lock.await(2, TimeUnit.SECONDS);
    }

}