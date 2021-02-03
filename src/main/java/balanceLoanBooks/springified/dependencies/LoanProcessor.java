package balanceLoanBooks.springified.dependencies;

import balanceLoanBooks.springified.dependencies.model.Facility;
import balanceLoanBooks.springified.dependencies.model.Restriction;
import balanceLoanBooks.springified.dependencies.repo.CovenantRepo;
import balanceLoanBooks.springified.dependencies.repo.FacilityRepo;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import balanceLoanBooks.springified.dependencies.model.Loan;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

@SuppressWarnings("ResultOfMethodCallIgnored")
@Slf4j
@Component
public class LoanProcessor {
    private final FacilityRepo facilityRepo;
    private final CovenantRepo covenantRepo;
    //needs a getter for logging purposes
    @Getter private final File assignmentsFile;
    private final File yieldsFile;

    @Autowired
    public LoanProcessor(FacilityRepo facilityRepo,
                         CovenantRepo covenantRepo,
                         @Value("${loans.assignments-output-file-path}") String assignmentsFilePath,
                         @Value("${yields.yields-output-file-path}") String yieldsFilePath) throws IOException {

        this.facilityRepo = facilityRepo;
        this.covenantRepo = covenantRepo;

        assignmentsFile = new File(assignmentsFilePath);
        yieldsFile = new File(yieldsFilePath);
        initFiles(assignmentsFile, yieldsFile);
    }

    /**
    * For all facilities in the LoanProcessor's facilityRepo, write "facility_id,expected_yield" to yieldsFile (CSV format)
     * example (if facilityRepo only contains one facility, then file (after write operation) looks like):
     *          facility_id,expected_yield
     *          1,          323890.25
     */
    public void persistFacilityYields() {
        for (Facility facility: facilityRepo.getAll()) {
            try {
                String yieldData = String.format("%d,%.2f\n", facility.getFacilityId(), facility.getYield());
                Files.append(yieldData, yieldsFile, Charset.defaultCharset());

            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException("Cannot write to yieldsFile in LoanProcessor", e);
            }
        }

        log.info(String.format("Facility yields were successfully persisted to %s", yieldsFile));
    }

    /**
     * Given a loan, for each facility figure out the restrictions to apply
     * and then see if the loan can be applied, if so then assign and update the facility's capacity(amount) and yield
     *
     * @param loan object to be processed
     * @return facility object (in facilityRepo) to which the loan is allocated. If no valid facility exists, returns null
     */
    public Facility process(Loan loan) {
        //local variables
        float defaultLikelihood;
        float expectedYield;

        //iterate through facilities to (hopefully) get a match
        for (Facility facility : facilityRepo.getAll()) {
            if (loanMeetsFacilityCriteria(loan, facility)) {
                defaultLikelihood = loan.getDefaultLikelihood();

                //calculate expected yield of the loan (formula taken from READme)
                expectedYield = (1 - defaultLikelihood) * loan.getInterestRate() * loan.getAmount()
                        - (defaultLikelihood * loan.getAmount())
                        - (loan.getAmount() * facility.getInterestRate());

                //if expected yield is negative, the loan is unprofitable and shouldn't be assigned to this facility
                //otherwise, assign loan to this facility w/side effect of updating facility's capacity(amount) and yield
                if (expectedYield >= 0) {
                    assignLoan(loan, expectedYield, facility);
                    return facility;
                }
            }
        }
        //if no facility can accommodate the loan, return null
        return null;
    }

    private boolean loanMeetsFacilityCriteria(Loan loan, Facility facility) {
        //get Restriction object for current Facility
        int facilityId = facility.getFacilityId();
        Restriction activeRestriction = covenantRepo.getFacilityRestriction(facilityId, facility.getBankId());

        //we must check that the loan's amount, state, and default_likelihood are all valid for this facility
        return !facilityAmountIsExhaustedForTheLoan(loan.getAmount(), facility.getAmount())
                && !loanStateIsBanned(loan.getState(), activeRestriction)
                && loanDefaultLikelihoodSatisfied(loan.getDefaultLikelihood(), activeRestriction);

    }

    private void assignLoan(Loan loan, float expectedYield, Facility facility) {
        facility.setAmount(facility.getAmount() - loan.getAmount());
        facility.setYield(facility.getYield() + expectedYield);
        try {
            String assignmentData = Joiner.on(",").useForNull("").join(loan.getId(), facility.getFacilityId()) + System.lineSeparator();
            Files.append(assignmentData, assignmentsFile, Charset.defaultCharset());

        } catch (IOException e) {
            throw new IllegalStateException("Cannot write to assignmentsFile", e);
        }

    }

    private boolean facilityAmountIsExhaustedForTheLoan(Integer loanAmount, Integer facilityMaxAmount) {
        return facilityMaxAmount - loanAmount < 0;
    }

    private boolean loanStateIsBanned(String loanState, Restriction restriction) {
        if (StringUtils.isEmpty(loanState)) {
            return false;
        }

        return restriction.getBannedStates().contains(loanState);
    }

    private boolean loanDefaultLikelihoodSatisfied(float loanDefaultLikelihood, Restriction restriction) {
        return (restriction.getMaxDefaultLikelyhood() != null) && (restriction.getMaxDefaultLikelyhood() >= loanDefaultLikelihood);
    }

    private void initFiles(File assignmentsFile, File yieldsFile) throws IOException {
        //clear contents of assignments file and write header to first line
        if (assignmentsFile.delete()) {
            assignmentsFile.createNewFile();
        }
        String assignmentFileHeader = "loan_id,facility_id\n";
        Files.append(assignmentFileHeader, assignmentsFile, Charset.defaultCharset());

        //clear contents of yields file and write header to first line
        if (yieldsFile.delete()) {
            yieldsFile.createNewFile();
        }
        String yieldsFileHeader = "facility_id,expected_yield\n";
        Files.append(yieldsFileHeader, yieldsFile, Charset.defaultCharset());

    }
}
