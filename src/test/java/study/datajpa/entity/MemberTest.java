package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1",10,teamA);
        Member member2 = new Member("member2",30,teamA);
        Member member3 = new Member("member3",40,teamB);
        Member member4 = new Member("member4",50,teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        //초기화
        em.flush();//em.persist를 하면 db에 바로 insert 쿼리를 날리는게 아니라
                   //일단 jpa- em.context (영속성 컨텍스트) 라는 곳에 멤버, team을 모아놓는다.
                   //em.flush를 하면 db에 쿼리를 날린다.
        em.clear();  //db에 쿼리를 날리고
                    // em.context에 있는 캐시를 다 날린다.

        //확인
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        //iter치고 엔터만 쳐도 루프문이 생긴당
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("-> member.team = " + member.getTeam());
        }
    }

    @Test
    public void JpaEventBaseEntity() throws Exception{
        //given
        Member member = new Member("member1");
        memberRepository.save(member); //PrePersist 발생

        Thread.sleep(100);
        member.setUsername("member2");

        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findById(member.getId()).get();

        //then
        System.out.println("findMember CreatedDate = " + findMember.getCreatedDate());
        System.out.println("findMember UpdatedDate = " + findMember.getLastModifiedDate());
        System.out.println("findMember createdBy = " + findMember.getCreatedBy());
        System.out.println("findMember updatedBy = " + findMember.getLastModifiedBy());

    }
}