package Items;

import Characters.Character;

public class WeaponItem extends EquippableItem {
    double damage;

    public WeaponItem(String name, String description, double weight, double value, boolean scenery, double damage) {
        super(name, description, weight, value, scenery, 0);
        setDamage(damage);
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return damage;
    }

    public double attack(Character character) {
        double dealt = Math.min(damage, character.getHealth());
        character.setHealth(character.getHealth()-dealt);
        return dealt;
    }
}
