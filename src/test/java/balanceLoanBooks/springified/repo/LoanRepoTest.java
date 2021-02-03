package balanceLoanBooks.springified.repo;

import balanceLoanBooks.springified.dependencies.repo.LoanRepo;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LoanRepoTest {

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("dataset", "small");
    }

    @Test
    public void readAllSmallDataset() {
        LoanRepo repo = new LoanRepo();
        assertNotNull(repo.getAll());
        assertEquals(3, repo.getAll().size());
    }

    @Test
    public void readAllLargeDataset() {
        System.setProperty("dataset", "large");
        LoanRepo repo = new LoanRepo();
        assertNotNull(repo.getAll());
        assertEquals(425, repo.getAll().size());
    }
}
