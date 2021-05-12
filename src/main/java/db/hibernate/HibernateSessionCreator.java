package db.hibernate;


import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSessionCreator {
    private static SessionFactory sessionFactory = null;

    public static SessionFactory getSessionFactory(final Class tableName) {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory(tableName);
        }
        return sessionFactory;
    }

    private static SessionFactory buildSessionFactory(final Class tableName) {
        return new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(tableName) //Из models
                .buildSessionFactory();
    }
}
