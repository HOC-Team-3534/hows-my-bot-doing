package org.team3534.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tba.api.TeamApi;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Data;

@Produces
@ApplicationScoped
public class TeamService {
    private Cache<OprsCacheKey, Float> highestOprsCache;

    @Inject TeamApi teamApi;
    @Inject EventService eventService;

    @PostConstruct
    void init() {
        highestOprsCache =
                Caffeine.newBuilder()
                        .expireAfterWrite(10, TimeUnit.MINUTES)
                        .maximumSize(100)
                        .build();
    }

    @Data
    @AllArgsConstructor
    public static class OprsCacheKey {
        private String key;
        private int year;
    }

    public float getHighestOPR(String key, int year) {
        return highestOprsCache.get(
                new OprsCacheKey(key, year),
                (oprsCacheKey) -> {
                    var events =
                            teamApi.getTeamEventsByYear(oprsCacheKey.key, oprsCacheKey.year, "");

                    var highestOprs = 0.0;

                    for (var event : events) {
                        var eventOprs = eventService.getEventOprs(event.getKey());
                        var teamOprs = eventOprs.getOprs().get(oprsCacheKey.key);
                        if (teamOprs != null && teamOprs > highestOprs) {
                            highestOprs = teamOprs;
                        }
                    }

                    return (float) highestOprs;
                });
    }
}
