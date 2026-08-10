package com.brickmarket.member;

import com.brickmarket.member.domain.Member;
import com.brickmarket.member.domain.MemberRole;
import com.brickmarket.member.domain.MemberStatus;
import com.brickmarket.member.domain.OAuthProvider;
import com.brickmarket.member.repository.MemberRepository;
import com.brickmarket.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@Import(MemberService.class)
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void createsMemberWhenOAuthIdentityDoesNotExist() {
        Member member = memberService.findOrCreate(OAuthProvider.KAKAO, "12345", "레고수집가");

        assertThat(member.getId()).isNotNull();
        assertThat(member.getProvider()).isEqualTo(OAuthProvider.KAKAO);
        assertThat(member.getProviderId()).isEqualTo("12345");
        assertThat(member.getNickname()).isEqualTo("레고수집가");
        assertThat(member.getRole()).isEqualTo(MemberRole.USER);
        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    void returnsExistingMemberWhenOAuthIdentityAlreadyExists() {
        Member first = memberService.findOrCreate(OAuthProvider.KAKAO, "12345", "첫닉네임");
        Member second = memberService.findOrCreate(OAuthProvider.KAKAO, "12345", "변경닉네임");

        assertThat(second.getId()).isEqualTo(first.getId());
        assertThat(second.getNickname()).isEqualTo("첫닉네임");
        assertThat(memberRepository.count()).isEqualTo(1);
    }
}
