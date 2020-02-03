package uk.co.huntersix.spring.rest.referencedata;

import org.junit.Before;
import org.junit.Test;
import uk.co.huntersix.spring.rest.dto.PersonDto;
import uk.co.huntersix.spring.rest.model.Person;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class PersonDataServiceTest {

    PersonDataService personDataService;

    @Before
    public void setUp() throws Exception {
        personDataService = new PersonDataService();
        ArrayList<Person> people = new ArrayList<>(Arrays.asList(
                new Person("Mary", "Smith"),
                new Person("Brian", "Archer"),
                new Person("Collin", "Brown")
        ));
        setFinalStatic(PersonDataService.class.getDeclaredField("PERSON_DATA"), people);

    }

    @Test
    public void findPerson_ShouldReturnNull_WhenNonExistingNamesGiven() throws Exception {

        //when
        Person person = personDataService.findPerson("someRandomLastName", "randomFirstName");
        //then
        assertNull(person);
    }

    @Test
    public void findPerson_ShouldReturnPerson_WhenExistingNamesGiven() throws Exception {

        //when
        Person person = personDataService.findPerson("Brown", "Collin");
        //then
        assertNotNull(person);
        assertEquals("Brown", person.getLastName());
        assertEquals("Collin", person.getFirstName());
        assertNotNull(person.getId());
    }

    @Test
    public void findPerson_ShouldReturnFirstPerson_WhenMultipleExistingNamesGiven() throws Exception {
        //given
        List<Person> testData = Arrays.asList(
                new Person("Mary", "Smith"),
                new Person("Brian", "Archer"),
                new Person("Brian", "Archer"),
                new Person("Collin", "Brown")
        );
        setFinalStatic(PersonDataService.class.getDeclaredField("PERSON_DATA"), testData);

        //when
        Person person = personDataService.findPerson("Archer", "Brian");
        //then
        assertNotNull(person);
        assertEquals("Archer", person.getLastName());
        assertEquals("Brian", person.getFirstName());
        assertNotNull(person.getId());
    }

    @Test
    public void findPeopleUsingSurname_ShouldReturnEmptyList_WhenNonExistingSurnameGiven() throws Exception {
        //when
        List<Person> people = personDataService.findPeopleUsingSurname("someRandomLastName");
        //then
        assertEquals(0, people.size());
    }

    @Test
    public void findPeopleUsingSurname_ShouldReturnPersonList_WhenExistingNamesGiven() throws Exception {
        //when
        List<Person> people = personDataService.findPeopleUsingSurname("Brown");
        //then
        assertEquals(1, people.size());
        assertEquals("Brown", people.get(0).getLastName());
        assertEquals("Collin", people.get(0).getFirstName());
        assertNotNull(people.get(0).getId());
    }

    @Test
    public void findPeopleUsingSurname_ShouldReturnPersonList_WhenMultipleExistingNamesGiven() throws Exception {

        //given
        List<Person> testData = Arrays.asList(
                new Person("Mary", "Smith"),
                new Person("Terry", "Smith"),
                new Person("John", "Smith"),
                new Person("Brian", "Archer"),
                new Person("Brian", "Archer"),
                new Person("Collin", "Brown")
        );
        setFinalStatic(PersonDataService.class.getDeclaredField("PERSON_DATA"), testData);

        //when
        List<Person> people = personDataService.findPeopleUsingSurname("Smith");
        //then
        assertEquals(3, people.size());
        assertEquals("Smith", people.get(0).getLastName());
        assertEquals("Mary", people.get(0).getFirstName());
        assertNotNull(people.get(0).getId());

        assertEquals("Smith", people.get(1).getLastName());
        assertEquals("Terry", people.get(1).getFirstName());
        assertNotNull(people.get(1).getId());

        assertEquals("Smith", people.get(2).getLastName());
        assertEquals("John", people.get(2).getFirstName());
        assertNotNull(people.get(2).getId());
    }

    @Test
    public void addPerson_ShouldAddPerson() throws Exception {
        //given
        PersonDto personDto = new PersonDto("first", "last");

        //when
        personDataService.addPerson(personDto);
        //then
        assertEquals(4, personDataService.PERSON_DATA.size());
    }

    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }
}