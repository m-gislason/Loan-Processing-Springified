package balanceLoanBooks.springified.dependencies.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"facilityId", "maxDefaultLikelihood", "bankId", "bannedState"})
public class Covenant {

    @JsonProperty("facility_id")
    private Integer facilityId;

    @JsonProperty("max_default_likelihood")
    private Float maxDefaultLikelihood = 0.0f;

    @JsonProperty("bank_id")
    private Integer bankId;

    @JsonProperty("banned_state")
    private String bannedState;

}
