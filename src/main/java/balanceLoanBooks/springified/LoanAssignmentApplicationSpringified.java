package balanceLoanBooks.springified;

import balanceLoanBooks.springified.dependencies.LoanProcessor;
import balanceLoanBooks.springified.dependencies.model.Facility;
import balanceLoanBooks.springified.dependencies.repo.LoanRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import balanceLoanBooks.springified.dependencies.model.Loan;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@Slf4j
public class LoanAssignmentApplicationSpringified {
    private final LoanProcessor processor;
    private final LoanRepo loanRepo;

    @Autowired
    LoanAssignmentApplicationSpringified(LoanProcessor processor, LoanRepo loanRepo) {
        this.processor = processor;
        this.loanRepo = loanRepo;
    }
    public static void main(String[] args) {
        SpringApplication.run(LoanAssignmentApplicationSpringified.class, args);
    }

    @PostConstruct
    public void processLoans() {
        log.info("Starting application, creating loan processor\n");

        //Get the list of loans to process
        List<Loan> loans = loanRepo.getAll();


        //For each loan, check if it can be assigned to a facility, and if so write to file
        AtomicInteger successfulAssignmentCount = new AtomicInteger();
        loans.stream().forEach(loan -> {
            Facility f = processor.process(loan);
            //if a loan can't be assigned, the LoanProcessor returns null & doesn't write anything to the assignmentsFile
            if (f == null) {
                log.info("Loan {} could not be assigned, no assignment written to file", loan);
            } else {
                successfulAssignmentCount.getAndIncrement();
                log.debug("Loan {} assigned to facility {}", loan, f);
            }
        });
        log.info(String.format("%s of %d loans were successfully assigned", successfulAssignmentCount, loans.size()));
        log.info(String.format("All successful loan assignments were persisted to %s", processor.getAssignmentsFile()));

        //Write the each facility's yield to file
        processor.persistFacilityYields();
    }

}
