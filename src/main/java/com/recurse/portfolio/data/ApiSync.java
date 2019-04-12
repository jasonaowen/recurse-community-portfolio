package com.recurse.portfolio.data;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@Log
public class ApiSync implements ApplicationRunner {
    private static final String PROFILES_URL =
        "https://www.recurse.com/api/v1/profiles?limit={limit}&offset={offset}";
    private static final int PROFILES_PAGE_SIZE = 50;

    @Autowired
    ConfigurableApplicationContext application;

    @Autowired
    DataSource dataSource;

    @Value("${ACCESS_TOKEN}")
    private String accessToken;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!args.getNonOptionArgs().contains("apiSync")) {
            return;
        }

        log.info("Syncing database with Recurse Center API...");
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        final List<RecurseProfile> profiles = fetchProfiles();
        log.info(String.format("Fetched %d profiles", profiles.size()));

        updateProfiles(profiles, connection);
        connection.commit();
        log.info("Sync complete.");
        application.close();
    }

    private List<RecurseProfile> fetchProfiles() {
        RestTemplate client = new RestTemplate();
        HttpEntity<String> entity = getAuthHeader();
        final ParameterizedTypeReference<List<RecurseProfile>> responseType
            = new ParameterizedTypeReference<>() {
        };
        List<RecurseProfile> profiles = new ArrayList<>();
        int offset = 0;
        List<RecurseProfile> page;

        do {
            page = Optional.ofNullable(
                client.exchange(
                    PROFILES_URL,
                    HttpMethod.GET,
                    entity,
                    responseType,
                    PROFILES_PAGE_SIZE,
                    offset
                ).getBody()
            ).orElse(Collections.emptyList());
            profiles.addAll(page);
            offset += PROFILES_PAGE_SIZE;
        } while (!page.isEmpty());

        return profiles;
    }

    private void updateProfiles(
        List<RecurseProfile> profiles,
        Connection connection
    ) throws SQLException {
        PreparedStatement upsertProfile = connection.prepareStatement(
            "INSERT INTO recurse_profiles (" +
                "  recurse_profile_id," +
                "  name," +
                "  email," +
                "  github," +
                "  twitter," +
                "  image_url," +
                "  directory_url" +
                ") " +
                "VALUES(?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (recurse_profile_id) " +
                "DO UPDATE SET" +
                "  name = EXCLUDED.name," +
                "  email = EXCLUDED.email," +
                "  github = EXCLUDED.github," +
                "  twitter = EXCLUDED.twitter," +
                "  image_url = EXCLUDED.image_url," +
                "  directory_url = EXCLUDED.directory_url"
        );
        for (RecurseProfile profile : profiles) {
            upsertProfile.setInt(1, profile.getUserId());
            upsertProfile.setString(2, profile.getName());
            upsertProfile.setString(3, profile.getEmail());
            upsertProfile.setString(4, profile.getGithub());
            upsertProfile.setString(5, profile.getTwitter());
            upsertProfile.setString(6, profile.getImageUrl());
            upsertProfile.setString(7, profile.getDirectoryUrl());
            upsertProfile.executeUpdate();
            updateStints(profile.getUserId(), profile.getStints(), connection);
        }
    }

    private void updateStints(
        int profileId,
        List<Stint> stints,
        Connection connection
    ) throws SQLException {
        PreparedStatement upsertStint = connection.prepareStatement(
            "INSERT INTO stints (" +
                "  recurse_profile_id," +
                "  batch_id," +
                "  start_date," +
                "  end_date," +
                "  for_half_batch," +
                "  title," +
                "  stint_type" +
                ") " +
                "VALUES(?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (recurse_profile_id, start_date) " +
                "DO UPDATE SET" +
                "  batch_id = EXCLUDED.batch_id," +
                "  end_date = EXCLUDED.end_date," +
                "  for_half_batch = EXCLUDED.for_half_batch," +
                "  title = EXCLUDED.title," +
                "  stint_type = EXCLUDED.stint_type"
        );
        for (Stint stint : stints) {
            upsertStint.setInt(1, profileId);
            if (stint.getBatch() == null) {
                upsertStint.setNull(2, Types.INTEGER);
            } else {
                updateBatch(stint.getBatch(), connection);
                upsertStint.setInt(2, stint.getBatch().getBatchId());
            }
            upsertStint.setDate(3, Date.valueOf(stint.getStartDate()));
            upsertStint.setDate(4, sqlDate(stint.getEndDate()));
            upsertStint.setBoolean(5, stint.isForHalfBatch());
            upsertStint.setString(6, stint.getTitle());
            upsertStint.setString(7, stint.getType());
            upsertStint.executeUpdate();
        }
    }

    private void updateBatch(
        Batch batch,
        Connection connection
    ) throws SQLException {
        PreparedStatement upsertBatch = connection.prepareStatement(
            "INSERT INTO batches (" +
                "  batch_id," +
                "  name," +
                "  name_alt," +
                "  name_short" +
                ") " +
                "VALUES(?, ?, ?, ?) " +
                "ON CONFLICT (batch_id) " +
                "DO UPDATE SET" +
                "  name = EXCLUDED.name," +
                "  name_alt = EXCLUDED.name_alt," +
                "  name_short = EXCLUDED.name_short"
        );
        upsertBatch.setInt(1, batch.getBatchId());
        upsertBatch.setString(2, batch.getName());
        upsertBatch.setString(3, batch.getAltName());
        upsertBatch.setString(4, batch.getShortName());
        upsertBatch.executeUpdate();
    }

    private Date sqlDate(LocalDate date) {
        return Optional.ofNullable(date)
            .map(Date::valueOf)
            .orElse(null);
    }

    private HttpEntity<String> getAuthHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<>("parameters", httpHeaders);
    }
}
