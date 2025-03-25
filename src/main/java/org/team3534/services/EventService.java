package org.team3534.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tba.api.EventApi;
import com.tba.model.Event;
import com.tba.model.EventOPRs;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import java.util.concurrent.TimeUnit;

@Produces
@ApplicationScoped
public class EventService {
    private Cache<String, Event> eventCache;
    private Cache<String, EventOPRs> eventOprsCache;

    @Inject EventApi eventApi;

    @PostConstruct
    void init() {
        eventCache =
                Caffeine.newBuilder()
                        .expireAfterWrite(10, TimeUnit.MINUTES)
                        .maximumSize(100)
                        .build();

        eventOprsCache =
                Caffeine.newBuilder()
                        .expireAfterWrite(10, TimeUnit.MINUTES)
                        .maximumSize(100)
                        .build();
    }

    public Event getEvent(String key) {
        return eventCache.get(key, this::fetchEventFromApi);
    }

    private Event fetchEventFromApi(String key) {
        return eventApi.getEvent(key, "");
    }

    public void putEventInCache(Event event) {
        eventCache.put(event.getKey(), event);
    }

    public EventOPRs getEventOprs(String key) {
        return eventOprsCache.get(key, this::fetchEventOPRsFromApi);
    }

    private EventOPRs fetchEventOPRsFromApi(String key) {
        return eventApi.getEventOPRs(key, "");
    }
}
