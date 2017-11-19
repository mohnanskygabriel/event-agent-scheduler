package eventsSourceProvider;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eventsSourceProvider.dao.EventsSourceDAO;
import eventsSourceProvider.entities.EventsSource;

public class Application {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
		EventsSourceDAO eventsSourceDAO = (EventsSourceDAO) context.getBean("eventsSourceDAO");
		List<EventsSource> eventsSourceList = eventsSourceDAO.getAll();
		for (EventsSource eventsSource : eventsSourceList) {
			System.out.println(eventsSource.getSourceType());
		}

	}

}
