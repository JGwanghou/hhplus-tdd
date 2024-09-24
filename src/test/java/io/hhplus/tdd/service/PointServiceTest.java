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
        UserPoint mockUserPoint = new UserPoint(userId, amount, System.currentTimeMillis());

        when(userPointTable.insertOrUpdate(userId, amount)).thenReturn(mockUserPoint);
        UserPoint result = pointService.addPoint(userId, amount);

        verify(userPointTable).insertOrUpdate(userId, amount);
        verify(pointHistoryTable).insert(eq(userId), eq(amount), eq(TransactionType.CHARGE), anyLong());

        assertEquals(mockUserPoint, result);
    }

    @Test
    @DisplayName("특정 유저 포인트 사용 서비스 테스트")
    void usePoint() {
        long userId = 1L;
        long amount = 3500L;
        UserPoint mockUserPoint = new UserPoint(userId, amount, System.currentTimeMillis());

        when(userPointTable.insertOrUpdate(userId, amount)).thenReturn(mockUserPoint);
        UserPoint result = pointService.usePoint(userId, amount);

        verify(userPointTable).insertOrUpdate(userId, amount);
        verify(pointHistoryTable).insert(eq(userId), eq(amount), eq(TransactionType.USE), anyLong());

        assertEquals(mockUserPoint, result);

    }
}