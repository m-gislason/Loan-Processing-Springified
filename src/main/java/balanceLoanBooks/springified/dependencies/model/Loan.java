package balanceLoanBooks.springified.dependencies.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"interestRate", "amount", "id", "defaultLikelihood", "state"})
public class Loan {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("interest_rate")
    private Float interestRate;

    @JsonProperty("default_likelihood")
    private Float defaultLikelihood;

    @JsonProperty("state")
    private String state;

}
