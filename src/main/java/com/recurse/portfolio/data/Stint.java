package com.recurse.portfolio.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties("in_progress")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Stint {
    @JsonProperty("id")
    int stintId;

    Batch batch;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    LocalDate startDate;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    LocalDate endDate;

    boolean forHalfBatch;

    String title;

    String type;
}
