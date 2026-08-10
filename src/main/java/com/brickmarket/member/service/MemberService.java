package com.brickmarket.member.service;

import com.brickmarket.member.domain.Member;
import com.brickmarket.member.domain.OAuthProvider;
import com.brickmarket.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member findOrCreate(OAuthProvider provider, String providerId, String nickname) {
        return memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> memberRepository.save(Member.oauth(provider, providerId, nickname)));
    }
}
