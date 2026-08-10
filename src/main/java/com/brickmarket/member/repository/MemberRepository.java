package com.brickmarket.member.repository;

import com.brickmarket.member.domain.Member;
import com.brickmarket.member.domain.OAuthProvider;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByProviderAndProviderId(OAuthProvider provider, String providerId);
}
