package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    // 특정 유저의 포인트를 조회
    public UserPoint getUserPoint(Long id) {
        return userPointTable.selectById(id);
    }

    // 특정 유저의 포인트 충전/이용 내역을 조회
    public List<PointHistory> getUserHistorys(Long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

    // 특정 유저의 포인트를 충전
    public UserPoint addPoint(Long id, Long amount) {
        UserPoint userPoint = userPointTable.selectById(id);
        long point = userPoint.point() + amount;

        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
        return userPointTable.insertOrUpdate(id, point);
    }

    public UserPoint usePoint(Long id, Long amount) {
        UserPoint userPoint = userPointTable.selectById(id);
        if(userPoint.point() < amount){
            throw new IllegalStateException("에러가 발생했습니다.");
        }

        long point = userPoint.point() - amount;
        pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis());
        return userPointTable.insertOrUpdate(id, point);
    }
}
