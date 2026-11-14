package com.app.yeoback_backend.mapper;

import com.app.yeoback_backend.domain.MemberVO;
import com.app.yeoback_backend.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface MemberMapper {
    // 회원가입
    public void insert(MemberDTO memberDTO);
    // 로그인
    public Optional<MemberDTO> selectMemberForLogin(MemberVO memberVO);

}
