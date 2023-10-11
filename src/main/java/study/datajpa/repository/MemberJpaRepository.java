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
        return em.createQuery("select count (m) from Member m where m.age =:age", Long.class )
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



        /*
        - 검색 조건 : 나이가 10살
        - 정렬 조건: 이름으로 내림차순
        - 페이징 조건 : 첫 번째 페이지, 페이지당 보여줄 데이터는 3건
       */


    public List<Member> findByPage(int age, int offset, int limit){
       return em.createQuery("select m from Member m where m.age =:age order by m.username desc")
                .setParameter("age", age)
                .setFirstResult(offset) //몇 번째 부터?
                .setMaxResults(limit)        //개수를 몇 개 가져올건가
                .getResultList();

    } //페이징 처리를 했으니, 이게 몇 번째 페이지인지를 나타내줘야 한다.

    public long totalCount(int age){
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult(); //count이니까 단 건 (한 개) -> SingleResult

    }

    public int bulkAgePlus(int age){
        return em.createQuery("update Member m set m.age = m.age + 1" +
                   "where m.age >= :age")
           .setParameter("age", age)
           .executeUpdate(); //ctrl + alt + N 으로 깔끔하게 리팩토링 , Inline Variable
    } //파라미터로 넘어온 Age 값과 나이가 같거나 이상인 사람들의 age 값을 1씩 증가


}
