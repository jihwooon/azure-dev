package jpa.core;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import jpa.core.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest

class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testMember() throws Exception {
        Member member = new Member();
        member.setName("memberA");

        Long savedId = memberRepository.save(member);
        Member member1 = memberRepository.find(savedId);

        assertThat(member1.getId()).isEqualTo(member.getId());
        assertThat(member1.getName()).isEqualTo(member.getName());
    }
}
