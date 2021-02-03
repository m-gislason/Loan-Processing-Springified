package balanceLoanBooks.springified.dependencies.repo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import balanceLoanBooks.springified.dependencies.model.Facility;

import java.util.Comparator;
import java.util.List;

@Component
public class FacilityRepo extends AbstractRepo<Facility> {
    @Value("${facilities.input-file}")
    private String file;

    public FacilityRepo() {
        super();
    }

    public List<Facility> getAll() {
        return super.getAll(Facility.class, file, new Comparator<Facility>() {
            @Override
            public int compare(Facility o1, Facility o2) {
                return o1.getInterestRate().compareTo(o2.getInterestRate());
            }
        });
    }
}
