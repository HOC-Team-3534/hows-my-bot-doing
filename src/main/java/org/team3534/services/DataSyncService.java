package org.team3534.services;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Calendar;
import org.team3534.sync.EventSynchronizer;
import org.team3534.sync.TeamSynchronizer;

@ApplicationScoped
public class DataSyncService {

    @Inject EventSynchronizer eventSynchronizer;

    @Inject TeamSynchronizer teamSynchronizer;

    @Scheduled(every = "P7D")
    public void syncSeasonData() {
        syncSeasonData(Calendar.getInstance().getWeekYear());
    }

    public void syncSeasonData(int year) {
        eventSynchronizer.syncEventsByYear(year);
        teamSynchronizer.syncTeamsByYear(year);
    }
}
