package io.vavr.hibernate;

import io.vavr.collection.LinearSeq;
import io.vavr.collection.List;
import io.vavr.hibernate.userstype.PersistentList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CollectionType;
import org.hibernate.annotations.Type;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.LinkedList;


@RunWith(SpringRunner.class)
public class ListUserTypeTest {

    @Autowired
    private PersonRepository people;

    @SpringBootApplication
    static class Configuration {

    }


    @Test
    public void readsPersonIntoJavaslangOptionById() {

        Person adrian = new Person("Adrian", "Bongiorno");
        Person maya = new Person("Maya", "Bongiorno");
        Person viola = new Person("Viola", "Bongiorno", List.of(adrian, maya));
        Person me = new Person("Christian", "Bongiorno", List.of(adrian, maya));

//    Iterable<Person> people = this.people.save(List.of(adrian, maya, viola, me).toJavaList());

//    Person result = this.people.findOne(me.getId());
//
//    assertThat(result).isNotNull();
//    assertThat(result).isEqualTo(me);
//    assertThat(result.getChildren()).containsExactly(adrian, maya);
    }

    @Getter
    @Entity
    @NoArgsConstructor
    public class Person {

        private @GeneratedValue
        @Id
        Long id;
        private String firstname;

        private String lastname;

        @ManyToMany
        @CollectionType(type = "io.vavr.hibernate.userstype.ListUserType2")
        private List<Person> children;


        public Person(String firstname, String lastname, List<Person> children) {
            this.firstname = firstname;
            this.lastname = lastname;
            this.children = children;
        }

        public Person(String firstname, String lastname) {
            this(firstname, lastname, List.empty());
        }
    }

    @Component
    public interface PersonRepository extends CrudRepository<Person, Long> {

    }
}
