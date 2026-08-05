package de.ep.astralrelics.playerdata;

import de.ep.astralrelics.relic.RelicType;

public class PlayerData {

    private RelicType leftRelic;
    private RelicType rightRelic;


    public PlayerData() {
        this.leftRelic = null;
        this.rightRelic = null;
    }


    public RelicType getLeftRelic() {
        return leftRelic;
    }


    public void setLeftRelic(RelicType relic) {
        this.leftRelic = relic;
    }


    public RelicType getRightRelic() {
        return rightRelic;
    }


    public void setRightRelic(RelicType relic) {
        this.rightRelic = relic;
    }
}
