package com.app.yeoback_backend.service;

import com.app.yeoback_backend.aop.annotation.LogStatus;
import com.app.yeoback_backend.dto.MemberDTO;
import com.app.yeoback_backend.repository.MemberDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberDAO memberDAO;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @LogStatus
    public void join(MemberDTO memberDTO) {
        memberDTO.setMemberPassword(passwordEncoder.encode(memberDTO.getMemberPassword()));
        memberDAO.save(memberDTO);
    }

}
