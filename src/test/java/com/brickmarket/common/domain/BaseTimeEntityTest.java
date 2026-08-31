package com.brickmarket.common.domain;

import com.brickmarket.common.config.JpaAuditingConfig;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest(excludeAutoConfiguration = JpaRepositoriesAutoConfiguration.class)
@Import(JpaAuditingConfig.class)
@EntityScan(basePackageClasses = BaseTimeEntityTest.class)
class BaseTimeEntityTest {

    private final TestEntityManager entityManager;

    @Autowired
    BaseTimeEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Test
    void keepsCreatedAtAndChangesUpdatedAtWhenEntityIsUpdated() throws InterruptedException {
        AuditTestEntity entity = new AuditTestEntity("최초 이름");
        entityManager.persistAndFlush(entity);
        Instant createdAt = entity.getCreatedAt();
        Instant updatedAt = entity.getUpdatedAt();

        Thread.sleep(10);
        entity.changeName("변경 이름");
        entityManager.flush();
        entityManager.clear();

        AuditTestEntity updated = entityManager.find(AuditTestEntity.class, entity.getId());

        assertThat(updated.getCreatedAt()).isEqualTo(createdAt);
        assertThat(updated.getUpdatedAt()).isAfter(updatedAt);
    }

    @Entity
    @Table(name = "audit_test_entities")
    static class AuditTestEntity extends BaseTimeEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String name;

        protected AuditTestEntity() {
        }

        private AuditTestEntity(String name) {
            this.name = name;
        }

        private Long getId() {
            return id;
        }

        private void changeName(String name) {
            this.name = name;
        }
    }
}
