package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.*;
import be.swop.groep11.main.resource.ResourcePlanner;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BranchOfficeProxyTest extends BranchOfficeTest{

    BranchOfficeProxy branchOfficeProxy1, branchOfficeProxy2, branchOfficeProxy3;

    @Before
    public void setUpProxyTest(){
        branchOffice1 = new BranchOfficeProxy(branchOffice1);
        branchOffice2 = new BranchOfficeProxy(branchOffice2);
        branchOffice3 = new BranchOfficeProxy(branchOffice3);
    }

    @Test
    public void proxyInProxyTest() throws Exception{
        BranchOfficeProxy proxy = new BranchOfficeProxy(branchOffice1);
        BranchOfficeProxy proxy2 = new BranchOfficeProxy(proxy);
    }

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
        assertFalse(proxy.equals("Random String"));
        assertFalse(proxy.equals(null));
    }

    @Test
    public void testName() throws Exception{
        BranchOffice branchOffice = new BranchOffice("branch office", "locatie", mock(ProjectRepository.class), mock(ResourcePlanner.class));
        BranchOfficeProxy proxy = new BranchOfficeProxy(branchOffice);
        assertTrue(proxy.getName().equals("branch office"));
        proxy.setName("test name");
        assertTrue(proxy.getName().equals("test name"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalName() throws Exception{
        BranchOffice branchOffice = new BranchOffice("branch office", "locatie", mock(ProjectRepository.class), mock(ResourcePlanner.class));
        BranchOfficeProxy proxy = new BranchOfficeProxy(branchOffice);
        proxy.setName(null);
        proxy.setName("");
    }

    @Test
    public void testLocation() throws Exception{
        BranchOffice branchOffice = new BranchOffice("branch office", "locatie", mock(ProjectRepository.class), mock(ResourcePlanner.class));
        BranchOfficeProxy proxy = new BranchOfficeProxy(branchOffice);
        assertTrue(proxy.getLocation().equals("locatie"));
        proxy.setLocation("op caf?");
        assertTrue(proxy.getLocation().equals("op caf?"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalLocation() throws Exception{
        BranchOffice branchOffice = new BranchOffice("branch office", "locatie", mock(ProjectRepository.class), mock(ResourcePlanner.class));
        BranchOfficeProxy proxy = new BranchOfficeProxy(branchOffice);
        proxy.setLocation("");
        proxy.setLocation(null);
    }

    @Test
    public void delegateTask_ProperToOther() throws Exception{
        super.delegateTask_ProperToOther();
//        branchOfficeProxy1.delegateTask(task, branchOfficeProxy2);
//
//        assertTrue(branchOfficeProxy1.getOwnTasks().contains(task));
//        assertFalse(branchOfficeProxy1.getDelegatedTasks().contains(task));
//        assertFalse(branchOfficeProxy1.getUnplannedTasks().contains(task));
//        assertFalse(branchOfficeProxy2.getOwnTasks().contains(task));
//        assertTrue(branchOfficeProxy2.getDelegatedTasks().contains(task));
//        assertTrue(branchOfficeProxy2.getUnplannedTasks().contains(task));
//        assertTrue(task.getDelegatedTo().equals(branchOfficeProxy2));
//        assertTrue(task.isDelegated());
    }

    @Test
    public void delegateTask_OtherToProper() throws Exception {
        super.delegateTask_OtherToProper();
//        branchOfficeProxy1.delegateTask(task, branchOfficeProxy2);
//        branchOfficeProxy2.delegateTask(task, branchOfficeProxy1);
//
//        assertTrue(branchOfficeProxy1.getOwnTasks().contains(task));
//        assertFalse(branchOfficeProxy1.getDelegatedTasks().contains(task));
//        assertTrue(branchOfficeProxy1.getUnplannedTasks().contains(task));
//        assertFalse(branchOfficeProxy2.getOwnTasks().contains(task));
//        assertFalse(branchOfficeProxy2.getDelegatedTasks().contains(task));
//        assertFalse(branchOfficeProxy2.getUnplannedTasks().contains(task));
    }

    @Test
    public void delegateTask_OtherToOther() throws Exception {
        super.delegateTask_OtherToOther();
//        branchOfficeProxy1.delegateTask(task, branchOfficeProxy2);
//        branchOfficeProxy2.delegateTask(task, branchOfficeProxy3);
//
//        assertFalse(branchOfficeProxy2.getOwnTasks().contains(task));
//        assertFalse(branchOfficeProxy2.getDelegatedTasks().contains(task));
//        assertFalse(branchOfficeProxy2.getUnplannedTasks().contains(task));
//        assertFalse(branchOfficeProxy3.getOwnTasks().contains(task));
//        assertTrue(branchOfficeProxy3.getDelegatedTasks().contains(task));
//        assertTrue(branchOfficeProxy3.getUnplannedTasks().contains(task));
    }

    @Test (expected = IllegalArgumentException.class)
    public void delegateTask_CanNotPlanTask() throws Exception {
        super.delegateTask_CanNotPlanTask();
//        when(resourcePlanner2.hasEnoughResourcesToPlan(task)).thenReturn(false);
//        branchOfficeProxy1.delegateTask(task, branchOfficeProxy2);
    }

    @Test
    public void addEmployeeTest() {
        super.addEmployeeTest();
//        branchOfficeProxy1.addEmployee(projectManager);
//        branchOfficeProxy1.addEmployee(developer1);
//        branchOfficeProxy1.addEmployee(developer2);
//
//        assertTrue(branchOfficeProxy1.getEmployees().size() == 3);
//        assertTrue(branchOfficeProxy1.getDevelopers().size() == 2);
    }

}
