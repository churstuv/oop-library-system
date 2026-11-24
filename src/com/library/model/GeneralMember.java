package com.library.model;

import java.util.List;

public class GeneralMember extends Member {

    // ===== 필드 =====
    // 필요 없음.


    // ===== 생성자 =====
    public GeneralMember(String memberId, String name) {
        super(memberId, name);
    }


    // ===== 추상 클래스 구현 & 조회 =====
    @Override
    public int getMaxLoanCount() { // 유형별 최대 대출 권수 (다형성)
        return 5;           // GeneralMember: 최대 대출 권수 5권.
    }

    @Override
    public String getMemberType() { // 유형 이름 반환 (출력용)
        return "일반";        // GeneralMember: 유형 이름 "일반".
    }

}
