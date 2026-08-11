package com.brickmarket.member.service;

import com.brickmarket.member.domain.Member;
import com.brickmarket.member.domain.OAuthProvider;
import com.brickmarket.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberRegistrationService {

    private final MemberRepository memberRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Member create(OAuthProvider provider, String providerId, String nickname) {
        return memberRepository.saveAndFlush(Member.oauth(provider, providerId, nickname));
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Optional<Member> findExisting(OAuthProvider provider, String providerId) {
        return memberRepository.findByProviderAndProviderId(provider, providerId);
    }
}
