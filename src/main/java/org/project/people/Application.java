package org.project.people;

import org.apache.commons.dbcp.BasicDataSource;
import org.project.people.data.dao.GenericDao;
import org.project.people.data.dao.PersonDaoImpl;
import org.project.people.data.entity.Person;
import org.project.people.data.util.EntityUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.sql.DataSource;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public DataSource getDevelopmentDataSource(){
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testDb");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setInitialSize(5);
        dataSource.setMaxActive(10);
        return dataSource;
    }

    @Bean
    GenericDao<Person> getPersonDao(DataSource dataSource) {
        return new PersonDaoImpl(dataSource, EntityUtil.getTable(Person.class));
    }
}

