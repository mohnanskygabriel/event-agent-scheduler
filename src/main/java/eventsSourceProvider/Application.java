package eventsSourceProvider;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
		TaskSchedulerExample ts = (TaskSchedulerExample) context.getBean("taskSchedulerExample");
		ts.schedule();

	}

}
