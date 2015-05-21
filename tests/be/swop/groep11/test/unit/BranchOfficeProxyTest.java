package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.*;
import be.swop.groep11.main.resource.ResourcePlanner;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class BranchOfficeProxyTest {

    @Test
    public void equals_TrueTest() {
        BranchOffice branchOffice = new BranchOffice("branch office", "locatie", mock(ProjectRepository.class), mock(ResourcePlanner.class));
        BranchOfficeProxy proxy = new BranchOfficeProxy(branchOffice);
        assertTrue(branchOffice.equals(proxy));
        assertTrue(proxy.equals(branchOffice));
        assertTrue(proxy.equals(proxy));
    }

    @Test
    public void equals_FalseTest() {
        BranchOffice branchOffice = new BranchOffice("branch office", "locatie", mock(ProjectRepository.class), mock(ResourcePlanner.class));
        BranchOfficeProxy proxy = new BranchOfficeProxy(branchOffice);
        BranchOffice branchOffice2 = new BranchOffice("branch office2", "locatie2", mock(ProjectRepository.class), mock(ResourcePlanner.class));
        assertFalse(branchOffice2.equals(proxy));
        assertFalse(proxy.equals(branchOffice2));
    }

}
