package balanceLoanBooks.springified.dependencies.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"bankId", "bankName"})
public class Bank {

    @JsonProperty("id")
    private Integer bankId;

    @JsonProperty("name")
    private String bankName;
}
