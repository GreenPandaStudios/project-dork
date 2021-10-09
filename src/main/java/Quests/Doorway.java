package Quests;


import Interfaces.IDescriptable;
public class Doorway implements IDescriptable {

    public Doorway(Room toRoom){



    }



    private boolean locked;
    public boolean getLocked(){
        return  locked;
    }
    public void setLocked(boolean value){
        locked = value;
    }
    //description for the doorway's locked state
    private String lockedDesc = "The doorway is locked";
    //description for the doorway's unlocked state
    private String unlockedDesc = "The doorway is unlocked";




    @Override
    public String Description() {
        return locked ? lockedDesc : unlockedDesc ;
    }
}
