package com.app.yeoback_backend.member;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** SQL은 resources/mapper/MemberMapper.xml 에 있다. */
@Mapper
public interface MemberMapper {

    /** 이메일로 회원 조회 (소문자로 정규화한 값을 넘긴다) */
    MemberAuth findByEmail(@Param("email") String email);

    /** 휴대폰 번호로 회원 조회 (숫자만 남긴 값을 넘긴다) */
    MemberAuth findByPhone(@Param("phone") String phone);

    MemberAuth findById(@Param("id") Long id);
}
