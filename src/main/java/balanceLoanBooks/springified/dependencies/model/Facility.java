package balanceLoanBooks.springified.dependencies.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"amount", "interestRate", "id", "bankId"})
@Builder
public class Facility {

    @JsonProperty("amount")
    @JsonDeserialize(using = DoubleToIntDeser.class)
    private Integer amount;

    @JsonProperty("interest_rate")
    private Float interestRate;

    @JsonProperty("id")
    private Integer facilityId;

    @JsonProperty("bank_id")
    private Integer bankId;

    @JsonIgnore
    private float yield;

    private static class DoubleToIntDeser extends JsonDeserializer {

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return p.readValueAs(Double.class).intValue();
        }
    }
}
