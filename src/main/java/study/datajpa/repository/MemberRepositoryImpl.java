package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

//Impl에는 한가지 규칙이 있다.
//구현 인터페이스 + Impl 이름 규칙
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }   //쿼리 dsl을 쓰기 시작하면 커스텀하기 쉬워진다.


}
