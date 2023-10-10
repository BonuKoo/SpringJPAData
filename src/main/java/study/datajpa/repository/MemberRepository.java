package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    //JPQL이 제공하는 문법

    //파라미터 바인딩
    // 1. 위치 기반
    // 2. 이름 기반

    //select m from Member m where m.username = ?0 //위치 기반
    //select m from Member m where m.username = :name //이름 기반

    //코드 가독성 및 유지보수를 위해 가급적 이름 기반을 사용하자.
    //위치 기반은 위치가 바뀌어버렸을 때 버그가 발생할 수 있다.

    //위치 기반의 예시

    @Query("select m from Member m where m.username = :name")
    Member findMembers(@Param("name") String username);


    //컬렉션 파라미터 바인딩
    //Collections 타입으로 in 절 지원
    //in 절로 여러개를 조회하고 싶을 때 사용하는 기능
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);



    //반환타입 1
    List<Member> findListByUsername(String username); //컬렉션

    //반환타입 2
    Member findMemberByUsername(String username); //단건

    //반환타입 3
    Optional<Member> findOptionalByUsername(String username); //단건 Optional boolean Type




}

