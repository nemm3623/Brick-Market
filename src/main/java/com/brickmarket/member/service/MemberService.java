package com.brickmarket.member.service;

import com.brickmarket.common.exception.BusinessException;
import com.brickmarket.common.exception.ErrorCode;
import com.brickmarket.member.domain.Member;
import com.brickmarket.member.domain.OAuthProvider;
import com.brickmarket.member.repository.MemberRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberRegistrationService memberRegistrationService;

    public MemberService(MemberRepository memberRepository, MemberRegistrationService memberRegistrationService) {
        this.memberRepository = memberRepository;
        this.memberRegistrationService = memberRegistrationService;
    }

    public Member findOrCreate(OAuthProvider provider, String providerId, String nickname) {
        return memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> createOrFindExisting(provider, providerId, nickname));
    }

    private Member createOrFindExisting(OAuthProvider provider, String providerId, String nickname) {
        try {
            return memberRegistrationService.create(provider, providerId, nickname);
        } catch (DataIntegrityViolationException e) {
            return memberRegistrationService.findExisting(provider, providerId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_OAUTH_CONFLICT));
        }
    }
}
