package ch.bbcag.ebai.controllers;


import ch.bbcag.ebai.models.Advert;
import ch.bbcag.ebai.repositories.AdvertRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.server.ResponseStatusException;


import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static ch.bbcag.ebai.utils.TestDataUtils.getTestAdvert;
import static ch.bbcag.ebai.utils.TestDataUtils.getTestAdverts;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdvertController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdvertControllerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    private String postValidAdvertJson = "{\"id\":\"0\", \"name\":\"test\", \"user\":{\"id\":\"1\"}}";
    private String postInvalidAdvertJson = "{\"id\":\"0\", \"user\":{\"id\":\"1\"}}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdvertController advertController;

    @MockBean
    private AdvertRepository advertRepository;

    @Test //pass
    public void checkGet_whenNoParam_thenAllAdvertsAreReturned() throws Exception {

        List<Advert> adverts = getTestAdverts();

        doReturn(adverts).when(advertController).findByName(null);
        String allAdverts = objectMapper.writeValueAsString(adverts);
        mockMvc.perform(get("/adverts")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(allAdverts));
    }


    @Test //pass
    public void checkGet_whenExistingName_thenAdvertIsReturned() throws Exception {
        String advertName = "Advert 1";
        doReturn(getTestAdverts()).when(advertController).findByName(advertName);

        mockMvc.perform(get("/adverts")
                        .contentType("application/json")
                        .queryParam("name", advertName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].name").value(advertName));
    }

    @Test //pass
    public void checkGet_whenNotExistingName_thenIsOkAndEmpty() throws Exception {
        String advertName = "NotExistingAdvert";
        doReturn(new ArrayList<Advert>()).when(advertController).findByName(advertName);
        mockMvc.perform(get("/adverts")
                        .contentType("application/json")
                        .queryParam("name", advertName))
                .andExpect(status().isOk()).andExpect(content().string("[]"));
    }

    @Test //pass
    public void checkGetById_whenValidId_thenAdvertIsReturned() throws Exception {

        mockMvc.perform(get("/adverts/" + 1)
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test //error
    public void checkGetByID_whenInvalidId_thenIsNotFound() throws Exception {
        doReturn(Optional.empty()).when(advertController).findById(0);
        mockMvc.perform(get("/adverts/" + 0)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test //pass
    public void checkPut_whenValidAdvert_thenIsOk() throws Exception {
        mockMvc.perform(put("/adverts")
                        .contentType("application/json")
                        .content("{\"id\":\"1\", \"name\":\"Test\", \"bids\": [{\"id\":\"1\", \"value\":\"5\"}]}"))
                .andExpect(status().isOk());
    }

    @Test //pass
    public void checkPut_whenInvalidAdvert_thenIsBadRequest() throws Exception {
        doThrow(ConstraintViolationException.class).when(advertController).update(new Advert());

        mockMvc.perform(put("/adverts")
                        .contentType("application/json")
                        .content("{\"id\":\"1\", \"bids\": [{\"id\":\"1\", \"value\":\"5\"}]}"))
                .andExpect(status().isBadRequest());
    }

    @Test //pass
    public void checkPost_whenNewValidAdvertInserted_thenIsCreated() throws Exception {
        mockMvc.perform(post("/adverts")
                        .contentType("application/json")
                        .content(postValidAdvertJson)
                        .accept("application/json"))
                .andExpect(status().isCreated());
    }

    @Test //pass
    public void checkPost_whenNewInvalidAdvertInserted_thenIsBadRequest() throws Exception {
        mockMvc.perform(post("/adverts")
                        .contentType("application/json")
                        .content(postInvalidAdvertJson)
                        .accept("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test //pass
    public void checkDelete_whenValidId_thenIsOk() throws Exception {
        mockMvc.perform(delete("/adverts/1")
                        .contentType("application/json"))
                .andExpect(status().isOk());

        Mockito.verify(advertController).deleteById(eq(1));
    }

    @Test //error (EmptyResultDataAccessException)
    public void checkDelete_whenInvalidId_thenIsNotFound() throws Exception {
        doThrow(EmptyResultDataAccessException.class).when(advertController).deleteById(0);
        mockMvc.perform(delete("/adverts/0")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        Mockito.verify(advertController).deleteById(0);
    }


}
