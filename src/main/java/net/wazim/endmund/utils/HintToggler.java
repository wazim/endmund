package net.wazim.endmund.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HintToggler {

    private Logger logger = LoggerFactory.getLogger(HintToggler.class);
    private boolean toggleOn = false;

    public HintToggler() {
        if (System.getProperty("hint.toggle") != null) {
            toggleOn = Boolean.valueOf(System.getProperty("hint.toggle"));
            logger.info("Hint Toggle is set to : " + isToggleOn());
        } else {
            toggleOn = false;
        }
    }

    public void toggleHints() {
        toggleOn = !toggleOn;
        logger.info("Hint Toggle is set to : " + isToggleOn());
    }

    public boolean isToggleOn() {
        return toggleOn;
    }

}
