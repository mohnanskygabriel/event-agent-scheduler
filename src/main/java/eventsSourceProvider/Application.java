package eventsSourceProvider;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {

	public static void main(String[] args) {
		ApplicationContext superContext = new ClassPathXmlApplicationContext("AllBeans.xml");
		Scheduler ts = (Scheduler) superContext.getBean("scheduler");
		
		ts.scheduleAtFixedRate();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Shutdown...");
				((ConfigurableApplicationContext) superContext).close();

			}
		});
	}

}
