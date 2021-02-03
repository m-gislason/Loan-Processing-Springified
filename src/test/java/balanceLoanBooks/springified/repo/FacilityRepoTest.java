package balanceLoanBooks.springified.repo;

import balanceLoanBooks.springified.dependencies.repo.FacilityRepo;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FacilityRepoTest {

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("dataset", "small");
    }

    @Test
    public void readAllSmallDataset() {
        FacilityRepo repo = new FacilityRepo();
        assertNotNull(repo.getAll());
        assertEquals(2, repo.getAll().size());
    }

    @Test
    public void readAllLargeDataset() {
        System.setProperty("dataset", "large");
        FacilityRepo repo = new FacilityRepo();
        assertNotNull(repo.getAll());
        assertEquals(15, repo.getAll().size());
    }
}
