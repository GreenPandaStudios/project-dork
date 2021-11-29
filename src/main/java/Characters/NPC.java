package Characters;

import Quests.Room;

public class NPC extends Character {

    /**
     * Sets the current NPC's room to this room and updates the room with that info
     * @param room
     */
    @Override
    public void setRoom(Room room) {
        if (this.room != null) {
            this.room.setNpcCount(this.room.getNpcCount() - 1);
            this.room.removeCharacter(this);
        }
        this.room = room;
        this.room.setNpcCount(this.room.getNpcCount() + 1);
        this.room.addCharacter(this);
    }

    protected String name = "";

    /**
     * Gets the UNIQUE name of the NPC
     * @return
     */
    @Override
    public String getName() {
        return name;
    }
}
