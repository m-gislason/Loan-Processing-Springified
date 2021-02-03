package balanceLoanBooks.springified.dependencies.repo;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import balanceLoanBooks.springified.dependencies.model.Loan;

import java.util.List;

@Component
public class LoanRepo extends AbstractRepo<Loan> {
    @Value("${loans.input-file}")
    private String file;

    public LoanRepo() {
        super();
    }

    @SneakyThrows
    public List<Loan> getAll() {
        return super.getAll(Loan.class, file);
    }


}
