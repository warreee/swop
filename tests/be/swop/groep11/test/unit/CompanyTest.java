package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Company;
import be.swop.groep11.main.resource.ResourceTypeRepository;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * Klasse om Company te testen.
 */
public class CompanyTest {

    private ResourceTypeRepository resourceTypeRepository;
    private Company company1;
    private BranchOffice branchOffice1, branchOffice2, branchOffice3;

    @Before
    public void setUp() throws Exception {
        this.resourceTypeRepository = new ResourceTypeRepository();
        this.company1 = new Company("comp1", resourceTypeRepository);
        this.branchOffice1 = mock(BranchOffice.class);
        this.branchOffice2 = mock(BranchOffice.class);
        this.branchOffice3 = mock(BranchOffice.class);
    }

    @Test
    public void test() throws Exception {
        fail("implement tests");

    }

    @Test (expected = IllegalArgumentException.class)
    public void addBranchOfficeTest() throws Exception {
        company1.addBranchOffice(branchOffice1);
        company1.addBranchOffice(branchOffice2);
        company1.addBranchOffice(branchOffice3);
        assertTrue(company1.getBranchOffices().size() == 3);
        company1.addBranchOffice(null);
        company1.addBranchOffice(branchOffice1);
        assertTrue(company1.getBranchOffices().size() == 3);
    }


}
