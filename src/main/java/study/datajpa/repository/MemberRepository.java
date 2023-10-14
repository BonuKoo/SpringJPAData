package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long>,MemberRepositoryCustom {

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



    //반환타입 : Page

//    Page<Member> findByAge(int age, Pageable pageable); //

    //Slice를 List로 받을 수 있긴 하다. 이 경우 TotalCount 사용은 불가능
    //Slice<Member> findByAge(int age, Pageable pageable); //

    //@Query(value = "select m from Member m left join m.team t",     //select m from Member m 까지는, 컨텐츠를 가져오는 쿼리
    //        countQuery = "select count(m) from Member m")  // select count(m.username) from Member m 는 카운트를 가져온다.
        // - > 이러면, join이 없기 때문에 , DB에서 Member 카운트를 쉽게 가져올 수 있다. join Team 이 있는 select m from Member m left join m.team t 에서 카운트를 가져올 경우, join team 때문에 쿼리가 복잡해지는데, 그런 문제를 (성능 면에서든, 쓸데없는 쿼리를 안짜도 되는 점에서든) 방지
    Page<Member> findByAge(int age, Pageable pageable);
    //생산성 확대.

    @Modifying(clearAutomatically = true) //Modifying 어노테이션이 있어야, executeUpdate를 실행         //clearAutomatically = true가 있으면, em.clear 를 자동으로 실행해준다.
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);



    //N + 1 문제를 해결하기 위한 fetch join
    @Query("select m from Member m left join fetch m.team") //fetch join을 사용하면, Member를 가져올 때 member와 관련된 team을 한방 쿼리로 다 끌고 온다.
    List<Member> findMemberFetchJoin();

    @Override //JPA 레포지토리에 정의되어 있는 List<T> findAll을 재정의
    @EntityGraph(attributePaths = {"team"}) //JPQL 일일이 짜기 귀찮지? 그럼 이렇게 하자.
    List<Member> findAll();


    //그럼 만약 JPQL도 짜고, fetchJoin도 하고 싶다면?
    //이렇게 하면, Username 가지고 단순 작성했는데도
    //Team fetchJoin이 가능  - findMemberEntityGraph
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();



    //1-1 @NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
    //    작성 전
    //회원 데이터를 쓸 때, team 데이터를 쓸 일이 너무 많으면
    //이런 식으로 - findEntityGraphByUsername
    //@EntityGraph(attributePaths = ("team"))
    //List<Member> findEntityGraphByUsername(@Param("username") String username);

    //1-2
    //    작성 후
    //@EntityGraph(attributePaths = ("team")) /
    @EntityGraph("Member.all")
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value="true"))
    Member findReadOnlyByUsername(String username);

    //select for update
    //DB에서 LOCK을 걸 듯 JPA도 LOCK을 할 수 있다

    @Lock(LockModeType.PESSIMISTIC_WRITE) //JAVAX PERSISTENCE = JPA 패키지
    List<Member> findLockByUsername(String username);
}

