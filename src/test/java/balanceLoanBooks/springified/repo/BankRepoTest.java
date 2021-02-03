package balanceLoanBooks.springified.repo;

import balanceLoanBooks.springified.dependencies.repo.BankRepo;
import org.junit.jupiter.api.Test;
import org.testng.annotations.BeforeClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BankRepoTest {


    @BeforeClass
    public static void beforeClass() {
        System.setProperty("dataset", "small");
    }

    @Test
    public void readAllSmallDataset() {
        BankRepo repo = new BankRepo();
        assertNotNull(repo.getAll());
        assertEquals(2, repo.getAll().size());
    }

    @Test
    public void readAllLargeDataset() {
        System.setProperty("dataset", "large");
        BankRepo repo = new BankRepo();
        assertNotNull(repo.getAll());
        assertEquals(5, repo.getAll().size());
    }
}
