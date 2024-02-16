package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDiningCommons;
import edu.ucsb.cs156.example.entities.UCSBOrganisation;
import edu.ucsb.cs156.example.repositories.UCSBOrganisationRepository;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBOrganisationController.class)
@Import(TestConfig.class)
public class UCSBOrganisationControllerTests extends ControllerTestCase {

        @MockBean
        UCSBOrganisationRepository UCSBOrganisationRepository;

        @MockBean
        UserRepository userRepository;

        // Tests for GET /api/UCSBOrganization/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/UCSBOrganization/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }


        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/UCSBOrganization/all"))
                                .andExpect(status().is(200)); // logged
        }


        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {
    
                // arrange
    
                when(UCSBOrganisationRepository.findById(eq("OISS"))).thenReturn(Optional.empty());
    
                // act
                MvcResult response = mockMvc.perform(get("/api/UCSBOrganization?orgCode=OISS"))
                                .andExpect(status().isNotFound()).andReturn();
    
                // assert
    
                verify(UCSBOrganisationRepository, times(1)).findById(eq("OISS"));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("UCSBOrganisation with id OISS not found", json.get("message"));
        }


        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_UCSBOrganisations() throws Exception {

                // arrange

                UCSBOrganisation GDSC = UCSBOrganisation.builder()
                                .orgCode("GDSC")
                                .orgTranslationShort("Google Developer Student Club")
                                .orgTranslation("Google Developer Student Club")
                                .inactive(false)
                                .build();

                UCSBOrganisation ATO = UCSBOrganisation.builder()
                                .orgCode("Alpha Tao Omega")
                                .orgTranslationShort("Alpha Tao Omega")
                                .orgTranslation("Alpha Tao Omega")
                                .inactive(false)
                                .build();

                ArrayList<UCSBOrganisation> expectedorgs = new ArrayList<>();
                expectedorgs.addAll(Arrays.asList(GDSC, ATO));

                when(UCSBOrganisationRepository.findAll()).thenReturn(expectedorgs);

                // act
                MvcResult response = mockMvc.perform(get("/api/UCSBOrganization/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(UCSBOrganisationRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedorgs);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for POST /api/UCSBOrganization...

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/UCSBOrganization/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/UCSBOrganization/post"))
                                .andExpect(status().is(403)); // only admins can post
        }



        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_org() throws Exception {
                // arrange

                UCSBOrganisation OISS = UCSBOrganisation.builder()
                                .orgCode("OISS")
                                .orgTranslationShort("INTERNATIONAL STUDENTS")
                                .orgTranslation("OFFICE OF INTERNATIONAL STUDENTS AND SCHOLARS")
                                .inactive(true)
                                .build();

                when(UCSBOrganisationRepository.save(eq(OISS))).thenReturn(OISS);

                // act
                MvcResult response = mockMvc.perform(
                        post("/api/UCSBOrganization/post?orgCode=OISS&orgTranslationShort=INTERNATIONAL STUDENTS&orgTranslation=OFFICE OF INTERNATIONAL STUDENTS AND SCHOLARS&inactive=true")
                        .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(UCSBOrganisationRepository, times(1)).save(OISS);
                String expectedJson = mapper.writeValueAsString(OISS);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }


        // Tests for GET /api/UCSBOrganization?...

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/UCSBOrganization?orgCode=IVCRC"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        @WithMockUser(roles = { "USER" })

        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange

                UCSBOrganisation org = UCSBOrganisation.builder()
                                .orgCode("OISS")
                                .orgTranslationShort("INTERNATIONAL STUDENTS")
                                .orgTranslation("OFFICE OF INTERNATIONAL STUDENTS AND SCHOLARS")
                                .inactive(true)
                                .build();

                when(UCSBOrganisationRepository.findById(eq("OISS"))).thenReturn(Optional.of(org));

                // act
                MvcResult response = mockMvc.perform(get("/api/UCSBOrganization?orgCode=OISS"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(UCSBOrganisationRepository, times(1)).findById(eq("OISS"));
                String expectedJson = mapper.writeValueAsString(org);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for DELETE /api/UCSBOrganization?...

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_date() throws Exception {
            // arrange
    
            UCSBOrganisation OISS = UCSBOrganisation.builder()
                            .orgCode("OISS")
                            .orgTranslationShort("INTERNATIONAL STUDENTS")
                            .orgTranslation("OFFICE OF INTERNATIONAL STUDENTS AND SCHOLARS")
                            .inactive(true)
                            .build();
    
            when(UCSBOrganisationRepository.findById(eq("OISS"))).thenReturn(Optional.of(OISS));
    
            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/UCSBOrganization?orgCode=OISS")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();
    
            // assert
            verify(UCSBOrganisationRepository, times(1)).findById("OISS");
            verify(UCSBOrganisationRepository, times(1)).delete(any());
    
            Map<String, Object> json = responseToJson(response);
            assertEquals("UCSBOrganisation with id OISS deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_organisation_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(UCSBOrganisationRepository.findById(eq("OISS"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/UCSBOrganization?orgCode=OISS")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(UCSBOrganisationRepository, times(1)).findById("OISS");
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganisation with id OISS not found", json.get("message"));
        }

        // Tests for PUT /api/UCSBOrganization?...

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_orgs() throws Exception {
                // arrange

                UCSBOrganisation OISS = UCSBOrganisation.builder()
                                .orgCode("OISS")
                                .orgTranslationShort("INTERNATIONAL STUDENTS")
                                .orgTranslation("OFFICE OF INTERNATIONAL STUDENTS AND SCHOLARS")
                                .inactive(true)
                                .build();

                UCSBOrganisation OISSEdited = UCSBOrganisation.builder()
                                .orgCode("OISS")
                                .orgTranslationShort("INTERNATIONAL STUDENTSs")
                                .orgTranslation("OFFICE OF INTERNATIONAL STUDENTS AND SCHOLARSs")
                                .inactive(false)
                                .build();

                String requestBody = mapper.writeValueAsString(OISSEdited);

                when(UCSBOrganisationRepository.findById(eq("OISS"))).thenReturn(Optional.of(OISS));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/UCSBOrganization?orgCode=OISS")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(UCSBOrganisationRepository, times(1)).findById("OISS");
                verify(UCSBOrganisationRepository, times(1)).save(OISSEdited); // should be saved with updated info
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }


        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_org_that_does_not_exist() throws Exception {
                // arrange

                UCSBOrganisation EditedOISS = UCSBOrganisation.builder()
                                .orgCode("OISS")
                                .orgTranslationShort("INTERNATIONAL STUDENTS")
                                .orgTranslation("OFFICE OF INTERNATIONAL STUDENTS AND SCHOLARS")
                                .inactive(false)
                                .build();

                String requestBody = mapper.writeValueAsString(EditedOISS);

                when(UCSBOrganisationRepository.findById(eq("OISS"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/UCSBOrganization?orgCode=OISS")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(UCSBOrganisationRepository, times(1)).findById("OISS");
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganisation with id OISS not found", json.get("message"));

        }
}
