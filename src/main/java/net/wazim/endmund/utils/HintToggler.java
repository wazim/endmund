package net.wazim.endmund.utils;

public class HintToggler {

    private boolean toggleOn = false;

    public void toggleHints() {
        toggleOn = !toggleOn;
    }

    public boolean isToggleOn() {
        return toggleOn;
    }

}
