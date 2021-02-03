package balanceLoanBooks.springified.dependencies.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Restriction {

    private Integer facilityId;

    @Builder.Default
    private Set<String> bannedStates = new HashSet<>();

    private Float maxDefaultLikelyhood;
}
