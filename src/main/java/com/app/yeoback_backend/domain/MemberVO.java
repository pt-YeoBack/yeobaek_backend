package com.app.yeoback_backend.domain;

import com.app.yeoback_backend.audit.Period;
import com.app.yeoback_backend.common.enumeration.Status;
import io.netty.handler.codec.socks.SocksRequestType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
@SuperBuilder
public class MemberVO extends Period {
    private Long id;
    private String memberEmail;
    private String memberPhone;
    private String memberPassword;
    private String memberName;
    private String memberAlias;
    private String memberBio;
    private Status memberStatus;
}
