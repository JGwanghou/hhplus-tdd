package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

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
        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
        return userPointTable.insertOrUpdate(id, amount);
    }
//
//    public UserPoint getPoint(Long id) {
//        return userPointTable.selectById(id);
//    }
}
