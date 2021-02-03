package balanceLoanBooks.springified;

import balanceLoanBooks.springified.dependencies.LoanProcessor;
import balanceLoanBooks.springified.dependencies.model.Facility;
import balanceLoanBooks.springified.dependencies.model.Loan;
import balanceLoanBooks.springified.dependencies.model.Restriction;
import balanceLoanBooks.springified.dependencies.repo.CovenantRepo;
import balanceLoanBooks.springified.dependencies.repo.FacilityRepo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

//These test cases should pass.
public class LoanProcessorTest {


    @Mock
    private FacilityRepo facilityRepo;

    @Mock
    private CovenantRepo covenantRepo;

    @InjectMocks
    private LoanProcessor processor;

    //facilitates mocking of facilityRepo
    List<Facility> facilities;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Facility f1 = Facility.builder().amount(10000).bankId(1).facilityId(1).interestRate(0.01f).build();
        Facility f2 = Facility.builder().amount(20000).bankId(1).facilityId(2).interestRate(0.01f).build();
        Facility f3 = Facility.builder().amount(10000).bankId(2).facilityId(3).interestRate(0.02f).build();
        Facility f4 = Facility.builder().amount(20000).bankId(2).facilityId(4).interestRate(0.02f).build();

        facilities = Lists.newArrayList(f1, f2, f3, f4);
        when(facilityRepo.getAll()).thenReturn(facilities);

        Restriction r1 = Restriction.builder().bannedStates(Sets.newHashSet("IL", "CA")).facilityId(1).maxDefaultLikelyhood(0.02f).build();
        Restriction r2 = Restriction.builder().bannedStates(Sets.newHashSet("MA, AL")).facilityId(2).maxDefaultLikelyhood(0.02f).build();
        Restriction r3 = Restriction.builder().bannedStates(Sets.newHashSet("HI, GA")).facilityId(3).maxDefaultLikelyhood(0.02f).build();
        Restriction r4 = Restriction.builder().bannedStates(Sets.newHashSet("FL")).facilityId(4).maxDefaultLikelyhood(0.02f).build();

        when(covenantRepo.getFacilityRestriction(eq(1), eq(1))).thenReturn(r1);
        when(covenantRepo.getFacilityRestriction(eq(2), eq(1))).thenReturn(r2);
        when(covenantRepo.getFacilityRestriction(eq(3), eq(2))).thenReturn(r3);
        when(covenantRepo.getFacilityRestriction(eq(4), eq(2))).thenReturn(r4);

    }

    @Test
    public void testAssignmentSuccess() {
        Loan loan = Loan.builder().amount(100).defaultLikelihood(0f).interestRate(.04f).state("IL").build();
        Facility f = processor.process(loan);

        assertNotNull(f.getYield());
        assertEquals(3.0f, f.getYield(), .1);
    }

    @Test
    public void testNoAssignment() {
        //this loan has an interest rate of 0% and thus will be unprofitable for any facility
        Loan loan = Loan.builder().amount(100).defaultLikelihood(0f).interestRate(.00f).state("IL").build();
        //thus we expect null return
        Facility f = processor.process(loan);
        assertNull(f);
    }
}
