package eventsSourceProvider.dao;

import java.util.List;

import eventsSourceProvider.entities.EventsSource;

public interface EventsSourceDAO {

	public void save(EventsSource eventsSource);

	public List<EventsSource> getAll();
}
