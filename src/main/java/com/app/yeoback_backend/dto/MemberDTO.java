package com.app.yeoback_backend.dto;

import com.app.yeoback_backend.common.enumeration.Status;
import com.app.yeoback_backend.domain.MemberVO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter @Setter @ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class MemberDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String memberEmail;
    private String memberPhone;
    // 화면에서 받아서 수정만 가능, 넘기는건 불가능
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String memberPassword;
    private String memberName;
    private String memberAlias;
    private String memberBio;
    private Status memberStatus;

    // 프로필사진


    public MemberVO toMemberVO() {
        return MemberVO.builder()
                .id(id)
                .memberEmail(memberEmail)
                .memberPhone(memberPhone)
                .memberPassword(memberPassword)
                .memberName(memberName)
                .memberAlias(memberAlias)
                .memberBio(memberBio)
                .memberStatus(memberStatus)
                .build();
    }
}
