package com.brickmarket.member.dto;

import com.brickmarket.member.domain.Member;
import com.brickmarket.member.domain.MemberRole;
import com.brickmarket.member.domain.MemberStatus;
import com.brickmarket.member.domain.OAuthProvider;

public record MemberResponse(
        Long id,
        OAuthProvider provider,
        String nickname,
        MemberRole role,
        MemberStatus status
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getProvider(),
                member.getNickname(),
                member.getRole(),
                member.getStatus()
        );
    }
}
