package org.team3534.services;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.team3534.sync.EventSynchronizer;
import org.team3534.sync.TeamSynchronizer;

@ApplicationScoped
public class DataSyncService {

    @Inject
    EventSynchronizer eventSynchronizer;

    @Inject
    TeamSynchronizer teamSynchronizer;

    @Scheduled(every = "P7D")
    public void syncSeasonData() {
        var events = eventSynchronizer.syncEventsByYear(2025);
        for (var event : events) {
            eventSynchronizer.syncEventTeams(event.getKey());
            eventSynchronizer.syncEventOprs(event.getKey());
        }

        teamSynchronizer.syncTeamsByYear(2025);
    }
}
