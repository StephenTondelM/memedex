package com.memecorps.memedex.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memecorps.memedex.model.MemePost;
import com.memecorps.memedex.repository.MemePostRepository;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    Page<MemePost> memePostFakePages;

    @BeforeEach
    void beforeEach() {
        memePostFakeList = new ArrayList<>();

        memePostFakeList.add(MemePost.builder()
                .id("1")
                .user("testUser")
                .memeUrl("memesareus.com")
                .timestamp(1235123).build());

        memePostFakeList.add(MemePost.builder()
                .id("2")
                .user("testUser2")
                .memeUrl("memesareus.com")
                .timestamp(1235127).build());

        memePostFakePages = new PageImpl<>(memePostFakeList);
    }

    @Test
    void getAllValidRequestWithAllFieldsAndDefaultPaginationAndDescOrderByTimeTest() throws Exception {
        when(memePostRepositoryMock.findAll(any(Pageable.class))).thenReturn(memePostFakePages);

        mockMvc.perform(get("/api/memepost"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.[0].id").value(("1")))
                .andExpect(jsonPath("$.content.[0].user").value("testUser"))
                .andExpect(jsonPath("$.content.[0].memeUrl").value("memesareus.com"))
                .andExpect(jsonPath("$.content.[0].timestamp").value(1235123))
                .andExpect(jsonPath("$.content.[1].id").value(("2")))
                .andExpect(jsonPath("$.content.[1].user").value("testUser2"))
                .andExpect(jsonPath("$.content.[1].memeUrl").value("memesareus.com"))
                .andExpect(jsonPath("$.content.[1].timestamp").value(1235127));

        verify(memePostRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllValidRequestWithOutOfBoundPageTest() throws Exception {
        Page<MemePost> dummyPages = new PageImpl<>(new ArrayList<>());
        when(memePostRepositoryMock.findAll(any(Pageable.class))).thenReturn(dummyPages);

        mockMvc.perform(get("/api/memepost?page=3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        verify(memePostRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllInvalidRequestWithInvalidPageTest() throws Exception {
        Page<MemePost> dummyPages = new PageImpl<>(new ArrayList<>());
        when(memePostRepositoryMock.findAll(any(Pageable.class))).thenReturn(dummyPages);

        mockMvc.perform(get("/api/memepost?page=-1"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(memePostRepositoryMock, times(0)).findAll(any(Pageable.class));
    }

    @Test
    void getByIdValidRequestWithExistingResourceAndAllFieldsTest() throws Exception {
        when(memePostRepositoryMock.findById(any(String.class))).thenReturn(Optional.ofNullable(memePostFakeList.get(0)));

        mockMvc.perform(get("/api/memepost/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.user").value("testUser"))
                .andExpect(jsonPath("$.memeUrl").value("memesareus.com"))
                .andExpect(jsonPath("$.timestamp").value(1235123));

        verify(memePostRepositoryMock, times(1)).findById(any(String.class));
    }

    @Test
    void getByIdInvalidRequestWithNonExistingResourceTest() throws Exception {
        when(memePostRepositoryMock.findById(any(String.class))).thenReturn(Optional.ofNullable(null));

        mockMvc.perform(get("/api/memepost/12"))
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
                .andExpect(jsonPath("$.id").value(IsNull.nullValue())) // Will be auto generated by MongoDB
                .andExpect(jsonPath("$.user").value("testUser"))
                .andExpect(jsonPath("$.memeUrl").value("memesareus.com"))
                .andExpect(jsonPath("$.timestamp").isNumber());

        verify(memePostRepositoryMock, times(1)).findById(any(String.class));
        verify(memePostRepositoryMock, times(1)).save(any(MemePost.class));
    }

    @Test
    void createPostValidRequestWithNoIdFieldTest() throws Exception {
        MemePost memePostFake = memePostFakeList.get(0);
        memePostFake.setId(null);

        when(memePostRepositoryMock.save(any(MemePost.class))).then(returnsFirstArg());


        mockMvc.perform(post("/api/memepost")
                        .content(asJsonString(memePostFake))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(IsNull.nullValue())) // Will be auto generated by MongoDB
                .andExpect(jsonPath("$.user").value("testUser"))
                .andExpect(jsonPath("$.memeUrl").value("memesareus.com"))
                .andExpect(jsonPath("$.timestamp").isNumber());

        verify(memePostRepositoryMock, times(0)).findById(any());
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

        mockMvc.perform(delete("/api/memepost/1"))
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


        mockMvc.perform(put("/api/memepost/1")
                        .content(asJsonString(memePostFake))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
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

        mockMvc.perform(put("/api/memepost/12")
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