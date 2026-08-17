package com.brickmarket.common.security;

import com.brickmarket.member.domain.MemberRole;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public final class LoginMember implements OAuth2User {

    private final Long memberId;
    private final MemberRole role;
    private final Map<String, Object> attributes;
    private final Set<GrantedAuthority> authorities;

    public LoginMember(
            Long memberId,
            MemberRole role,
            Map<String, Object> attributes,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.memberId = memberId;
        this.role = role;
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
        this.authorities = Set.copyOf(authorities);
    }

    public Long memberId() {
        return memberId;
    }

    public MemberRole role() {
        return role;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return memberId.toString();
    }
}
