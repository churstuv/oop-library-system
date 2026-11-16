/*
 * [팀원 A 담당 · 연동 확인용 임시 구현] 팀원 A 코드로 교체 예정
 * GeneralMember - 일반 회원: 최대 5권
 */
package com.library.model;

public class GeneralMember extends Member {
    public GeneralMember(String id, String name) {
        super(id, name);
    }

    @Override public int getMaxLoanCount() { return 5; }
    @Override public String getMemberType() { return "일반"; }
}
