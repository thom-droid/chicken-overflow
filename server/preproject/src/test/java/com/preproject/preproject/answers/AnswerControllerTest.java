package com.preproject.preproject.answers;

import com.google.gson.Gson;
import com.preproject.preproject.answers.controller.AnswerController;
import com.preproject.preproject.answers.dto.AnswerPatchDto;
import com.preproject.preproject.answers.dto.AnswerPostDto;
import com.preproject.preproject.answers.dto.AnswerResponseDto;
import com.preproject.preproject.answers.entity.Answer;
import com.preproject.preproject.answers.mapper.mapstruct.AnswerMapper;
import com.preproject.preproject.answers.service.AnswerService;
import com.preproject.preproject.users.dto.UsersResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.preproject.preproject.ApiDocumentUtils.getRequestPreProcessor;
import static com.preproject.preproject.ApiDocumentUtils.getResponsePreProcessor;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(AnswerController.class)
@AutoConfigureRestDocs
public class AnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockBean
    private AnswerService answerService;

    @MockBean
    private AnswerMapper answerMapper;

    @Test
    @DisplayName("???????????? ?????????")
    public void postAnswerTest() throws Exception {

        //given
        AnswerPostDto post = AnswerPostDto.builder()
                .userId(1L)
                .questionId(1L)
                .content("????????????").build();

        AnswerResponseDto responseDto =
                AnswerResponseDto.builder()
                        .answerId(1L)
                        .content("????????????")
                        .user(
                                UsersResponseDto.builder()
                                        .userId(1L)
                                        .displayName("user1")
                                        .build()
                        ).build();

        String content = gson.toJson(post);

        given(answerMapper.answerPost(Mockito.any(AnswerPostDto.class))).willReturn(Mockito.mock(Answer.class));
        given(answerService.createAnswer(Mockito.any(Answer.class))).willReturn(Mockito.mock(Answer.class));
        given(answerMapper.answerResponse(Mockito.any(Answer.class))).willReturn(responseDto);

        //when
        ResultActions actions =
                mockMvc.perform(post("/questions/{question-id}/answer", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));


        //then
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.content").value("????????????"))
                .andDo(document("post-answer",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        pathParameters(
                                parameterWithName("question-id").description("?????? ??????")
                        ),
                        requestFields(
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("questionId").type(JsonFieldType.NUMBER).description("????????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("data.answerId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("data.user").type(JsonFieldType.OBJECT).description("????????? ??????"),
                                fieldWithPath("data.user.userId").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("data.user.displayName").type(JsonFieldType.STRING).description("????????? ??????")
                        )));
    }

    @Test
    @DisplayName("?????? ?????? ?????????")
    public void patchAnswerTest() throws Exception {

        //given
        long answer_id = 1L;

        AnswerPatchDto patch = AnswerPatchDto.builder()
                .answerId(answer_id)
                .content("?????? ??????")
                .build();

        AnswerResponseDto responseDto =
                AnswerResponseDto.builder()
                        .answerId(1L)
                        .content(patch.getContent())
                        .user(
                                UsersResponseDto.builder()
                                        .userId(1L)
                                        .displayName("user1")
                                        .build()
                        ).build();

        String content = gson.toJson(patch);

        given(answerMapper.answerPatch(Mockito.any(AnswerPatchDto.class))).willReturn(Mockito.mock(Answer.class));
        given(answerService.updateAnswer(Mockito.any(Answer.class))).willReturn(Mockito.mock(Answer.class));
        given(answerMapper.answerResponse(Mockito.any(Answer.class))).willReturn(responseDto);

        //when
        ResultActions actions =
                mockMvc.perform(patch("/questions/{question-id}/answer/{answer-id}", 1, answer_id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.answerId").value(patch.getAnswerId()))
                .andExpect(jsonPath("$.data.content").value("?????? ??????"))
                .andDo(document("patch-answer",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        pathParameters(
                                parameterWithName("answer-id").description("?????? ??????"),
                                parameterWithName("question-id").description("?????? ??????")
                        ),
                        requestFields(
                                fieldWithPath("answerId").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("data.answerId").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("data.user").type(JsonFieldType.OBJECT).description("?????? ????????? ??????"),
                                fieldWithPath("data.user.userId").type(JsonFieldType.NUMBER).description("?????? ????????? ?????????"),
                                fieldWithPath("data.user.displayName").type(JsonFieldType.STRING).description("????????? ??????")
                        )));

    }

    @Test
    @DisplayName("?????? ?????? ?????????")
    public void deleteAnswerTest() throws Exception {

        //given
//        doNothing();

        //when
        ResultActions actions = mockMvc.perform(delete("/questions/{question-id}/answer/{answer-id}", 1, 1));

        //then
        MvcResult result = actions.andExpect(status().isNoContent())
                .andDo(
                        document("delete-answer",
                                pathParameters(
                                        parameterWithName("question-id").description("?????? ??????"),
                                        parameterWithName("answer-id").description("?????? ??????")
                                ),
                                responseFields(
                                        fieldWithPath("data").type(JsonFieldType.STRING).description("?????? ?????? ??? ?????? ?????????")
                                )))
                .andReturn();
    }
}
