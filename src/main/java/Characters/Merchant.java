package Characters;

import Items.Item;
import Players.Inventory;

public class Merchant extends Character {

    /**
     * Sells the item in the playerInventory to the merchant,
     * Gold is added to the player and the item is removed
     *
     * @param playerInventory
     * @param item
     * @return if the sale was succesful
     */
    public boolean Sell(Inventory playerInventory, Item item) {

        //do I have enough gold?
        if (inventory.getGold() < item.getValue())
            return false;

        //place the item in the merchants inventory, if we can
        if (!inventory.addItem(item)) {
            return false;
        }

        playerInventory.removeItem(item.getName());

        //give the player the gold
        playerInventory.setGold(playerInventory.getGold() + item.getValue());

        //remove our gold
        inventory.setGold(inventory.getGold() - item.getValue());

        return true;
    }

    public boolean Buy(Inventory playerInventory, String itemName) {
        Item item = inventory.removeItem(itemName);

        //the merchant does not have the item
        if (item == null) return false;

        //does the player have enough gold?
        if (playerInventory.getGold() < item.getValue())
            return false;

        //can the player hold it
        if (playerInventory.addItem(item)) {
            //remove gold from player
            playerInventory.setGold(playerInventory.getGold() - item.getValue());
            //add gold to merchant
            inventory.setGold(inventory.getGold() + item.getValue());
            return true;
        } else {
            //put it back in the merchants inventory
            inventory.addItem(item);

            return false;
        }
    }

}
