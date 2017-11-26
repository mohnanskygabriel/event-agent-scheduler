package eventsSourceProvider;

import java.util.List;

import org.springframework.scheduling.TaskScheduler;

import eventsSourceProvider.dao.EventsSourceDAO;
import eventsSourceProvider.entities.EventsSource;

public class Scheduler {

	/*
	 * TODO: 0. Rename these classes and methods 1. make the DAO act like singleton
	 * and do not use ClassPathXmlApplicationContext outside Application.java
	 */

	private TaskScheduler scheduler;
	private long period;
	private EventsSourceDAO eventsSourceDAO;

	public Scheduler(TaskScheduler scheduler) {
		this.scheduler = scheduler;
	}

	private class EventsSourceSender implements Runnable {
		private EventsSourceDAO eventsSourceDAO;

		public EventsSourceSender(EventsSourceDAO eventsSourceDAO) {
			this.eventsSourceDAO = eventsSourceDAO;
		}

		public void run() {
			System.out.println(eventsSourceDAO);
			List<EventsSource> eventsSourceList = eventsSourceDAO.getAll();
			List<String> eventsSourceURLList = eventsSourceDAO.getAllsourceURLs();
			if (eventsSourceList != null) {
				for (String url : eventsSourceURLList) {
					System.out.println(url);
				}
			}
		}
	}

	public void scheduleAtFixedRate() {
		scheduler.scheduleAtFixedRate(new EventsSourceSender(this.getEventsSourceDAO()), period);
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public EventsSourceDAO getEventsSourceDAO() {
		return eventsSourceDAO;
	}

	public void setEventsSourceDAO(EventsSourceDAO eventsSourceDAO) {
		this.eventsSourceDAO = eventsSourceDAO;
	}

}