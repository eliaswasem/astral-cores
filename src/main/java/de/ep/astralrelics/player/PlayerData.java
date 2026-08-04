package de.ep.astralrelics.player;

public class PlayerData {

    private String leftSlot;
    private String rightSlot;


    public PlayerData() {
        this.leftSlot = null;
        this.rightSlot = null;
    }


    public String getLeftSlot() {
        return leftSlot;
    }


    public void setLeftSlot(String relic) {
        this.leftSlot = relic;
    }


    public String getRightSlot() {
        return rightSlot;
    }


    public void setRightSlot(String relic) {
        this.rightSlot = relic;
    }
}