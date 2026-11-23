package com.library.model;

import java.util.List;

public class StudentMember extends Member {

    // ===== 필드 =====
    // 필요 없음.


    // ===== 생성자 =====
    public StudentMember(String memberId, String name) {
        super(memberId, name);
    }


    // ===== 추상 클래스 구현 & 조회 =====
    @Override
    public int getMaxLoanCount() { // 유형별 최대 대출 권수 (다형성)
        return 3;           // StudentMember: 최대 대출 권수 3권.
    }

    @Override
    public String getMemberType() { // 유형 이름 반환 (출력용)
        return "학생";        // StudentMember: 유형 이름 "학생".
    }

}
