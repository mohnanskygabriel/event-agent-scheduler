package eventsSourceProvider.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import eventsSourceProvider.entities.EventsSource;

public class MySQLEventsSourceDAO implements EventsSourceDAO {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void save(EventsSource eventsSource) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.persist(eventsSource);
		tx.commit();
		session.close();
	}

	@SuppressWarnings("unchecked")
	public List<EventsSource> getAll() {
		Session session = sessionFactory.openSession();
		List<EventsSource> eventsSourceList = session.createQuery("from events_source").list();
		return eventsSourceList;
	}

}