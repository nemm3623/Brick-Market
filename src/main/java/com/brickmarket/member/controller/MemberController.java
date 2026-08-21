package com.brickmarket.member.controller;

import com.brickmarket.common.ApiResponse;
import com.brickmarket.common.security.LoginMember;
import com.brickmarket.member.domain.Member;
import com.brickmarket.member.dto.MemberResponse;
import com.brickmarket.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ApiResponse<MemberResponse> getMe(
            @AuthenticationPrincipal LoginMember loginMember
    ) {
        Member member = memberService.findById(loginMember.memberId());
        return ApiResponse.success(MemberResponse.from(member));
    }
}
