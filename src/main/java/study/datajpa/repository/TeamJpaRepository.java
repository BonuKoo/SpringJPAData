package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class TeamJpaRepository {
    @PersistenceContext //Jpa의 엔티티 매니저를 인젝션 해주는 어노테이션
    private EntityManager em;

    public Team save(Team team){
        em.persist(team);
        return team;
    }

    public void delete(Team team){
        em.remove(team);
    }

    public List<Team> findAll(){
        return em.createQuery("select t from Team t", Team.class)
                .getResultList();
    }

    public Optional<Team> findById(Long id){
        Team team = em.find(Team.class, id);
        return Optional.ofNullable(team); //null일 수도 아닐 수도 있다
    }

    public long count(){
        return em.createQuery("select count(T) from Team t", Long.class)
                .getSingleResult();
    }

}
