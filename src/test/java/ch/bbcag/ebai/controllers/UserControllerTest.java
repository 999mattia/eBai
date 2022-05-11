package ch.bbcag.ebai.controllers;

import ch.bbcag.ebai.models.Advert;
import ch.bbcag.ebai.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static ch.bbcag.ebai.utils.TestDataUtils.getTestAdverts;
import static ch.bbcag.ebai.utils.TestDataUtils.getTestUsers;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    private String postValidUserJson = "{\"id\":\"0\", \"name\":\"test\", \"location\":{\"id\":\"1\"}, \"adverts\":[], \"bids\":[]}";
    private String postInvalidUserJson = "{\"id\":\"0\" \"location\":{\"id\":\"1\"}, \"adverts\":[], \"bids\":[]}";
    private String allItemsJson = """
            [
              {
                "id": 1,
                "name": "Mattia",
                "adverts": [],
                "bids": [
                  {
                    "id": 1,
                    "value": 1
                  },
                  {
                    "id": 4,
                    "value": 1
                  }
                ]
              },
              {
                "id": 2,
                "name": "Levin",
                "adverts": [
                  {
                    "id": 1,
                    "name": "Kopfhörer Blau",
                    "bids": [
                      {
                        "id": 1,
                        "value": 1
                      },
                      {
                        "id": 4,
                        "value": 1
                      }
                    ]
                  }
                ],
                "bids": []
              },
              {
                "id": 5,
                "name": "test",
                "adverts": [],
                "bids": []
              }
            ]
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserController userController;

    @Test //pass
    public void checkGet_whenNoParam_thenAllUsersAreReturned() throws Exception {

        List<User> users = getTestUsers();

        doReturn(users).when(userController).findByName(null);
        String allUsers = objectMapper.writeValueAsString(users);
        mockMvc.perform(get("/users")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(allUsers));
    }


    @Test //error
    public void checkGet_whenExistingName_thenUserIsReturned() throws Exception {
        String userName = "User 1";
        doReturn(getTestAdverts()).when(userController).findByName(userName);

        mockMvc.perform(get("/users")
                        .contentType("application/json")
                        .queryParam("name", userName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].name").value(userName));
    }

    @Test //pass
    public void checkGet_whenNotExistingName_thenIsOkAndEmpty() throws Exception {
        String userName = "NotExistingUser";
        doReturn(new ArrayList<Advert>()).when(userController).findByName(userName);
        mockMvc.perform(get("/users")
                        .contentType("application/json")
                        .queryParam("name", userName))
                .andExpect(status().isOk()).andExpect(MockMvcResultMatchers.content().string("[]"));
    }


    @Test //pass
    public void checkGetById_whenValidId_thenUserIsReturned() throws Exception {

        mockMvc.perform(get("/users/" + 1)
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test //error
    public void checkGetByID_whenInvalidId_thenIsNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(userController).findById(0);
        mockMvc.perform(get("/users/" + 0)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test //pass
    public void checkPut_whenValidUser_thenIsOk() throws Exception {
        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content("{\"id\":\"1\", \"name\":\"MattiaTest\", \"location\":{\"id\":\"1\"}, \"adverts\":[], \"bids\":[]}"))
                .andExpect(status().isOk());
    }

    @Test //pass
    public void checkPut_whenInvalidLocation_thenIsNotFound() throws Exception {
        mockMvc.perform(put("/locations")
                        .contentType("application/json")
                        .content("{\"id\":\"1\", \"name\":\"MattiaTest\" \"location\":{\"id\":\"1\"}, \"adverts\":[], \"bids\":[]}"))
                .andExpect(status().isNotFound());
    }

    @Test //pass
    public void checkPost_whenNewValidBidInserted_thenIsCreated() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(postValidUserJson)
                        .accept("application/json"))
                .andExpect(status().isCreated());
    }

    @Test //pass
    public void checkPost_whenNewInvalidBidInserted_thenIsBadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(postInvalidUserJson)
                        .accept("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test //pass
    public void checkDelete_whenValidId_thenIsOk() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .contentType("application/json"))
                .andExpect(status().isOk());

        Mockito.verify(userController).deleteById(eq(1));
    }

    @Test //error
    public void checkDelete_whenInvalidId_thenIsNotFound() throws Exception {
        doThrow(EmptyResultDataAccessException.class).when(userController).deleteById(0);
        mockMvc.perform(delete("/locations/0")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        Mockito.verify(userController).deleteById(0);
    }
}
