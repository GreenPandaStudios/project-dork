import Players.Player;
import org.junit.Assert;
import org.junit.Test;

public class HealthTest {

    private Player player;

    @Test
    public void testHealth() {
        player = new Player();
        player.setHealth(10.5); //general test
        double currHealth = player.getHealth();
        Assert.assertEquals(currHealth, 10.5, 0.0);
        player.setHealth(30); //above maximum test
        currHealth = player.getHealth();
        Assert.assertEquals(currHealth, 20, 0.0);
        player.setHealth(-10); //below minimum test
        currHealth = player.getHealth();
        Assert.assertEquals(currHealth, 0, 0.0);
        player.setHealth(20); //exact maximum test
        currHealth = player.getHealth();
        Assert.assertEquals(currHealth, 20, 0.0);
        player.setHealth(0); //exact minimum test
        currHealth = player.getHealth();
        Assert.assertEquals(currHealth, 0, 0.0);
    }
}
