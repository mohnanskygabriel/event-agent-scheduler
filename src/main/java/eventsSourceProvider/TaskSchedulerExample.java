package eventsSourceProvider;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.TaskScheduler;

import eventsSourceProvider.dao.EventsSourceDAO;
import eventsSourceProvider.entities.EventsSource;

public class TaskSchedulerExample {

	/*
	 * TODO: 
	 * 0. Rename these classes and methods
	 * 1. make the DAO act like singleton and do not use
	 * ClassPathXmlApplicationContext outside Application.java
	 */

	private TaskScheduler taskScheduler;
	private long period;
	private EventsSourceDAO eventsSourceDAO;

	public TaskSchedulerExample(TaskScheduler taskScheduler) {
		this.taskScheduler = taskScheduler;
	}

	private class EventsSourceSender implements Runnable {

		public void run() {
			ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
			EventsSourceDAO eventsSourceDAO = (EventsSourceDAO) context.getBean("eventsSourceDAO");
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

	public void schedule() {
		taskScheduler.scheduleAtFixedRate(new EventsSourceSender(), period);
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