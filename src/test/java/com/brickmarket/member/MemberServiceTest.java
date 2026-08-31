package com.brickmarket.member;

import com.brickmarket.common.exception.BusinessException;
import com.brickmarket.common.exception.ErrorCode;
import com.brickmarket.member.domain.Member;
import com.brickmarket.member.domain.MemberRole;
import com.brickmarket.member.domain.MemberStatus;
import com.brickmarket.member.domain.OAuthProvider;
import com.brickmarket.member.repository.MemberRepository;
import com.brickmarket.member.service.MemberService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    @Test
    void createsMemberWhenOAuthIdentityDoesNotExist() {
        Member member = memberService.findOrCreate(OAuthProvider.KAKAO, "12345", "레고수집가");

        assertThat(member.getId()).isNotNull();
        assertThat(member.getProvider()).isEqualTo(OAuthProvider.KAKAO);
        assertThat(member.getProviderId()).isEqualTo("12345");
        assertThat(member.getNickname()).isEqualTo("레고수집가");
        assertThat(member.getRole()).isEqualTo(MemberRole.USER);
        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        assertThat(member.getCreatedAt()).isNotNull();
        assertThat(member.getUpdatedAt()).isNotNull();
    }

    @Test
    void findsMemberById() {
        Member saved = memberService.findOrCreate(OAuthProvider.KAKAO, "12345", "레고수집가");

        Member found = memberService.findById(saved.getId());

        assertThat(found.getId()).isEqualTo(saved.getId());
    }

    @Test
    void rejectsMissingMemberId() {
        assertThatThrownBy(() -> memberService.findById(999L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    void rejectsNullMemberId() {
        assertThatThrownBy(() -> memberService.findById(null))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    void returnsExistingMemberWhenOAuthIdentityAlreadyExists() {
        Member first = memberService.findOrCreate(OAuthProvider.KAKAO, "12345", "첫닉네임");
        Member second = memberService.findOrCreate(OAuthProvider.KAKAO, "12345", "변경닉네임");

        assertThat(second.getId()).isEqualTo(first.getId());
        assertThat(second.getNickname()).isEqualTo("첫닉네임");
        assertThat(memberRepository.count()).isEqualTo(1);
    }

    @Test
    void returnsSameMemberWhenOAuthRequestsArriveConcurrently() throws Exception {
        int requestCount = 8;
        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);
        CountDownLatch ready = new CountDownLatch(requestCount);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Member>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < requestCount; i++) {
                futures.add(executorService.submit(() -> {
                    ready.countDown();
                    start.await();
                    return memberService.findOrCreate(OAuthProvider.KAKAO, "concurrent-12345", "동시로그인");
                }));
            }

            assertThat(ready.await(3, TimeUnit.SECONDS)).isTrue();
            start.countDown();

            List<Member> members = new ArrayList<>();
            for (Future<Member> future : futures) {
                members.add(future.get(3, TimeUnit.SECONDS));
            }

            Long memberId = members.get(0).getId();
            assertThat(members).extracting(Member::getId).containsOnly(memberId);
            assertThat(memberRepository.count()).isEqualTo(1);
        } finally {
            executorService.shutdownNow();
        }
    }
}
