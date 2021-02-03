package balanceLoanBooks.springified.dependencies.repo;

import balanceLoanBooks.springified.dependencies.model.Bank;
import lombok.SneakyThrows;

import java.util.List;

public class BankRepo extends AbstractRepo<Bank> {

    private static final String file = "banks.csv";

    public BankRepo() {
        super();
        // getAll(Bank.class, file);
    }


    @SneakyThrows
    public List<Bank> getAll() {
        return super.getAll(Bank.class, file);
    }

}
