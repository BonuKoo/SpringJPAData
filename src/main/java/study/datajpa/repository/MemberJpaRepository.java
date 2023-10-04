package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {
// shift + ctrl + t = Create New Test
    @PersistenceContext //영속성 컨텍스트라고 불리는 엔티티 매니저 호출
    private EntityManager em;

    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public void delete(Member member){
        em.remove(member);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    } //JPQL은 테이블 대상이 아닌 객체를 대상으로 하는 쿼리

    public Optional<Member> findById(Long id){
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member); //null일 수도 아닐 수도 있다
    }

    public Long count(){
        return em.createQuery("select count (m) from Member m", Long.class)
                .getSingleResult();
    }
    public Member find(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username  and m.age > :age")
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }
}
