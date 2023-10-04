package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
    //조회 : find .. By, read...By, query...By, get...By (공식 문서 참조) By: where(조건 문)
    //Count : count...By( 반환타입 'long')
    //EXISTS: exists...By (반환타입 'boolean')
    //삭제 : delete...By, remove...By (반환타입 'long')
    //DISTINCT : findDistinct, findMemberDistinctBy
    //LIMIT : findFirst3, findFirst, findTop, findTop3 //위에서 3개

    List<Member> findTop3HelloBy();

   // List<Member> findByUsername(@Param("username") String username); namedQuery를 찾는다
}
