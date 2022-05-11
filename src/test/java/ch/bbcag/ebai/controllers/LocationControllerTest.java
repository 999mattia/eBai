package ch.bbcag.ebai.controllers;

import ch.bbcag.ebai.models.Advert;
import ch.bbcag.ebai.models.Location;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static ch.bbcag.ebai.utils.TestDataUtils.getTestAdverts;
import static ch.bbcag.ebai.utils.TestDataUtils.getTestLocations;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LocationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LocationControllerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    private String postValidLocationJson = "{\"id\":\"0\", \"plz\":\"1000\", \"users\":[], \"name\":\"test\"}";

    private String postInvalidLocationJson = "{\"id\":\"0\" \"plz\":\"1000\", \"users\":[], \"name\":\"test\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationController locationController;


    @Test //pass
    public void checkGet_whenNoParam_thenAllLocationsAreReturned() throws Exception {

        List<Location> locations = getTestLocations();

        doReturn(locations).when(locationController).findByNameOrPlz(null, null);
        String allLocations = objectMapper.writeValueAsString(locations);
        mockMvc.perform(get("/locations")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(allLocations));
    }

    @Test //pass
    public void checkGet_whenExistingName_thenLocationIsReturned() throws Exception {
        String locationName = "Location 1";
        doReturn(getTestAdverts()).when(locationController).findByNameOrPlz(locationName, null);

        mockMvc.perform(get("/locations")
                        .contentType("application/json")
                        .queryParam("name", locationName))
                .andExpect(status().isOk());
    }

    @Test //pass
    public void checkGet_whenExistingPlz_thenLocationIsReturned() throws Exception {
        Integer plz = 1;
        doReturn(getTestAdverts()).when(locationController).findByNameOrPlz(null, 1);

        mockMvc.perform(get("/locations")
                        .contentType("application/json")
                        .queryParam("plz", String.valueOf(plz)))
                .andExpect(status().isOk());
    }

    @Test //pass
    public void checkGet_whenExistingNameAndPlz_thenLocationIsReturned() throws Exception {
        String locationName = "Location 1";
        Integer plz = 1;
        doReturn(getTestAdverts()).when(locationController).findByNameOrPlz(locationName, plz);

        mockMvc.perform(get("/locations")
                        .contentType("application/json")
                        .queryParam("name", String.valueOf(locationName))
                        .queryParam("plz", String.valueOf(plz)))
                .andExpect(status().isOk());
    }

    @Test //pass
    public void checkGet_whenNonExistingName_thenIsOkAndEmpty() throws Exception {
        String locationName = "NotExistingLocation";
        doReturn(new ArrayList<Advert>()).when(locationController).findByNameOrPlz(locationName, null);
        mockMvc.perform(get("/locations")
                        .contentType("application/json")
                        .queryParam("name", locationName))
                .andExpect(status().isOk()).andExpect(content().string("[]"));
    }

    @Test //pass
    public void checkGet_whenNonExistingPlz_thenIsOkAndEmpty() throws Exception {
        Integer plz = 893247;
        doReturn(new ArrayList<Advert>()).when(locationController).findByNameOrPlz(null, plz);
        mockMvc.perform(get("/locations")
                        .contentType("application/json")
                        .queryParam("plz", String.valueOf(plz)))
                .andExpect(status().isOk()).andExpect(content().string("[]"));
    }

    @Test //pass
    public void checkGet_whenNonExistingPlzAndName_thenIsOkAndEmpty() throws Exception {
        String locationName = "NotExistingLocation";
        Integer plz = 893247;
        doReturn(new ArrayList<Advert>()).when(locationController).findByNameOrPlz(locationName, plz);
        mockMvc.perform(get("/locations")
                        .contentType("application/json")
                        .queryParam("name", String.valueOf(locationName))
                        .queryParam("plz", String.valueOf(plz)))
                .andExpect(status().isOk()).andExpect(content().string("[]"));
    }

    @Test //pass
    public void checkGetById_whenValidId_thenLocationIsReturned() throws Exception {

        mockMvc.perform(get("/locations/" + 1)
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test //not working (NoSuchElementException)
    public void checkGetByID_whenInvalidId_thenIsNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(locationController).findById(0);
        mockMvc.perform(get("/locations/" + 0)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test //pass
    public void checkPut_whenValidLocation_thenIsOk() throws Exception {
        mockMvc.perform(put("/locations")
                        .contentType("application/json")
                        .content("{\"id\":\"1\", \"plz\":\"3292\", \"users\":[{\"id\":\"1\"}], \"name\":\"Dotzigen\"}"))
                .andExpect(status().isOk());
    }

    @Test //pass
    public void checkPut_whenInvalidLocation_thenIsBadRequest() throws Exception {
        mockMvc.perform(put("/locations")
                        .contentType("application/json")
                        .content("{\"id\":\"1\" \"plz\":\"3292\", \"users\":[{\"id\":\"1\"}], \"name\":\"Dotzigen\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test //pass
    public void checkPost_whenNewValidLocationInserted_thenIsCreated() throws Exception {
        mockMvc.perform(post("/locations")
                        .contentType("application/json")
                        .content(postValidLocationJson)
                        .accept("application/json"))
                .andExpect(status().isCreated());
    }

    @Test //pass
    public void checkPost_whenNewInvalidLocationInserted_thenIsBadRequest() throws Exception {
        mockMvc.perform(post("/locations")
                        .contentType("application/json")
                        .content(postInvalidLocationJson)
                        .accept("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test //pass
    public void checkDelete_whenValidId_thenIsOk() throws Exception {
        mockMvc.perform(delete("/locations/1")
                        .contentType("application/json"))
                .andExpect(status().isOk());

        Mockito.verify(locationController).deleteById(eq(1));
    }

    @Test //error (EmptyResultDataAccessException)
    public void checkDelete_whenInvalidId_thenIsNotFound() throws Exception {
        doThrow(EmptyResultDataAccessException.class).when(locationController).deleteById(0);
        mockMvc.perform(delete("/locations/" + 0)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        Mockito.verify(locationController).deleteById(0);
    }
}
