package com.memecorps.memedex.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memecorps.memedex.model.MemePost;
import com.memecorps.memedex.repository.MemePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class MemePostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MemePostRepository memePostRepositoryMock;

    List<MemePost> memePostFakeList;

    @BeforeEach
    void beforeEach() {
        memePostFakeList = new ArrayList<>();

        memePostFakeList.add(MemePost.builder()
                .id("123")
                .user("testUser")
                .memeUrl("memesareus.com")
                .timestamp(1235123).build());

        memePostFakeList.add(MemePost.builder()
                .id("1234")
                .user("testUser2")
                .memeUrl("memesareus.com")
                .timestamp(1235127).build());
    }

    @Test
    void getAllValidRequestWithAllFieldsTest() throws Exception {
        when(memePostRepositoryMock.findAll()).thenReturn(memePostFakeList);

        mockMvc.perform(get("/api/memepost"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(("123")))
                .andExpect(jsonPath("$[0].user").value("testUser"))
                .andExpect(jsonPath("$[0].memeUrl").value("memesareus.com"))
                .andExpect(jsonPath("$[0].timestamp").value(1235123))
                .andExpect(jsonPath("$[1].id").value(("1234")))
                .andExpect(jsonPath("$[1].user").value("testUser2"))
                .andExpect(jsonPath("$[1].memeUrl").value("memesareus.com"))
                .andExpect(jsonPath("$[1].timestamp").value(1235127));

        verify(memePostRepositoryMock, times(1)).findAll();
    }

    @Test
    void getByIdValidRequestWithExistingResourceAndAllFieldsTest() throws Exception {
        when(memePostRepositoryMock.findById(any(String.class))).thenReturn(Optional.ofNullable(memePostFakeList.get(0)));

        mockMvc.perform(get("/api/memepost/123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.user").value("testUser"))
                .andExpect(jsonPath("$.memeUrl").value("memesareus.com"))
                .andExpect(jsonPath("$.timestamp").value(1235123));

        verify(memePostRepositoryMock, times(1)).findById(any(String.class));
    }

    @Test
    void getByIdInvalidRequestWithNonExistingResourceTest() throws Exception {
        when(memePostRepositoryMock.findById(any(String.class))).thenReturn(Optional.ofNullable(null));

        mockMvc.perform(get("/api/memepost/123"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(memePostRepositoryMock, times(1)).findById(any(String.class));
    }

    @Test
    void createPostValidRequestWithAllFieldsFilledTest() throws Exception {
        MemePost memePostFake = memePostFakeList.get(0);

        when(memePostRepositoryMock.findById(any(String.class))).thenReturn(Optional.ofNullable(null));
        when(memePostRepositoryMock.save(any(MemePost.class))).then(returnsFirstArg());


        mockMvc.perform(post("/api/memepost")
                        .content(asJsonString(memePostFake))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.user").value("testUser"))
                .andExpect(jsonPath("$.memeUrl").value("memesareus.com"))
                .andExpect(jsonPath("$.timestamp").value(1235123));

        verify(memePostRepositoryMock, times(1)).findById(any(String.class));
        verify(memePostRepositoryMock, times(1)).save(any(MemePost.class));
    }

    @Test
    void createPostInvalidRequestWithExistingResourceTest() throws Exception {
        MemePost memePostFake = memePostFakeList.get(0);

        when(memePostRepositoryMock.findById(any(String.class))).thenReturn(Optional.ofNullable(memePostFake));

        mockMvc.perform(post("/api/memepost")
                        .content(asJsonString(memePostFake))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());

        verify(memePostRepositoryMock, times(1)).findById(any(String.class));
        verify(memePostRepositoryMock, times(0)).save(any(MemePost.class));
    }

    @Test
    void deleteByIdValidRequestWithExistingResourceTest() throws Exception {
        MemePost memePostFake = memePostFakeList.get(0);
        
        when(memePostRepositoryMock.findById(any(String.class))).thenReturn(Optional.ofNullable(memePostFake));
        doNothing().when(memePostRepositoryMock).deleteById(any(String.class));

        mockMvc.perform(delete("/api/memepost/123"))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(memePostRepositoryMock, times(1)).findById(any(String.class));
        verify(memePostRepositoryMock, times(1)).deleteById(any(String.class));
    }

    @Test
    void deleteByIdInvalidRequestWithNonExistingResourceTest() throws Exception {
        when(memePostRepositoryMock.findById(any(String.class))).thenReturn(Optional.ofNullable(null));

        mockMvc.perform(delete("/api/memepost/25"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(memePostRepositoryMock, times(1)).findById(any(String.class));
        verify(memePostRepositoryMock, times(0)).deleteById(any(String.class));
    }

    @Test
    void updateByIdValidRequestWithExistingResourceAndAllFieldsFilledTest() throws Exception {
        MemePost memePostFake = memePostFakeList.get(0);
        MemePost memePostFake2 = memePostFakeList.get(1);

        when(memePostRepositoryMock.findById(any(String.class))).thenReturn(Optional.ofNullable(memePostFake2));
        when(memePostRepositoryMock.save(any(MemePost.class))).then(returnsFirstArg());


        mockMvc.perform(put("/api/memepost/123")
                        .content(asJsonString(memePostFake))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1234"))
                .andExpect(jsonPath("$.user").value("testUser"))
                .andExpect(jsonPath("$.memeUrl").value("memesareus.com"))
                .andExpect(jsonPath("$.timestamp").value(1235123));

        verify(memePostRepositoryMock, times(1)).findById(any(String.class));
        verify(memePostRepositoryMock, times(1)).save(any(MemePost.class));
    }

    @Test
    void updateByIdInvalidRequestWithNonExistingResourceTest() throws Exception {
        MemePost memePostFake = memePostFakeList.get(0);

        when(memePostRepositoryMock.findById(any(String.class))).thenReturn(Optional.ofNullable(null));

        mockMvc.perform(put("/api/memepost/123")
                        .content(asJsonString(memePostFake))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(memePostRepositoryMock, times(1)).findById(any(String.class));
        verify(memePostRepositoryMock, times(0)).save(any(MemePost.class));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}