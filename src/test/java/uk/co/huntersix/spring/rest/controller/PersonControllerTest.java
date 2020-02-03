package uk.co.huntersix.spring.rest.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.huntersix.spring.rest.dto.PersonDto;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonDataService personDataService;

    @Test
    public void shouldReturnPersonFromService() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(new Person("Mary", "Smith"));
        this.mockMvc.perform(get("/person/smith/mary"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("firstName").value("Mary"))
            .andExpect(jsonPath("lastName").value("Smith"));
    }

    @Test
    public void shouldReturn404FromServiceWhenPersonNotFound() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(null);
        this.mockMvc.perform(get("/person/nonexistinglastname/nonexistingfirstname"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnPeopleWithSpesifiedSurnameExistsOnceFromService() throws Exception {
        when(personDataService.findPeopleUsingSurname(any())).thenReturn(Lists.list(new Person("Mary", "Smith")));
        this.mockMvc.perform(get("/person/smith"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("people[0].id").exists())
                .andExpect(jsonPath("people[0].firstName").value("Mary"))
                .andExpect(jsonPath("people[0].lastName").value("Smith"));
    }

    @Test
    public void shouldReturnPeopleWithSpesifiedSurnameExistsMutlipleFromService() throws Exception {
        when(personDataService.findPeopleUsingSurname(any())).thenReturn(Lists.list(new Person("Mary", "Smith"), new Person("mesut", "smith")));
        this.mockMvc.perform(get("/person/smith"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("people[0].id").exists())
                .andExpect(jsonPath("people[0].firstName").value("Mary"))
                .andExpect(jsonPath("people[0].lastName").value("Smith"))
                .andExpect(jsonPath("people[1].id").exists())
                .andExpect(jsonPath("people[1].firstName").value("mesut"))
                .andExpect(jsonPath("people[1].lastName").value("smith"));
    }

    @Test
    public void shouldReturnNoContentFromServiceWhenPersonWithSurnameNotFound() throws Exception {
        when(personDataService.findPeopleUsingSurname(any())).thenReturn(Lists.emptyList());
        this.mockMvc.perform(get("/person/smith"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnBadRequesWhenPersonNameAndSurnameIsEmpty() throws Exception {
        PersonDto  personDto = new PersonDto("","");
        this.mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(personDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequesWhenPersonNameIsEmpty() throws Exception {
        PersonDto  personDto = new PersonDto("aaaaa","");
        this.mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(personDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequesWhenPersonSurnameIsEmpty() throws Exception {
        PersonDto  personDto = new PersonDto("","bbbbb");
        this.mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(personDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    public void shouldReturnConflictWhenPersonNameAndSurnameMatches() throws Exception {
        PersonDto  personDto = new PersonDto("Collin","Brown");
        when(personDataService.findPerson("Brown", "Collin")).thenReturn(new Person("Collin", "Brown"));

        this.mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(personDto)))
                .andDo(print())
                .andExpect(status().isConflict());
    }


    @Test
    public void shouldReturnCreatedWhenPersonNameAndSurnameValid() throws Exception {
        PersonDto  personDto = new PersonDto("mesut","gurle");
//        when(personDataService.findPerson("Brown", "Collin")).thenReturn(new Person("Collin", "Brown"));

        this.mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(personDto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }
}