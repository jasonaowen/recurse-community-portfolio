package com.recurse.portfolio.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecurseProfile {
    private static final String DIRECTORY = "https://www.recurse.com/directory/";

    @JsonProperty("id")
    int userId;

    @NonNull String name;
    @NonNull String email;
    String github;
    String twitter;

    @JsonProperty("image_path")
    @NonNull String imageUrl;

    @NonNull String directoryUrl;

    @NonNull List<Stint> stints;

    public RecurseProfile() {}

    @JsonProperty("slug")
    public void setSlug(String slug) {
        directoryUrl = DIRECTORY + slug;
    }
}
