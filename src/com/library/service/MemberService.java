/*
 * MemberService - 회원 등록과 조회
 * 일반·학생 회원 등록(F3)과 회원 ID 조회를 담당한다.
 * 주요 메소드: registerMember(), findMember(), getAllMembers()
 *
 * [설계 이유]
 *  - 단일 책임: 회원 목록 관리만 맡는다. 최대 대출 권수 같은 규칙은 Member 하위 클래스가 가진다.
 *  - 다형성: Member 타입으로만 다루므로 회원 유형(예: ProfessorMember)이 늘어나도 수정이 필요 없다.
 *  - CatalogService와 같은 방식(Optional 조회, 읽기 전용 목록, ID 대소문자 무시)으로 맞춰
 *    두 서비스를 사용하는 쪽이 헷갈리지 않게 했다.
 */
package com.library.service;

import com.library.model.Member;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class MemberService {
    private final Map<String, Member> members = new LinkedHashMap<>();

    /** 회원 등록 (F3). ID·이름이 비었거나 ID가 중복이면 IllegalArgumentException */
    public void registerMember(Member member) {
        Objects.requireNonNull(member, "등록할 회원이 없습니다.");
        if (member.getId() == null || member.getId().isBlank()) {
            throw new IllegalArgumentException("회원 ID를 입력해 주세요.");
        }
        if (member.getName() == null || member.getName().isBlank()) {
            throw new IllegalArgumentException("회원 이름을 입력해 주세요.");
        }
        String key = normalize(member.getId());
        if (members.containsKey(key)) {
            throw new IllegalArgumentException("이미 등록된 회원 ID입니다: " + member.getId());
        }
        members.put(key, member);
    }

    /** ID로 회원 조회 */
    public Optional<Member> findMember(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(members.get(normalize(id)));
    }

    /** 전체 회원 (등록 순서) */
    public List<Member> getAllMembers() {
        return List.copyOf(members.values());
    }

    private static String normalize(String id) {
        return id.trim().toUpperCase(Locale.ROOT);
    }
}
