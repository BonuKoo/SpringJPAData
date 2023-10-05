package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
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

    List<Member> findByUsername(@Param("username") String username);// namedQuery를 찾는다

    //실무에서 많이 쓰이는,  Query 정의하기.
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);
    //이 기능의 장점 : 쿼리에 오타 나면 애플리케이션 로딩 하는 과정에서 문법 오류를 잡아준다.
    // 위의 쿼리 자체는, 원래 없는 쿼리를 직접 만든 쿼리이다보니, 쿼리로 변환하는 과정에서 문법에 오류가 있으면 잡아주는 것
    //동적 쿼리는 어떻게 해야 되냐? 동적 쿼리는 Query DSL

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t ") //dto로 조회할 땐 꼭 new operation 이라는 걸 꼭 써야 한다. !
    List<MemberDto> findMemberDto();    // new study.datajpa.dto(m.id, m.username, m.team) 마치 생성자로 new 하는 것처럼 다 적어줘야 한다..
}                                       //JPQL이 제공하는 문법
