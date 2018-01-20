package eventsSourceProvider;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
		Scheduler ts = (Scheduler) context.getBean("scheduler");
		ts.scheduleAtFixedRate();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Shutdown...");
				((ConfigurableApplicationContext) context).close();
			}
		});

	}

}
