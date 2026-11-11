/*
 * SampleDataFactory - 시연용 초기 데이터 생성
 * 시연용 자료(B001~B003, M001~M002, R001)와 회원(G001, S001)을 등록하고,
 * 대출·반납 기록을 실제 서비스 흐름(LoanService)을 거쳐 만든다.
 * 날짜는 오늘 기준 상대값으로 만들어 발표 날짜와 무관하게 연체·이력이 보이도록 한다.
 * 주요 메소드: load(CatalogService, MemberService, LoanService, LibraryClock)
 */
package com.library.util;

public class SampleDataFactory {
}
