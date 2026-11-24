// MemberService - 회원 등록과 조회 (F3)

package com.library.service;

import com.library.model.Member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MemberService {

    private final Map<String, Member> members = new LinkedHashMap<>();   // 키: memberId

    public void registerMember(Member member) {
        if (member == null) throw new IllegalArgumentException("회원이 null입니다.");
        if (members.containsKey(member.getMemberId())) {
            throw new IllegalArgumentException("이미 등록된 회원 ID입니다: " + member.getMemberId());
        }
        members.put(member.getMemberId(), member);
    }

    public Optional<Member> findMember(String memberId) {
        return Optional.ofNullable(members.get(memberId));
    }

    public List<Member> getAllMembers() {
        return Collections.unmodifiableList(new ArrayList<>(members.values()));
    }
}
