import db.hibernate.HibernateSessionCreator;
import db.hibernate.models.Animal;
import db.hibernate.models.Places;
import db.hibernate.models.Workman;
import db.hibernate.models.Zoo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.fintech.qa.homework.utils.BeforeUtils;

import javax.persistence.PersistenceException;
import java.math.BigInteger;

public class TestHibernate {
    @BeforeAll
    public static void init() {
        BeforeUtils.createData();
    }

    // В таблице public.animal ровно 10 записей
    @Test
    public void testTenAnimal() {
        int count = 10;
        SessionFactory sessionFactory = HibernateSessionCreator.getSessionFactory(Animal.class);
        Session session = sessionFactory.openSession();
        int countHibernate = ((BigInteger) session.createNativeQuery("SELECT count(*) FROM animal")
                .uniqueResult()).intValue();
        Assertions.assertEquals(count, countHibernate);
        session.close();
    }

    // В таблицу public.animal нельзя добавить строку с индексом от 1 до 10 включительно
    @Test
    public void testAdd() {
        SessionFactory sessionFactory = HibernateSessionCreator.getSessionFactory(Animal.class);
        Session session = sessionFactory.openSession();
        int id = 1;

        try {
            Transaction transaction = session.beginTransaction();
            Animal animal = new Animal();
            animal.setId(id);
            session.save(animal);
            Animal an = session.createNativeQuery("Select * from animal where id = " + id, Animal.class).list().get(0);
            System.out.println(an.getName());
            transaction.commit();
            Assertions.assertNull(an, "Ошибка. Запись добавлена"); //Тест упадет, если запись добавится
        } catch (PersistenceException e) {
            System.out.println("Нельзя добавить запись с id = " + id + "\n" + e.getMessage());
        }
        session.close();
    }

    // В таблицу public.workman нельзя добавить строку с name = null
    @Test
    public void testAddNullName() {
        SessionFactory sessionFactory = HibernateSessionCreator.getSessionFactory(Workman.class);
        Session session = sessionFactory.openSession();
        int countHibernate = ((BigInteger) session.createNativeQuery("SELECT count(*) FROM workman")
                .uniqueResult()).intValue() + 1;
        try {
            Transaction transaction = session.beginTransaction();
            Workman workman = new Workman();
            workman.setId(countHibernate);
            workman.setAge(15);
            workman.setPosition(1);
            workman.setName(null);
            session.save(workman);
            Workman workm = session.createNativeQuery("Select * from workman where id = "
                    + countHibernate, Workman.class).list().get(0);
            Assertions.assertNull(workm, "Ошибка. Запись добавлена"); //Тест упадет, если запись добавится
            transaction.commit();

        } catch (PersistenceException e) {
            System.out.println("Значение NULL не разрешено для поля name \n" + e.getMessage());
        }
        session.close();
    }

    //Если в таблицу public.places добавить еще одну строку, то в ней будет 6 строк
    @Test
    public void testSixCount() {
        SessionFactory sessionFactory = HibernateSessionCreator.getSessionFactory(Places.class);
        Session session = sessionFactory.openSession();
        try {
            Transaction transaction = session.beginTransaction();
            Places places = new Places();
            places.setId(6);
            places.setRow(2);
            places.setPlaceNum(156);
            places.setName("Загон 6");
            session.save(places);
            transaction.commit();
            int count = ((BigInteger) session.createNativeQuery("Select COUNT(*) from places")
                    .uniqueResult()).intValue();
            Assertions.assertEquals(6, count);
        } catch (PersistenceException e) {
            System.out.println("Ошибка! Запись не добавлена \n" + e.getMessage());
        }
        session.close();
    }

    //В таблице public.zoo всего три записи с name 'Центральный', 'Северный', 'Западный'
    @Test
    public void zooName() {
        Session session = HibernateSessionCreator.getSessionFactory(Zoo.class).openSession();
        int zooCount = 3;
        int count = ((BigInteger) session.createNativeQuery("select count(*) from zoo where \"name\""
                + " in ('Центральный', 'Западный', 'Северный')").uniqueResult()).intValue();
        session.close();
        Assertions.assertEquals(zooCount, count);
    }
}
