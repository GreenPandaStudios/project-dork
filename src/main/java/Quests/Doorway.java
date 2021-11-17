package Quests;


import Interfaces.IDescriptable;
import Items.TrapItem;

public class Doorway implements IDescriptable {
    /**
     * @param toRoom
     * @param locked
     */
    public Doorway(Room toRoom, boolean locked) {

        setLocked(locked);
        setToRoom(toRoom);
    }

    public Doorway(Room toRoom, boolean locked, String keyName) {
        setLocked(locked);
        setToRoom(toRoom);
        setKeyName(keyName);
    }

    public Doorway() {
        locked = false;
    }

    ;

    TrapItem trap = null;

    private Room toRoom;

    private boolean locked;

    private String keyName;

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean value) {
        locked = value;
    }

    public String getLockedDesc() {
        return lockedDesc;
    }

    public void setLockedDesc(String lockedDesc) {
        this.lockedDesc = lockedDesc;
    }

    //description for the doorway's locked state
    private String lockedDesc = "The doorway is locked";

    public String getUnlockedDesc() {
        return unlockedDesc;
    }

    public void setUnlockedDesc(String unlockedDesc) {
        this.unlockedDesc = unlockedDesc;
    }

    //description for the doorway's unlocked state
    private String unlockedDesc = "The doorway is unlocked";


    @Override
    public String Description() {
        return locked ? lockedDesc : unlockedDesc;
    }

    public Room getToRoom() {
        return toRoom;
    }

    public void setToRoom(Room toRoom) {
        this.toRoom = toRoom;
    }


    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setTrap(TrapItem newTrap) {
        trap = newTrap;
    }

    public TrapItem getTrap() {
        return trap;
    }
}
