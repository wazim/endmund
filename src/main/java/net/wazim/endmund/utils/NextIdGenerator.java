package net.wazim.endmund.utils;

public class NextIdGenerator {

    private static int id = 0;

    public static int getNextId() {
        int thisId = id;
        id++;
        return thisId;
    }

}
