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
	private List<EventsSource> calledForUpdate;

	public Scheduler(TaskScheduler scheduler) {
		this.scheduler = scheduler;
	}

	private class EventsSourceSender implements Runnable {

		public EventsSourceSender(EventsSourceDAO eventsSourceDAO) {
			setCalledForUpdate(new ArrayList<>());
		}

		public void run() {
			List<EventsSource> eventsSourceList = getEventsSourceDAO().getAllEventsSources();
			if (eventsSourceList != null) {
				for (EventsSource eventsSource : eventsSourceList) {
					/*
					 * if the source was not called for update before and the
					 * calculated seconds left to update are less then the check
					 * period of sources call the update
					 */
					if (!getCalledForUpdate().contains(eventsSource)
							&& ((eventsSource.getNextCheckTime().getTime() - new Date().getTime())
									/ 1000) <= getDatabaseCheckPeriod()) {
						/*
						 * TODO:updateSource(eventsSource)
						 */						
						getCalledForUpdate().add(eventsSource);
					}
				}
			}
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

	public List<EventsSource> getCalledForUpdate() {
		return calledForUpdate;
	}

	public void setCalledForUpdate(List<EventsSource> calledForUpdate) {
		this.calledForUpdate = calledForUpdate;
	}

}