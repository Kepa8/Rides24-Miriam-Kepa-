package testOperations;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

import configuration.ConfigXML;
import domain.Booking;
import domain.Driver;
import domain.Ride;
import domain.Traveler;
import domain.User;


public class TestDataAccess {
	protected  EntityManager  db;
	protected  EntityManagerFactory emf;

	ConfigXML  c=ConfigXML.getInstance();
 

	public TestDataAccess()  {
		 
		System.out.println("TestDataAccess created");
		//open();
	}

	public void open(){
		String fileName=c.getDbFilename();
		if (c.isDatabaseLocal()) {
			  emf = Persistence.createEntityManagerFactory("objectdb:"+fileName);
			  db = emf.createEntityManager();
		} else {
			Map<String, String> properties = new HashMap<String, String>();
			  properties.put("javax.persistence.jdbc.user", c.getUser());
			  properties.put("javax.persistence.jdbc.password", c.getPassword());

			  emf = Persistence.createEntityManagerFactory("objectdb://"+c.getDatabaseNode()+":"+c.getDatabasePort()+"/"+fileName, properties);

			  db = emf.createEntityManager();
    	   }
		System.out.println("TestDataAccess opened");
	}
	
	public void close(){
		db.close();
		System.out.println("TestDataAccess closed");
	}

	public boolean removeDriver(String name) {
		System.out.println(">> TestDataAccess: removeDriver");
		Driver d = db.find(Driver.class, name);
		if (d!=null) {
			db.getTransaction().begin();
			db.remove(d);
			db.getTransaction().commit();
			return true;
		} else 
		return false;
    }
	
	public Driver createDriver(String name, String pass) {
		System.out.println(">> TestDataAccess: addDriver");
		Driver driver=null;
			db.getTransaction().begin();
			try {
			    driver=new Driver(name,pass);
				db.persist(driver);
				db.getTransaction().commit();
			}
			catch (Exception e){
				e.printStackTrace();
			}
			return driver;
    }
	
	public boolean existDriver(String email) {
		 return  db.find(Driver.class, email)!=null;	 
	}
		
		public Driver addDriverWithRide(String name, String from, String to,  Date date, int nPlaces, float price) {
			System.out.println(">> TestDataAccess: addDriverWithRide");
				Driver driver=null;
				db.getTransaction().begin();
				try {
					 driver = db.find(Driver.class, name);
					if (driver==null) {
						System.out.println("Null da");
						driver=new Driver(name,null);
				    	db.persist(driver);
					}
				    driver.addRide(from, to, date, nPlaces, price);
					db.getTransaction().commit();
					System.out.println("Driver created "+driver);
					
					return driver;
					
				}
				catch (Exception e){
					e.printStackTrace();
				}
				return null;
	    }
		
		
		public boolean existRide(String name, String from, String to, Date date) {
			System.out.println(">> TestDataAccess: existRide");
			Driver d = db.find(Driver.class, name);
			if (d!=null) {
				return d.doesRideExists(from, to, date);
			} else 
			return false;
		}
		public Ride removeRide(String name, String from, String to, Date date ) {
			System.out.println(">> TestDataAccess: removeRide");
			Driver d = db.find(Driver.class, name);
			if (d!=null) {
				db.getTransaction().begin();
				Ride r= d.removeRide(from, to, date);
				db.getTransaction().commit();
				System.out.println("created rides" +d.getCreatedRides());
				return r;

			} else 
			return null; 
		}
		
		public User createUser(String username, String password, String mota) {
		    db.getTransaction().begin();
		    User user = new User(username, password, mota);
		    db.persist(user);
		    db.getTransaction().commit();
		    return user;
		}

		public User findUser(String username) {
		    return db.find(User.class, username);
		}
		
		public EntityManager getEntityManager() {
	        return db;
	    }
		public void setEntityManager(EntityManager entityManager) {
		    this.db = entityManager;
		}
		public void updateUserMoney(String username, double money) {
	        db.getTransaction().begin();
	        User user = findUser(username);
	        if (user != null) {
	            user.setMoney(money);
	            db.merge(user);
	        }
	        db.getTransaction().commit();
	    }
		
		public void removeAllUsers() {
	        db.getTransaction().begin();
	        Query query = db.createQuery("DELETE FROM User");
	        query.executeUpdate();
	        db.getTransaction().commit();
	    }
		public void saveUser(User user) {
	        db.getTransaction().begin();
	        db.persist(user);
	        db.getTransaction().commit();
	    }
		
		public void removeAll() {
		    System.out.println(">> TestDataAccess: removeAll");

		    if (db == null || !db.isOpen()) {
		        open();
		    }

		    db.getTransaction().begin();
		    db.createQuery("DELETE FROM Booking").executeUpdate();
		    db.createQuery("DELETE FROM Ride").executeUpdate();
		    db.createQuery("DELETE FROM Driver").executeUpdate();
		    db.createQuery("DELETE FROM Traveler").executeUpdate();
		    db.createQuery("DELETE FROM User").executeUpdate();
		    db.createQuery("DELETE FROM Car").executeUpdate();
		    db.createQuery("DELETE FROM Movement").executeUpdate();
		    db.createQuery("DELETE FROM Discount").executeUpdate();
		    db.getTransaction().commit();
		    
		    long count = (long) db.createQuery("SELECT COUNT(u) FROM User u").getSingleResult();
		    System.out.println("Number of users after deletion: " + count);
		}
		
		public Ride createRide(Ride ride) {
		    db.getTransaction().begin();
		    db.persist(ride);
		    db.getTransaction().commit();
		    return ride;
		}

		public Booking findBooking(Ride ride, Traveler traveler) {
		    
		        return db.createQuery("SELECT b FROM Booking b WHERE b.ride = :ride AND b.traveler = :traveler", Booking.class)
		                 .setParameter("ride", ride)
		                 .setParameter("traveler", traveler)
		                 .getSingleResult();
		    
		}
		
		public Ride getRide(String username, String origin, String destination, Date date) {
		    return (Ride) db.createQuery("SELECT r FROM Ride r WHERE r.driver.username = :username AND r.origin = :origin AND r.destination = :destination AND r.date = :date")
		            .setParameter("username", username)
		            .setParameter("origin", origin)
		            .setParameter("destination", destination)
		            .setParameter("date", date)
		            .getSingleResult();
		}
		
}