package io.hhplus.tdd.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private UserPointTable userPointTable;
    @Mock
    private PointHistoryTable pointHistoryTable;

    @InjectMocks
    private PointService pointService;


    @Test
    @DisplayName("특정 유저 포인트 조회 서비스 테스트")
    void getUserPoint() {
        long userId = 10L;
        long amount = 6000L;
        UserPoint mockUserPoint = new UserPoint(userId, amount, System.currentTimeMillis());

        when(userPointTable.selectById(userId)).thenReturn(mockUserPoint);
        UserPoint result = pointService.getUserPoint(userId);

        verify(userPointTable).selectById(userId);

        assertEquals(mockUserPoint, result);
    }

    @Test
    @DisplayName("특정 유저 포인트 내역 조회 서비스 테스트")
    void getUserHistorys() {
        long userId = 20L;
        List<PointHistory> mockHistory = Arrays.asList(
                new PointHistory(1, userId, 3000, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(2, userId, 2000, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(3, userId, 5000, TransactionType.USE, System.currentTimeMillis())
        );

        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(mockHistory);
        List<PointHistory> result = pointService.getUserHistorys(userId);

        verify(pointHistoryTable).selectAllByUserId(userId);
        assertEquals(mockHistory, result);
    }

    @Test
    @DisplayName("특정 유저 포인트 충전 서비스 테스트")
    void addPoint() {
        long userId = 1L;
        long amount = 3500L;

        UserPoint userPoint;
        when(userPointTable.selectById(userId)).thenReturn(
                new UserPoint(userId, 0L, System.currentTimeMillis())
        );

        when(userPointTable.insertOrUpdate(userId, amount)).thenReturn(
                userPoint = new UserPoint(userId, amount, System.currentTimeMillis())
        );

        UserPoint result = pointService.addPoint(userId, amount);

        verify(userPointTable).selectById(userId); // userPoint 조회
        verify(userPointTable).insertOrUpdate(userId, amount);
        verify(pointHistoryTable).insert(eq(userId), eq(amount), eq(TransactionType.CHARGE), anyLong());

        assertEquals(userPoint, result);
    }

    @Test
    @DisplayName("특정 유저 포인트 사용 서비스 테스트")
    void usePoint() {
        long userId = 1L;
        long amount = 3500L;
        long useAmount = 2000L;
        long resultAmount = amount - useAmount;

        UserPoint userPoint;
        when(userPointTable.selectById(userId)).thenReturn(
                new UserPoint(userId, amount, System.currentTimeMillis())
        ); // 3500 포인트 유저 생성

        when(userPointTable.insertOrUpdate(userId, resultAmount)).thenReturn(
                userPoint = new UserPoint(userId, resultAmount, System.currentTimeMillis())
        ); // 아래 useAmount 2000이 Service에서 userPoint.point() - 2000이므로 resultAmount 인자넣어서 차감된 값 기대

        UserPoint result = pointService.usePoint(userId, useAmount);

        verify(userPointTable).selectById(userId);
        verify(userPointTable).insertOrUpdate(userId, resultAmount);
        verify(pointHistoryTable).insert(eq(userId), eq(useAmount), eq(TransactionType.USE), anyLong());

        assertEquals(userPoint, result);
    }

    @Test
    @DisplayName("잔고가 부족한 경우 ")
    void usePointException(){
        long userId = 1L;
        long amount = 1000L; // 잔고 1000으로 설정
        long useAmount = 2000L; // 2000 포인트 사용 시도

        // 잔고가 부족한 유저 설정
        when(userPointTable.selectById(userId)).thenReturn(
                new UserPoint(userId, amount, System.currentTimeMillis())
        ); // 1000 포인트 유저 생성

        IllegalStateException exception = assertThrows(
                IllegalStateException.class, () -> pointService.usePoint(userId, useAmount)
        );

        assertEquals("에러가 발생했습니다.", exception.getMessage());
    }
}