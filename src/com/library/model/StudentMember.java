/*
 * [팀원 A 담당 · 연동 확인용 임시 구현] 팀원 A 코드로 교체 예정
 * StudentMember - 학생 회원: 최대 3권
 */
package com.library.model;

public class StudentMember extends Member {
    public StudentMember(String id, String name) {
        super(id, name);
    }

    @Override public int getMaxLoanCount() { return 3; }
    @Override public String getMemberType() { return "학생"; }
}
