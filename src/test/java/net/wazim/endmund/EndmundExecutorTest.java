package net.wazim.endmund;

import net.wazim.endmund.domain.GuardianClueAndSolution;
import org.junit.Test;

import static net.wazim.endmund.EndmundExecutor.createHint;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class EndmundExecutorTest {

    @Test
    public void canCreateAValidHint() {
        GuardianClueAndSolution clueAndSolution = new GuardianClueAndSolution("This lord is more cunning", "archer", 6);
        assertThat(createHint(clueAndSolution), is("A....."));
    }

}