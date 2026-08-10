package com.brickmarket.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "members",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_member_provider_provider_id",
                columnNames = {"provider", "provider_id"}
        )
)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OAuthProvider provider;

    @Column(name = "provider_id", nullable = false, length = 100)
    private String providerId;

    @Column(nullable = false, length = 40)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status;

    protected Member() {
    }

    private Member(OAuthProvider provider, String providerId, String nickname) {
        this.provider = provider;
        this.providerId = providerId;
        this.nickname = nickname;
        this.role = MemberRole.USER;
        this.status = MemberStatus.ACTIVE;
    }

    public static Member oauth(OAuthProvider provider, String providerId, String nickname) {
        return new Member(provider, providerId, nickname);
    }

    public Long getId() {
        return id;
    }

    public OAuthProvider getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getNickname() {
        return nickname;
    }

    public MemberRole getRole() {
        return role;
    }

    public MemberStatus getStatus() {
        return status;
    }
}
