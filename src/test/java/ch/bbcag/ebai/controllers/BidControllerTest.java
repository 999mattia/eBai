package ch.bbcag.ebai.controllers;

import ch.bbcag.ebai.models.Advert;
import ch.bbcag.ebai.models.Bid;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import java.util.Optional;

import static ch.bbcag.ebai.utils.TestDataUtils.getTestAdverts;
import static ch.bbcag.ebai.utils.TestDataUtils.getTestBids;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BidController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BidControllerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    private String postValidBidJson = "{\"id\":\"0\", \"advert\":{\"id\":\"1\"}, \"user\":{\"id\":\"1\"}}";
    private String postInvalidBidJson = "{\"id\":\"0\" \"user\":{\"id\":\"1\"}}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BidController bidController;


    @Test //pass
    public void checkGet_whenNoParam_thenAllBidsAreReturned() throws Exception {

        List<Bid> bids = getTestBids();

        doReturn(bids).when(bidController).findByValue(null);
        String allBids = objectMapper.writeValueAsString(bids);
        mockMvc.perform(get("/bids")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(allBids));
    }


    @Test //pass
    public void checkGet_whenExistingValue_thenBidIsReturned() throws Exception {
        Integer value = 1;
        doReturn(getTestBids()).when(bidController).findByValue(value);

        mockMvc.perform(get("/bids")
                        .contentType("application/json")
                        .queryParam("value", String.valueOf(value)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].value").value(value));
    }

    @Test //pass
    public void checkGet_whenNotExistingValue_thenisOkAndEmpty() throws Exception {
        Integer value = 239845;
        doReturn(new ArrayList<Advert>()).when(bidController).findByValue(value);
        mockMvc.perform(get("/bids")
                        .contentType("application/json")
                        .queryParam("value", String.valueOf(value)))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test //pass
    public void checkGetById_whenValidId_thenBidIsReturned() throws Exception {
        mockMvc.perform(get("/bids/" + 1)
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    public void checkGetByID_whenInvalidId_thenIsNotFound() throws Exception { //not working
        doReturn(Optional.empty()).when(bidController).findById(0);
        mockMvc.perform(get("/bids/" + 0)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test //error
    public void checkPut_whenValidBid_thenIsOk() throws Exception {
        mockMvc.perform(put("/bids")
                .contentType("application/json")
                .content("{\"id\":1, \"value\":1, \"advert\":{\"id\":1}, \"user\":{\"id\":1}}"));
    }

    @Test //pass
    public void checkPut_whenInvalidBid_thenIsNotFound() throws Exception {
        mockMvc.perform(put("/adverts")
                        .contentType("application/json")
                        .content("{\"id\":\"1\", \"value\":\"1\", \"advert\":{\"id\":\"1\"}, \"user\":{\"id\":\"1\"}}"))
                .andExpect(status().isNotFound());
    }

    @Test //error
    public void checkPost_whenNewValidBidInserted_thenIsCreated() throws Exception {
        mockMvc.perform(post("/bids")
                        .contentType("application/json")
                        .content(postValidBidJson)
                        .accept("application/json"))
                .andExpect(status().isCreated());
    }

    @Test //pass
    public void checkPost_whenNewInvalidBidInserted_thenIsBadRequest() throws Exception {
        mockMvc.perform(post("/bids")
                        .contentType("application/json")
                        .content(postInvalidBidJson)
                        .accept("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test //pass
    public void checkDelete_whenValidId_thenIsOk() throws Exception {
        mockMvc.perform(delete("/bids/1")
                        .contentType("application/json"))
                .andExpect(status().isOk());

        Mockito.verify(bidController).deleteById(eq(1));
    }


    @Test
    public void checkDelete_whenInvalidId_thenIsNotFound() throws Exception { //not working (EmptyResultDataAccessException)
        doThrow(EmptyResultDataAccessException.class).when(bidController).deleteById(0);
        mockMvc.perform(delete("/bids/" + 0)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        Mockito.verify(bidController).deleteById(0);
    }
}
