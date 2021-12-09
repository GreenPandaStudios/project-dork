package Characters;

import Items.Item;
import Players.Player;

import java.util.Random;

public class Enemy extends NPC {
    double damage;
    double accuracy;
    //add dodge chance?
    double dodge = 0;
    Random rng;

    String description;
    String hitDescription;
    String missDescription;
    String deathDescription;

    public Enemy(String name, double hp, double damage, double accuracy) {
        this.name = name;
        maxHealth = hp;
        health = hp;
        this.damage = damage;
        this.accuracy = accuracy;
        rng = new Random();
        description = name;
        hitDescription = name + " attacked and hit!";
        missDescription = name + " attacked and missed!";
        deathDescription = name + " has been slain!";
    }

    // attacks given player, returns true if attack hits successfully, false if not
    public boolean attack(Player player) {
        if (rng.nextInt(100) < accuracy) {
            player.setHealth(player.getHealth() - damage);
            return true;
        }
        return false;
    }

    // set the amount of gold this enemy drops
    public void setGold(double gold) {
        getInventory().setGold(gold);
    }

    // add an item to be dropped when this enemy is killed
    public void addDrop(Item item) {
        getInventory().addItem(item);
    }

    // returns a description of this enemy with its name, current HP and attack damage
    public String getStatDescription() {
        return "Enemy: " + name + " - Current HP: " + health + " - Damage: " + damage;
    }

    // return the amount of damage this enemy deals
    public double getDamage() {
        return this.damage;
    }

    // returns a description of this enemy
    public String getDescription() {
        return description;
    }

    // sets the description of this enemy
    public void setDescription(String description) {
        this.description = description;
    }

    // returns a description of the enemy attacking and hitting
    public String getHitDescription() {
        return hitDescription;
    }

    // sets the description of this enemy attacking and hitting
    public void setHitDescription(String hitDescription) {
        this.hitDescription = hitDescription;
    }

    // returns a description of the enemy attacking and missing
    public String getMissDescription() {
        return missDescription;
    }

    // sets the description of this enemy attacking and missing
    public void setMissDescription(String missDescription) {
        this.missDescription = missDescription;
    }

    // returns a description of the enemy dying
    public String getDeathDescription() {
        return deathDescription;
    }

    // sets the description of this enemy dying
    public void setDeathDescription(String deathDescription) {
        this.deathDescription = deathDescription;
    }
}
