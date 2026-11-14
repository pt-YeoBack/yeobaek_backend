package com.app.yeoback_backend.repository;

import com.app.yeoback_backend.domain.MemberVO;
import com.app.yeoback_backend.dto.MemberDTO;
import com.app.yeoback_backend.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberDAO {
    private final MemberMapper memberMapper;

    // 회원가입
    public void save(MemberDTO memberDTO) {
        memberMapper.insert(memberDTO);
    }
    // 로그인
    public Optional<MemberDTO> findMemberForLogin(MemberVO memberVO) {
        return memberMapper.selectMemberForLogin(memberVO);
    }
}
