package eventsSourceProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.scheduling.TaskScheduler;
import eventagent.persistence.entities.*;
import eventagent.persistence.dao.*;

public class Scheduler {

	private TaskScheduler scheduler;
	private long databaseCheckPeriod;
	private EventsSourceDAO eventsSourceDAO;
	private long eventsSourceAliveTimeInCacheInSeconds;

	public Scheduler(TaskScheduler scheduler) {
		this.scheduler = scheduler;
	}

	private class EventsSourceSender implements Runnable {
		private List<EventsSource> cachedEventsSource;

		public EventsSourceSender(EventsSourceDAO eventsSourceDAO) {
			setCachedEventsSource(new ArrayList<>());
		}

		public void run() {
			List<EventsSource> eventsSourceList = getEventsSourceDAO().getAllEventsSources();
			Date currentDateAndTime = new Date();
			if (eventsSourceList != null) {
				for (EventsSource eventsSource : eventsSourceList) {

					// if eventsSource is already in cache it'll be skipped
					if (getCachedEventsSource().contains(eventsSource)) {
						continue;
					}

					long dateDifferenceInSeconds = (eventsSource.getNextCheckTime().getTime()
							- currentDateAndTime.getTime()) / 1000;
					/*
					 * if events from source should be updated within a
					 * specified time by eventsSourceAliveTimeInCacheInSeconds
					 * add it into cache
					 */
					if (dateDifferenceInSeconds <= getEventsSourceAliveTimeInCacheInSeconds()) {
						getCachedEventsSource().add(eventsSource);
					}
				}
			} else {
				// Send e-mail about eventsSource db down if not sent already
			}
			currentDateAndTime = new Date();
			List<EventsSource> cachedEventsSource = getCachedEventsSource();
			if (cachedEventsSource != null && cachedEventsSource.size() > 0)
				for (EventsSource eventsSource : cachedEventsSource) {
					long dateDifferenceInSeconds = (eventsSource.getNextCheckTime().getTime()
							- currentDateAndTime.getTime()) / 1000;
					// if the events from source had to be updated
					if (dateDifferenceInSeconds <= 0) {
						/*
						 * TODO: tu volam metodu od Patrika Rojeka
						 * ROJEKDAO.updateSource(?eventsSource?)
						 */
						System.out.println("Urob update pre event: " + eventsSource);
					}
				}

		}

		public List<EventsSource> getCachedEventsSource() {
			return cachedEventsSource;
		}

		public void setCachedEventsSource(List<EventsSource> cachedEventsSource) {
			this.cachedEventsSource = cachedEventsSource;
		}
	}

	public void scheduleAtFixedRate() {
		scheduler.scheduleAtFixedRate(new EventsSourceSender(this.getEventsSourceDAO()), databaseCheckPeriod);
	}

	public long getDatabaseCheckPeriod() {
		return databaseCheckPeriod;
	}

	public void setDatabaseCheckPeriod(long period) {
		this.databaseCheckPeriod = period;
	}

	public EventsSourceDAO getEventsSourceDAO() {
		return eventsSourceDAO;
	}

	public void setEventsSourceDAO(EventsSourceDAO eventsSourceDAO) {
		this.eventsSourceDAO = eventsSourceDAO;
	}

	public long getEventsSourceAliveTimeInCacheInSeconds() {
		return eventsSourceAliveTimeInCacheInSeconds;
	}

	public void setEventsSourceAliveTimeInCacheInSeconds(long eventsSourceAliveTimeInCacheInSeconds) {
		this.eventsSourceAliveTimeInCacheInSeconds = eventsSourceAliveTimeInCacheInSeconds;
	}

}