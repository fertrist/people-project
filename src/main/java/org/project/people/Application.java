package org.project.people;

import org.apache.commons.dbcp.BasicDataSource;
import org.project.people.data.dao.CategoryDaoImpl;
import org.project.people.data.dao.DataBaseDao;
import org.project.people.data.dao.GenericDao;
import org.project.people.data.dao.PersonDaoImpl;
import org.project.people.data.entity.Category;
import org.project.people.data.entity.Person;
import org.project.people.service.PersonService;
import org.project.people.util.EntityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import javax.sql.DataSource;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
@EnableWebSecurity
public class Application extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("getDevelopmentAdminDataSource")
    private DataSource adminDataSource;

    private static final String usersTable = "users";
    private static final String adminDbName = "admindb";
    private static final String peopleDBName = "admindb";
    public static final String production = "production";
    public static final String development = "development";
    public static final String profile = development;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.setAdditionalProfiles(development);
        springApplication.run(args);
        //code that allows to initialize static variables
        /*import org.springframework.context.ConfigurableApplicationContext context = springApplication.run(args);
        DataBaseDao dataBaseDao = (DataBaseDao) context.getBean("getDataBaseDao");
        GenericDao<Category> categoryDao = (GenericDao<Category>) context.getBean("getCategoryDao");
        GenericDao<Person> personDao = (GenericDao<Person>) context.getBean("getPersonDao");*/
    }

    /**
     * Development database for regular purposes like storing data.
     */
    @Profile(development)
    @Bean
    @Primary
    @ConfigurationProperties(prefix="datasource.primary")
    public DataSource getDevelopmentDataSource(){
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .setName(peopleDBName)
            .addScript("resources\\sql\\h2\\schema.sql")
            .addScript("resources\\sql\\h2\\insert-values.sql").setScriptEncoding("UTF-8")
            .build();
/*
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testDb");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setInitialSize(5);
        dataSource.setMaxActive(10);
        return dataSource;
*/
    }

    /**
     * Development database for administrative purposes like storing passwords, etc.
     */
    @Profile(development)
    @Bean
    @ConfigurationProperties(prefix="datasource.secondary")
    public DataSource getDevelopmentAdminDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .setName(adminDbName)
            .addScript("resources\\sql\\h2\\admin-script.sql")
            .build();
    }

    /**
     * Production db for storing people data.
     */
    @Profile(production)
    @Bean DataSource getMySqlDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost/" + peopleDBName); //?user=fertrist&password=fertrist
        dataSource.setUsername("fertrist");
        dataSource.setPassword("fertrist");
        dataSource.setInitialSize(3);
        return dataSource;
    }

    @Bean GenericDao<Person> getPersonDao(DataSource dataSource) {
        return new PersonDaoImpl(dataSource, EntityUtil.getTable(Person.class));
    }

    @Bean GenericDao<Category> getCategoryDao(DataSource dataSource) {
        return new CategoryDaoImpl(dataSource, EntityUtil.getTable(Category.class));
    }

    @Bean public DataBaseDao getDataBaseDao(DataSource dataSource) {
        return new DataBaseDao(dataSource);
    }

    @Bean public PersonService getPersonService() {
        return new PersonService();
    }

    /**
     * Required to enable security.
     */
    public class SecurityWebInitializer extends AbstractSecurityWebApplicationInitializer {}

    /**
     * Override to configure user store.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        boolean inMemoryUserStore = false;
        if (inMemoryUserStore) {
            //Required for support in-memory user store.
            auth.inMemoryAuthentication()
                .withUser("user").password("password").roles("USER").and()
                .withUser("admin").password("password").roles("USER", "ADMIN");
        } else {
            //Required for support in-database user store.
            auth.
                jdbcAuthentication()
                .dataSource(adminDataSource)
                .usersByUsernameQuery("select username, password, enabled from " + usersTable + " where username=?")
                .authoritiesByUsernameQuery("select username, role from " + usersTable + " where username=?");

                //passwords should be written to database with the same encoding. Now they are loaded by init script
                //.passwordEncoder(new StandardPasswordEncoder("53cr3t"));
        }
    }
}

