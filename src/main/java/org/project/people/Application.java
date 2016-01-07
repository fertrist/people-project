package org.project.people;

import org.apache.commons.dbcp.BasicDataSource;
import org.project.people.data.dao.CategoryDaoImpl;
import org.project.people.data.dao.DataBaseDao;
import org.project.people.data.dao.GenericDao;
import org.project.people.data.dao.PersonDaoImpl;
import org.project.people.data.entity.Category;
import org.project.people.data.entity.Person;
import org.project.people.util.EntityUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.sql.DataSource;

import static org.project.people.util.TestUtil.getDate;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
public class Application {

    static Person[] persons = new Person[]{
        new Person(1, "Коленный-1", "Сидор", "Сидорович", true,
            getDate("1960-09-15"), true, true, 1, null),
        new Person(2, "Коленный-2", "Сидор", "Сидорович", true,
            getDate("1955-10-15"), true, true, 1, "Коленный"),
        new Person(3, "Коленный-3", "Сидор", "Сидорович", true,
            getDate("1965-11-15"), true, true, 1, null),
        new Person(4, "Коленный-4", "Сидор", "Сидорович", true,
            getDate("1970-03-15"), true, true, 1, null),
        new Person(5, "Имя-1", "Фамилия-1", "Отчество-1", true,
            getDate("1986-12-03"), true, true, 2, "Такой себе типок"),
        new Person(6, "Имя-2", "Фамилия-2", "Отчество-2", true,
            getDate("1988-08-05"), false, true, 3, null),
        new Person(7, "Имя-3", "Фамилия-3", "Отчество-3", false,
            getDate("1985-04-07"), false, false, 4, null),
        new Person(8, "Имя-4", "Фамилия-4", "Отчество-4", false,
            getDate("1990-03-01"), true, true, 2, null),
        new Person(9, "Имя-5", "Фамилия-5", "Отчество-5", false,
            getDate("1990-03-01"), true, false, 3, null),
        new Person(10, "Имя-6", "Фамилия-6", "Отчество-6", true,
            getDate("1990-03-01"), true, true, 4, null),
    };

    static String[] categories = new String[]{"Белый", "Зелёный", "Гости", "Еврейский список"};

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        DataBaseDao dataBaseDao = (DataBaseDao) context.getBean("getDataBaseDao");
        GenericDao<Category> categoryDao = (GenericDao<Category>) context.getBean("getCategoryDao");
        GenericDao<Person> personDao = (GenericDao<Person>) context.getBean("getPersonDao");
//        try {
//            dataBaseDao.createTables();
//            for (String category : categories) {
//                System.out.println(categoryDao.save(new Category(category)) == 1);
//            }
//            for (Person person : persons) {
//                System.out.println(personDao.save(person) == 1);
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//            dataBaseDao.dropTables(EntityUtil.getTableNames());
//        } finally {
//        }
    }

    private static void initTables(DataBaseDao dataBaseDao) {
        dataBaseDao.createTables();
        //xfillTables();
    }

    private static void dropTables(DataBaseDao dataBaseDao) {
        dataBaseDao.dropTables(EntityUtil.getTableNames());
    }

//    @Bean
//    public DataSource getDevelopmentDataSource(){
//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setDriverClassName("org.h2.Driver");
//        dataSource.setUrl("jdbc:h2:mem:testDb");
//        dataSource.setUsername("sa");
//        dataSource.setPassword("");
//        dataSource.setInitialSize(5);
//        dataSource.setMaxActive(10);
//        return dataSource;
//    }

//    @Bean DataSource getOracleDataSource() {
//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
//        dataSource.setUrl("jdbc:oracle:thin:@localhost:1521");
//        dataSource.setUsername("sa");
//        return dataSource;
//    }

    @Bean DataSource getMySqlDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost/people"); //?user=fertrist&password=fertrist
        dataSource.setUsername("fertrist");
        dataSource.setPassword("fertrist");
        dataSource.setInitialSize(3);
        return dataSource;
    }

    @Bean
    GenericDao<Person> getPersonDao(DataSource dataSource) {
        return new PersonDaoImpl(dataSource, EntityUtil.getTable(Person.class));
    }

    @Bean
    GenericDao<Category> getCategoryDao(DataSource dataSource) {
        return new CategoryDaoImpl(dataSource, EntityUtil.getTable(Category.class));
    }

    @Bean
    public DataBaseDao getDataBaseDao(DataSource dataSource) {
        return new DataBaseDao(dataSource);
    }

}

