package org.project.people.controller;

import org.project.people.data.dao.GenericDao;
import org.project.people.data.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Павел on 06.01.2016.
 */
@RestController
public class PeopleController {

    @Autowired
    private GenericDao<Person> personDao;

    @RequestMapping("/people")
    List<Person> getAllPeople() {
        List<Person> persons = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            persons.add(new Person(i, "PersonName-" + i, "PersonMidname-" + i, "PersonLastname-" + i,
                (i % 2 == 0), new Date(), (i %2 == 0), (i %2 == 0), i % 4, "Comment-" + i));
        }
        return persons;
    }

    @RequestMapping("/people/db")
    List<Person> getAllPeopleDb() {
        return personDao.list();
    }

    @RequestMapping("/people/{id}")
    Person getAllPeopleDb(@PathVariable int id) {
        return personDao.get(id);
    }

}
