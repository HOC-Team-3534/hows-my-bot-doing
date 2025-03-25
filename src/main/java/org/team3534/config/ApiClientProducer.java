package org.team3534.config;

import com.tba.api.DistrictApi;
import com.tba.api.DistrictsApi;
import com.tba.api.EventApi;
import com.tba.api.EventsApi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.net.URI;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

@ApplicationScoped
public class ApiClientProducer {

    @Produces
    @ApplicationScoped
    public DistrictsApi createDistrictsApi() {
        return RestClientBuilder.newBuilder()
                .baseUri(URI.create("https://www.thebluealliance.com/api/v3"))
                .register(TbaAuthHeaderFilter.class)
                .build(DistrictsApi.class);
    }

    @Produces
    @ApplicationScoped
    public DistrictApi createDistrictApi() {
        return RestClientBuilder.newBuilder()
                .baseUri(URI.create("https://www.thebluealliance.com/api/v3"))
                .register(TbaAuthHeaderFilter.class)
                .build(DistrictApi.class);
    }

    @Produces
    @ApplicationScoped
    public EventsApi createEventsApi() {
        return RestClientBuilder.newBuilder()
                .baseUri(URI.create("https://www.thebluealliance.com/api/v3"))
                .register(TbaAuthHeaderFilter.class)
                .build(EventsApi.class);
    }

    @Produces
    @ApplicationScoped
    public EventApi createEventApi() {
        return RestClientBuilder.newBuilder()
                .baseUri(URI.create("https://www.thebluealliance.com/api/v3"))
                .register(TbaAuthHeaderFilter.class)
                .build(EventApi.class);
    }
}
