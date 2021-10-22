import Items.Item;
import Players.Inventory;
import org.junit.Assert;
import org.junit.Test;

public class InventoryDisplayTest {
    Inventory testInventory = new Inventory(10000);
    Inventory testInventory2 = new Inventory(10000);
    Inventory testInventory3 = new Inventory(10000);

    @Test
    public void testEmptyInventory(){
        Assert.assertEquals("Your inventory is empty!\n", testInventory.displayItems());
    }
    @Test
    public void testSingleItemInventory(){
        testInventory2.addItem(new Item("Golden-Apple", "A curious golden apple.", 50, 1000, false));
        Assert.assertEquals("Your inventory contains:\n\t-Golden-Apple\n", testInventory2.displayItems());
    }

    @Test
    public void testMultiItemInventory(){
        testInventory3.addItem(new Item("Golden-Apple1", "A curious golden apple.", 50, 1000, false));
        testInventory3.addItem(new Item("Golden-Apple2", "A curious golden apple.", 50, 1000, false));
        testInventory3.addItem(new Item("Golden-Apple3", "A curious golden apple.", 50, 1000, false));
        testInventory3.addItem(new Item("Golden-Apple4", "A curious golden apple.", 50, 1000, false));
        testInventory3.addItem(new Item("Golden-Apple5", "A curious golden apple.", 50, 1000, false));
        testInventory3.addItem(new Item("Golden-Apple6", "A curious golden apple.", 50, 1000, false));
        testInventory3.addItem(new Item("Golden-Apple7", "A curious golden apple.", 50, 1000, false));
        testInventory3.addItem(new Item("Golden-Apple8", "A curious golden apple.", 50, 1000, false));
        testInventory3.addItem(new Item("Golden-Apple9", "A curious golden apple.", 50, 1000, false));
        testInventory3.addItem(new Item("Golden-Apple10", "A curious golden apple.", 50, 1000, false));

        Assert.assertEquals("Your inventory contains:\n\t-Golden-Apple1\n\t-Golden-Apple2\n\t-Golden-Apple10\n\t-Golden-Apple3\n\t-Golden-Apple4\n\t-Golden-Apple5\n\t-Golden-Apple6\n\t-Golden-Apple7\n\t-Golden-Apple8\n\t-Golden-Apple9\n", testInventory3.displayItems());
    }
}
