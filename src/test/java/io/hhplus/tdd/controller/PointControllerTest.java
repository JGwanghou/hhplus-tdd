package io.hhplus.tdd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.TddApplication;
import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@WebMvcTest(PointController.class)
@ContextConfiguration(classes = {PointController.class, TddApplication.class})
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PointService pointService;

    @Test
    @DisplayName("특정 유저 포인트 조회")
    void point() throws Exception {
        long testId = 370L;
        given(pointService.getUserPoint(testId))
                .willReturn(new UserPoint(testId, 0, System.currentTimeMillis()));

        // when
        mockMvc.perform(get("/point/" + testId))
                .andExpect(status().isOk());

        verify(pointService).getUserPoint(testId);
    }

    @Test
    void history() throws Exception{
        long testId = 370L;

        mockMvc.perform(
                get("/point/" + testId + "/histories"))
                .andExpect(status().isOk());

        verify(pointService).getUserHistorys(testId);
    }

    @Test
    @DisplayName("특정 유저 포인트 충전")
    void charge() throws Exception {
        // given
        long testId = 370L;
        long amount = 2100L;

        given(pointService.addPoint(testId, amount))
                .willReturn(new UserPoint(testId, amount, System.currentTimeMillis()));

        String json = new ObjectMapper().writeValueAsString(amount);

        // when
        mockMvc.perform(patch("/point/" + testId + "/charge")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.point").exists())
                .andExpect(jsonPath("$.updateMillis").exists())
                .andDo(print());

        // then
        verify(pointService).addPoint(testId, amount);
    }

    @Test
    @DisplayName("특정 유저 포인트 사용")
    void use() throws Exception {
        long testId = 123L;
        long initAmount = 5000L;
        long useAmount = 4000L;

        // Mock객체 동작 설정
        given(pointService.addPoint(testId, initAmount))
                .willReturn(new UserPoint(testId, initAmount, System.currentTimeMillis()));

        UserPoint userPoint = pointService.addPoint(testId, initAmount);

        mockMvc.perform(
                patch("/point/" + userPoint.id() + "/use")
                        .content(new ObjectMapper().writeValueAsString(useAmount))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());

        // then
        verify(pointService).usePoint(userPoint.id(), useAmount);
    }
}