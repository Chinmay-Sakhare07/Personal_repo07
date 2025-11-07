/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Business.Person;

import java.util.ArrayList;

/**
 *
 * @author kal bugrara
 */
public class PersonDirectory {

    ArrayList<Person> personlist;

    public PersonDirectory() {

        personlist = new ArrayList();

    }

    public Person newPerson(String id) {

        Person p = new Person(id);
        personlist.add(p);
        return p;
    }

    public Person newPerson(String id, String firstName, String lastName, String email, String phone) {
        Person p = new Person(id, firstName, lastName, email, phone);
        personlist.add(p);
        return p;
    }

    public Person findPerson(String id) {

        for (Person p : personlist) {

            if (p.isMatch(id)) {
                return p;
            }
        }
        return null;
    }

    public Person findPersonByEmail(String email) {
        for (Person p : personlist) {
            if (p.getEmail() != null && p.getEmail().equalsIgnoreCase(email)) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<Person> getPersonList() {
        return personlist;
    }

    public boolean removePerson(String id) {
        Person p = findPerson(id);
        if (p != null) {
            return personlist.remove(p);
        }
        return false;
    }

}
