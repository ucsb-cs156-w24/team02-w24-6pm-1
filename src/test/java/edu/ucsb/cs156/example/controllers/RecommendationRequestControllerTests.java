package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;
//import edu.ucsb.cs156.example.repositories.UCSBDateRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = RecommendationRequestController.class)
@Import(TestConfig.class)
public class RecommendationRequestControllerTests extends ControllerTestCase {

    @MockBean
    RecommendationRequestRepository recommendationRequestRepository;

    @MockBean
    UserRepository userRepository;

    // Tests for GET /api/recommendation-requests/all

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/recommendation-requests/all"))
                .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
        mockMvc.perform(get("/api/recommendation-requests/all"))
                .andExpect(status().is(200)); // logged
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_recommendation_requests() throws Exception {

        // arrange
        LocalDateTime requestDate1 = LocalDateTime.parse("2022-01-03T00:00:00");
        LocalDateTime requestDate2 = LocalDateTime.parse("2022-01-03T00:00:00");

        RecommendationRequest request1 = RecommendationRequest.builder()
        // request1.setId(1L);
                .requesterEmail("user1@example.com")
                .professorEmail("prof1@example.com")
                .explanation("Request explanation 1")
                .DateRequest(requestDate1)
                .DateNeeded(requestDate2)
                .done(false)
                .build();


        LocalDateTime requestDate3 = LocalDateTime.parse("2022-03-11T00:00:00");
        LocalDateTime requestDate4 = LocalDateTime.parse("2022-03-11T00:00:00");

        RecommendationRequest request2 = RecommendationRequest.builder()
        // request2.setId(2L);
                .requesterEmail("user1@example.com")
                .professorEmail("prof1@example.com")
                .explanation("Request explanation 1")
                .DateRequest(requestDate3)
                .DateNeeded(requestDate4)
                .done(false)
                .build();


        ArrayList<RecommendationRequest> expectedRequests = new ArrayList<>();
        expectedRequests.addAll(Arrays.asList(request1, request2));

        when(recommendationRequestRepository.findAll()).thenReturn(expectedRequests);

        // act
        MvcResult response = mockMvc.perform(get("/api/recommendation-requests/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(recommendationRequestRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedRequests);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

// Tests for POST /api/recommendation-requests/post...

@Test
public void logged_out_users_cannot_post() throws Exception {
    mockMvc.perform(post("/api/recommendation-requests/post"))
            .andExpect(status().is(403));
}

@WithMockUser(roles = { "USER" })
@Test
public void logged_in_regular_users_cannot_post() throws Exception {
    mockMvc.perform(post("/api/recommendation-requests/post"))
            .andExpect(status().is(403)); // only admins can post
}

@WithMockUser(roles = { "ADMIN", "USER" })
@Test
public void an_admin_user_can_post_a_new_recommendation_requests() throws Exception {
    // arrange
    LocalDateTime requestDate1 = LocalDateTime.parse("2022-01-03T00:00:00");
    LocalDateTime requestDate2 = LocalDateTime.parse("2022-01-03T00:00:00");

    RecommendationRequest request1 = RecommendationRequest.builder()
        .requesterEmail("user1@example.com")
        .professorEmail("prof1@example.com")
        .explanation("Request explanation 1")
        .DateRequest(requestDate1)
        .DateNeeded(requestDate2)
        .done(true)
        .build();    


    when(recommendationRequestRepository.save(eq(request1))).thenReturn(request1);
    //when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
    //.thenAnswer(invocation -> invocation.getArgument(0));

    // act
    MvcResult response = mockMvc.perform(
        post("/api/recommendation-requests/post?requesterEmail=user1@example.com&professorEmail=prof1@example.com&explanation=Request explanation 1&DateRequest=2022-01-03T00:00:00&DateNeeded=2022-01-03T00:00:00&done=true")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();
            //.andExpect(status().isOk()).andReturn(); 

    // assert
    verify(recommendationRequestRepository, times(1)).save(request1);
    //verify(recommendationRequestRepository, times(1)).save(any(RecommendationRequest.class));
    String expectedJson = mapper.writeValueAsString(request1);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
}

// Tests for GET /api/recommendation-requests?id=...

@Test
public void logged_out_users_cannot_get_by_id() throws Exception {
    mockMvc.perform(get("/api/recommendation-requests?id=1"))
            .andExpect(status().is(403)); // logged out users can't get by id
}

@WithMockUser(roles = { "USER" })
@Test
public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {
    // arrange
    LocalDateTime requestDate = LocalDateTime.parse("2022-01-03T00:00:00");
    LocalDateTime needDate = LocalDateTime.parse("2022-01-03T00:00:00");


    RecommendationRequest request = RecommendationRequest.builder()
    //request.setId(1L);
        .requesterEmail("user1@example.com")
        .professorEmail("prof1@example.com")
        .explanation("Request explanation 1")
        .DateRequest(requestDate)
        .DateNeeded(needDate)
        .done(false)
        .build();

    when(recommendationRequestRepository.findById(eq(1L))).thenReturn(Optional.of(request));

    // act
    MvcResult response = mockMvc.perform(get("/api/recommendation-requests?id=1"))
            .andExpect(status().isOk()).andReturn();

    // assert

    verify(recommendationRequestRepository, times(1)).findById(eq(1L));
    String expectedJson = mapper.writeValueAsString(request);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
}

@WithMockUser(roles = { "USER" })
@Test
public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {
    // arrange
    when(recommendationRequestRepository.findById(eq(1L))).thenReturn(Optional.empty());

    // act
    MvcResult response = mockMvc.perform(get("/api/recommendation-requests?id=1"))
            .andExpect(status().isNotFound()).andReturn();

    // assert
    verify(recommendationRequestRepository, times(1)).findById(eq(1L));
    Map<String, Object> json = responseToJson(response);
    assertEquals("EntityNotFoundException", json.get("type"));
    assertEquals("RecommendationRequest with id 1 not found", json.get("message"));
}

// Tests for DELETE /api/recommendation-requests?id=...

@WithMockUser(roles = { "ADMIN", "USER" })
@Test
public void admin_can_delete_a_recommendation_request() throws Exception {
    // arrange
    LocalDateTime requestDate = LocalDateTime.parse("2023-03-15T08:00:00");
    LocalDateTime needDate = LocalDateTime.parse("2023-03-15T08:00:00");

    RecommendationRequest recommendationRequest = RecommendationRequest.builder()
    //recommendationRequest.setId(15L);
        .requesterEmail("user@example.com")
        .professorEmail("prof@example.com")
        .explanation("Original request explanation")
        .DateRequest(requestDate)
        .DateNeeded(needDate)
        .done(false)
        .build();

    when(recommendationRequestRepository.findById(eq(15L))).thenReturn(Optional.of(recommendationRequest));

    // act
    MvcResult response = mockMvc.perform(
            delete("/api/recommendation-requests?id=15")
                    .with(csrf()))
            .andExpect(status().isOk()).andReturn();

    // assert
    verify(recommendationRequestRepository, times(1)).findById(15L);
    verify(recommendationRequestRepository, times(1)).delete(any());

    Map<String, Object> json = responseToJson(response);
    assertEquals("Recommendation request with id 15 deleted", json.get("message"));
}

@WithMockUser(roles = { "ADMIN", "USER" })
@Test
public void admin_tries_to_delete_non_existent_recommendation_request_and_gets_right_error_message()
        throws Exception {
    // arrange
    when(recommendationRequestRepository.findById(eq(15L))).thenReturn(Optional.empty());

    // act
    MvcResult response = mockMvc.perform(
            delete("/api/recommendation-requests?id=15")
                    .with(csrf()))
            .andExpect(status().isNotFound()).andReturn();

    // assert
    verify(recommendationRequestRepository, times(1)).findById(15L);
    Map<String, Object> json = responseToJson(response);
    assertEquals("RecommendationRequest with id 15 not found", json.get("message"));
}

// Tests for PUT /api/recommendation-requests?id=...

// @WithMockUser(roles = { "ADMIN", "USER" })
// @Test
// public void admin_can_edit_an_existing_recommendation_request() throws Exception {
//     // arrange
//     LocalDateTime requestDate = LocalDateTime.parse("2023-03-15T08:00:00");
// //     LocalDateTime updatedRequestDate = LocalDateTime.parse("2023-03-20T08:00:00");
    
//     RecommendationRequest originalRequest = new RecommendationRequest();
//     originalRequest.setId(67L);
//     originalRequest.setRequesterEmail("user@example.com");
//     originalRequest.setProfessorEmail("prof@example.com");
//     originalRequest.setExplanation("Original request explanation");
//     originalRequest.setDateRequest(requestDate);
//     originalRequest.setDateNeeded(requestDate);
//     originalRequest.setDone(false);

//     RecommendationRequest updatedRequest = new RecommendationRequest();
//     updatedRequest.setId(67L);
//     updatedRequest.setRequesterEmail("updateduser@example.com");
//     updatedRequest.setProfessorEmail("updatedprof@example.com");
//     updatedRequest.setExplanation("request explanation");

// }
/// Tests for PUT /api/recommendation-requests?id=...

@WithMockUser(roles = { "ADMIN", "USER" })
@Test
public void admin_can_edit_an_existing_recommendation_request() throws Exception {
    // arrange
    LocalDateTime requestDate = LocalDateTime.parse("2023-03-15T08:00:00");
    LocalDateTime updatedRequestDate = LocalDateTime.parse("2023-03-20T08:00:00");

    LocalDateTime requestDate1 = LocalDateTime.parse("2023-03-15T08:00:00");
    LocalDateTime updatedRequestDate1 = LocalDateTime.parse("2023-03-20T08:00:00");

    RecommendationRequest originalRequest = RecommendationRequest.builder()
        .requesterEmail("user@example.com")
        .professorEmail("prof@example.com")
        .explanation("Original request explanation")
        .DateRequest(requestDate)
        .DateNeeded(updatedRequestDate)
        .build();

        RecommendationRequest updRequest = RecommendationRequest.builder()
        .requesterEmail("user@example.com")
        .professorEmail("prof@example.com")
        .explanation("Original request explanation")
        .DateRequest(requestDate1)
        .DateNeeded(updatedRequestDate1)
        .done(false)
        .build();
//     updatedRequest.setDone(true);

    String requestBody = mapper.writeValueAsString(updRequest);

    when(recommendationRequestRepository.findById(eq(67L))).thenReturn(Optional.of(originalRequest));
    when(recommendationRequestRepository.save(eq(updRequest))).thenReturn(updRequest);

    // act
    MvcResult response = mockMvc.perform(
            put("/api/recommendation-requests?id=67")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestBody)
                    .with(csrf()))
            .andExpect(status().isOk()).andReturn();

    // assert
    verify(recommendationRequestRepository, times(1)).findById(67L);
    verify(recommendationRequestRepository, times(1)).save(updRequest);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(requestBody, responseString);
}

@WithMockUser(roles = { "ADMIN", "USER" })
@Test
public void admin_cannot_edit_recommendation_request_that_does_not_exist() throws Exception {
    // arrange
    LocalDateTime requestDate = LocalDateTime.parse("2023-03-15T08:00:00");
    LocalDateTime requestDate1 = LocalDateTime.parse("2023-03-15T08:00:00");

    RecommendationRequest editedRequest = RecommendationRequest.builder()
    //editedRequest.setId(67);
        .requesterEmail("user@example.com")
        .professorEmail("prof@example.com")
        .explanation("Original request explanation")
        .DateRequest(requestDate)
        .DateNeeded(requestDate1)
        .done(true)
        .build();

    String requestBody = mapper.writeValueAsString(editedRequest);

    when(recommendationRequestRepository.findById(eq(67L))).thenReturn(Optional.empty());

    // act
    MvcResult response = mockMvc.perform(
            put("/api/recommendation-requests?id=67")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestBody)
                    .with(csrf()))
            .andExpect(status().isNotFound()).andReturn();

    // assert
    verify(recommendationRequestRepository, times(1)).findById(67L);
    Map<String, Object> json = responseToJson(response);
    assertEquals("RecommendationRequest with id 67 not found", json.get("message"));
}
}
