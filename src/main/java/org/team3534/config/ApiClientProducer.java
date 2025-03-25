package org.team3534.config;

import com.tba.api.DistrictApi;
import com.tba.api.DistrictsApi;
import com.tba.api.EventApi;
import com.tba.api.EventsApi;
import com.tba.api.SearchIndexApi;
import com.tba.api.TeamApi;
import com.tba.api.TeamsApi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.net.URI;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

@ApplicationScoped
public class ApiClientProducer {

    @Produces
    @ApplicationScoped
    public DistrictsApi createDistrictsApi() {
        return build(DistrictsApi.class);
    }

    @Produces
    @ApplicationScoped
    public DistrictApi createDistrictApi() {
        return build(DistrictApi.class);
    }

    @Produces
    @ApplicationScoped
    public EventsApi createEventsApi() {
        return build(EventsApi.class);
    }

    @Produces
    @ApplicationScoped
    public EventApi createEventApi() {
        return build(EventApi.class);
    }

    @Produces
    @ApplicationScoped
    public TeamsApi createTeamsApi() {
        return build(TeamsApi.class);
    }

    @Produces
    @ApplicationScoped
    public TeamApi createTeamApi() {
        return build(TeamApi.class);
    }

    @Produces
    @ApplicationScoped
    public SearchIndexApi createSearchIndexApi() {
        return build(SearchIndexApi.class);
    }

    private static <T> T build(Class<T> clazz) {
        return RestClientBuilder.newBuilder()
                .baseUri(URI.create("https://www.thebluealliance.com/api/v3"))
                .register(TbaAuthHeaderFilter.class)
                .build(clazz);
    }
}
