/**
 * A Place for all constants we may use in text
 */


public class TextConstants {

    public static final String helpOutput = "A list of valid commands:" +
            "\n >  `inspect`:\tInspect the current room" +
            "\n >  `inspect <itemName>`:\tInspect an item in the room or inventory" +
            "\n >  `take <itemName>`:\tTake an item in the current room" +
            "\n >  `drop <itemName>`:\tDrop an item from your inventory" +
            "\n >  `move <direction>`:\tTry to move in the specified direction" +
            "\n >  `take <itemName>`:\tTake an item in the current room" +
            "\n >  `use <itemName>`:\tUse an item in your inventory" +
            "\n >  `inventory`:\tDisplay the status of your inventory" +
            "\n >  `status`:\tDisplay the status of your character" +
            "\n >  `end`:\tEnd your turn" +
            "\n >  `help`:\tGet a DM of this help message";


    ////////////////PRE QUEST STRINGS ///////////////////////
    public static final String noActiveQuest = "No active quest!\nPlease create a new quest with \"Start Quest\"";
    public static final String partyMembersHeader = "The current party members are:\n";
    public static final String noPartyMembers = "There are no current party members.";
    public static final String notAPartyMember = "You are not a member of this party.";
    public static final String alreadyJoined = "You have already joined the party.";
    public static final String cannotStartQuest = "You must join before starting a quest. Type \"join\" to join.";
    ///////////////////////////////////////

    ////// ROOM STRINGS ////////////////
    public static final String inspectRoomOnTurnStart = "You take in your surroundings.";
    public static final String allPlayersAtExit = "All players have made it to the exit. Great job!";
    public static final String playerDies = "A player has fallen. The dungeon closes...";
    ////////////////////////////////////
}
