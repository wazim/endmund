package net.wazim.endmund.utils;

import org.junit.Test;

import static net.wazim.endmund.utils.NextIdGenerator.getNextId;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class NextIdGeneratorTest {

    @Test
    public void canGetTheNextId() {
        assertThat(getNextId(), is(0));
        assertThat(getNextId(), is(1));
        assertThat(getNextId(), is(2));
        assertThat(getNextId(), is(3));
    }

}