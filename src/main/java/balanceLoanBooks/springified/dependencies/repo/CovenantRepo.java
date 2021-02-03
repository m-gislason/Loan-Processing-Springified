package balanceLoanBooks.springified.dependencies.repo;

import balanceLoanBooks.springified.dependencies.model.Covenant;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import balanceLoanBooks.springified.dependencies.model.Restriction;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

@Component
public class CovenantRepo extends AbstractRepo<Covenant> {

    @Value("${covenants.input-file}")
    private String file;

    //covenants applicable to all facilities of a bank
    private final Multimap<Integer, Covenant> covenantsForAllFacilities;

    //facility specific covenants
    private final Multimap<Integer, Covenant> facilitySpecificCovenants;

    public CovenantRepo() {
        super();
        this.covenantsForAllFacilities = ArrayListMultimap.create();
        this.facilitySpecificCovenants = ArrayListMultimap.create();
    }

    @PostConstruct
    protected void init() {
        List<Covenant> list = getAll();
        for (Covenant c : list) {
            if (c.getFacilityId() == null) {
                //restriction applies to facilities for the bank
                covenantsForAllFacilities.put(c.getBankId(), c);
            } else {
                facilitySpecificCovenants.put(c.getFacilityId(), c);
            }
        }
    }

    @SneakyThrows
    public List<Covenant> getAll() {
        return super.getAll(Covenant.class, file);
    }

    public Restriction getFacilityRestriction(Integer facility, Integer bankId) {
        //Set<Covenant> ret = new HashSet<>();

        Restriction restriction = new Restriction();

        Collection<Covenant> covsByBank = covenantsForAllFacilities.get(bankId);
        if (covsByBank != null) {
            covsByBank.forEach(c -> {
                if (c.getBannedState() != null) {
                    restriction.getBannedStates().add(c.getBannedState());
                }

                //unsure about cases of input where for the same bank (no facility) we can get multiple default thresholds.
                //so just taking the max of those. this step could simply be get the first max default likelihood for the bank.
                Float banksMaxDefaultLikelihood = c.getMaxDefaultLikelihood();
                Float restrictionMaxDefaultLikelihood = restriction.getMaxDefaultLikelyhood();
                if (banksMaxDefaultLikelihood != null) {
                    if (restrictionMaxDefaultLikelihood == null) {
                        restriction.setMaxDefaultLikelyhood(banksMaxDefaultLikelihood);
                    } else {
                        restriction.setMaxDefaultLikelyhood(Math.max(restrictionMaxDefaultLikelihood, banksMaxDefaultLikelihood));
                    }
                }
            });
        }

        Collection<Covenant> covsByFacility = facilitySpecificCovenants.get(facility);

        if (covsByFacility != null) {
            Float maxDefaultLikelihood = null;
            for (Covenant c : covsByFacility) {
                if (c.getBannedState() != null) {
                    restriction.getBannedStates().add(c.getBannedState());
                }

                //override the banks max default likelihood at facility level if given
                //again, assuming that input data could be multiple max default values for same facility with different banned states.
                Float facilityMaxDefaultLikelyhood = c.getMaxDefaultLikelihood();
                if (facilityMaxDefaultLikelyhood != null) {
                    if (maxDefaultLikelihood == null) {
                        maxDefaultLikelihood = facilityMaxDefaultLikelyhood;
                    } else {
                        maxDefaultLikelihood = Math.max(facilityMaxDefaultLikelyhood, maxDefaultLikelihood);
                    }
                }
            }

            if (maxDefaultLikelihood != null) {
                restriction.setMaxDefaultLikelyhood(maxDefaultLikelihood);
            }
        }

        return restriction;
    }


}
