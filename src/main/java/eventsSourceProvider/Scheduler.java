package eventsSourceProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.scheduling.TaskScheduler;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eventsSourceProvider.dao.EventsSourceDAO;
import eventsSourceProvider.entities.EventsSource;

public class Scheduler {

	/*
	 * TODO: 1. HTTP-send
	 */

	private TaskScheduler scheduler;
	private long period;
	private EventsSourceDAO eventsSourceDAO;
	private String httpURL;

	public Scheduler(TaskScheduler scheduler) {
		this.scheduler = scheduler;
	}

	private class EventsSourceSender implements Runnable {
		private List<EventsSource> cachedEventsSource;

		public EventsSourceSender(EventsSourceDAO eventsSourceDAO) {
			setCachedEventsSource(new ArrayList<>());
		}

		public void run() {
			List<EventsSource> eventsSourceList = getEventsSourceDAO().getAll();
			Date currentDateAndTime = new Date();
			if (eventsSourceList != null) {
				for (EventsSource eventsSource : eventsSourceList) {
					long dateDifferenceInSeconds = (eventsSource.getNextCheckTime().getTime()
							- currentDateAndTime.getTime()) / 1000;
					if (dateDifferenceInSeconds <= 3600)
						getCachedEventsSource().add(eventsSource);
				}
			}
			currentDateAndTime = new Date();
			List<EventsSource> cachedEventsSource = getCachedEventsSource();
			if (cachedEventsSource != null && cachedEventsSource.size() > 0)
				for (EventsSource eventsSource : cachedEventsSource) {
					long dateDifferenceInSeconds = (eventsSource.getNextCheckTime().getTime()
							- currentDateAndTime.getTime()) / 1000;
					if (dateDifferenceInSeconds <= 0) {
						ObjectMapper mapper = new ObjectMapper();
						try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
							mapper.writeValue(bos, eventsSource);
							CloseableHttpClient httpClient = HttpClients.createDefault();
							HttpPost httpPost = new HttpPost(getHttpURL());
							httpPost.setHeader("Content-Type", "application/json");
							httpPost.setHeader("Accept", "application/json");
							HttpEntity postBody = new ByteArrayEntity(bos.toByteArray());
							httpPost.setEntity(postBody);
							HttpResponse response = httpClient.execute(httpPost);
							/*
							 * TODO: 1. tu cakam na odpoved treba skusit urobit
							 * cakanie zopar sekund a nasledne urobit resend 2.
							 * Treba mat otvoreny port na prijimanie spatneho
							 * info o vykoonani checku source a zapis vysledku
							 * do db, preratanie nasledujuceho casu, potrebujem
							 * k tomu id source, cas kedy bol vykonany check a
							 * vysledok
							 */
						} catch (JsonGenerationException e) {
							e.printStackTrace();
						} catch (JsonMappingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
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

	public String getHttpURL() {
		return httpURL;
	}

	public void setHttpURL(String httpURL) {
		this.httpURL = httpURL;
	}

}