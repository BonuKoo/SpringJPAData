package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;


//    @Autowired MemberQueryRepository memberQueryRepository;
    @Test
    public void testMember(){

        System.out.println("memberRepository = " + memberRepository.getClass());
        // 이 class의 정체는? class com.sun.proxy.$Proxy107    - 프록시 객체 (가짜 객체)
        // 구현 클래스를 Spring이 만들어서 인젝션을 해준거다.
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        findMember1.setUsername("member!!!");

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        Long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }
    @Test
    public void findByUsernameAndAgeGreaterThan() {    
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        List<Member> result =                       
                memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15); //유저네임과 Age가 And 조건으로 묶인다.
                                                                                           //GraterThan : Age보다 크게 //Username은 Member 안에 있는 프로퍼티다. 없는거 넣으면 안통함
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy(){
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery(){
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery(){
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList(){
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        List<String> usernameList = memberRepository.findUsernameList();
    for (String s : usernameList){          //실무에선 assertThat으로 해라.. 이딴 짓하지 말고
        System.out.println(" s = " + s);
    }
    }

    @Test
    public void findMemberDto(){

        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA",10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames(){

        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        //Arrays 를 assertJ 로 불러왔을 땐 안됐는데,
        //Java.util로 불러오니까 성공 ㅅㅂ
        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA","BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);

        }//AAA와 BBB를 IN 절로 가져오는 쿼리
    }

    @Test
    public void returnType(){
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        //List<Member> findListByUsername(String username); //컬렉션
        List<Member> aaa = memberRepository.findListByUsername("AAA");

        //Member findMemberByUsername(String username); //단건
        Member bbb = memberRepository.findMemberByUsername("BBB");
        System.out.println("bbbbbbbbbbbbbbbbbbbbbbbb = " + bbb);

        Optional<Member> optionalByUsername = memberRepository.findOptionalByUsername("AAA");

        //%컬렉션 조회의 경우%
        //파라미터 ("???") 에 해당하는 값이 없을 경우
        //NULL이 아니다.. EMTPY 컬렉션을 반환한다.

        //요는, 리스트는 Null이 아니다. Empty 컬렉션이 반환된다.
        List<Member> result = memberRepository.findListByUsername("asdfjalksddffj");
        System.out.println("result =" + result.size() );

        Member findMember = memberRepository.findMemberByUsername("asdfjalksddffj");
        System.out.println("findMember = " + findMember); //단건인 경우엔 없으면 Null
        //JPL은 결과가 없으면 NullException 이 나오는데,
        //jpa는 그냥 NULL 반환해준다.

        //그런데, JAVA 8부턴 Optional이 타입이 나와서
        Optional<Member> findOptionalMember = memberRepository.findOptionalByUsername("asdfjalksddffj");
        System.out.println("findOptionalMember =" + findOptionalMember.orElseThrow());
        //이 경우 findMember = Optinal.empty 라는 결과가 나온다.

        //만약, AAA에 값이 2개라면 예외가 터진다.
//        Member m1 = new Member("AAA",10);
//        Member m2 = new Member("AAA",20);
//        memberRepository.save(m1);
//        memberRepository.save(m2);
//
//        //List<Member> findListByUsername(String username); //컬렉션
//        List<Member> aaa = memberRepository.findListByUsername("AAA");


    }

    @Test
    public void paging() throws Exception {
        //given
        //MemberJpaRepositoryTest에서 가져온 memberJpaRepository라서 빨간 불이 뜨는데, 이걸 한번에 바꾸는 단축키 shift + F6
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username");// 0페이지부터 3개를 가져온다. 아래에서부터

        //when

//      Page<Member> page = memberRepository.findByAge(age, pageRequest);
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

//      Page에서 Slice로 바꾸려면, MemberRepository 인터페이스에 정의되어 있는 findByAge의 반환타입도 Slice로 바꿔야 한다 !
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest);
        //0번째에서 3개를 가져온다 - > Page와 다르게 slice는 limit에 1개를 더 붙여서, 총 4개를 가져올 것이다. 페이지는 3개, 슬라이스는 4개

        //long totalCount = memberRepository.totalCount(age);
        // 토탈 카운트를 따로 변수 지정 안해도 된다.
        // 반환타입을 Page로 받으면 스프링부트가 page 받고 쿼리 짜고 다 알아서 해준다.

        //페이지 계산 공식 적용...
        // totalPage = totalCount / size ...
        // 마지막 페이지 ...
        // 최초 페이지 ..


        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 모든 엔티티는 그대로 반환하면 안되고, 무조건 DTO로 반환해야 한다 !!!! %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        //DTO로 반환하기 위한 꿀 팁 :: 맵 사용,      map이라는 것은 , 내부의 것을 바꿔서 다른 결과로 낸다.
        //이 객체는 dto로 변환 되었기 때문에 , 반환해도 된다!
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));


        //then
        List<Member> content = page.getContent();

        //Slice가 아닌 Page일 때 가능. slice에는 Total 기능이 없다.
        //long totalElements = page.getTotalElements();//토탈 카운트와 같은 정보를 가져온다.

        //getTotalElements :: 전체 카운트 수가 몇개?

        //getTotalPages :: 전체 페이지 수가 몇개?

        for (Member member : content) {
            System.out.println("members = " + member);
            
        }
        //238번째 줄이 Slice가 아닌 Page일 때 가능. slice에는 Total 기능이 없다.
        //System.out.println("totalElements = " + totalElements);

        //쿼리문을 살펴보면, 내가 특별히 count 를 한 적이 없는데,
        // 스프링 JPA가, 반환타입이 Page라서 페이지를 계산해야하니까 totalCount (count) 쿼리를 날린다.

        assertThat(content.size()).isEqualTo(3);
        //238번째 줄이 Slice가 아닌 Page일 때 가능. slice에는 Total 기능이 없다.
//        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호도 가져올 수 있다 !
        //238번째 줄이 Slice가 아닌 Page일 때 가능. slice에는 Total 기능이 없다.
//      assertThat(page.getTotalElements()).isEqualTo(2); //총 페이지 수 2개
        assertThat(page.isFirst()).isTrue(); //첫 페이지 있냐?
        assertThat(page.hasNext()).isTrue(); //다음 페이지 있냐?

    }
    //Slice는 다 가져오지 않고, 다음 페이지가 있는 지 없는 지에 대해 가져온다.

    //Slice라서 좋은 점이 그래서 뭐냐?
    // 모바일 등 환경에서, Page 1개를  하나 더 있으면 미리 로딩하든가 , 더 보기 기능으로 하나 미리 땡겨놓는 등으로 사용 가능.
    // Page만 있으면 하나 하나 추가 구현해야 할텐데, Slice로 날로 먹기 가능


    //%페이징 쿼리를 실무에서 잘 안쓰려고 하는 이유
    //%totalCount는 , DB에 있는 모든 데이터를 다 끌고 오려고 한다.
    //-> 많은 데이터를 끌고 오다보니, 당연히 속도가 느리다. (성능이 구리다)
    //그래서, totalCount 쿼리는 잘 짜야 한다. (Join 조인이 많이 일어난다고 했을 때.. )
    // 예를 들어, 이 프로젝트의 엔티티는 Member와 Team이 있다.
    // Member 값을 가져올 때, Team 엔티티를 join해서 데이터를 가져와야 한다. 그런데, totalCount를 leftJoin 하면, 개수가 다 똑같다
    // 예를 들어, 멤버와 팀이 있는데 , 멤버를 가져올 때 데이터를 쿼리하는 것은, team 데이터를 Join해서 가져와야 한다.
    //그런데 토탈 쿼리는 left out join으로 가져온다면 , totalCount 할 때는 join 할 필요가 업사
    //데이터 개수는 똑같기 때문에.
    // 이런 경우에는, 카운트 쿼리는 leftJoin 할 필요가 없다. 카운트 쿼리와 원자 쿼리가 다를 수 있는데
    // 카운트 쿼리를 분리하는 방법을 제공한다.


    @Test
    public void bulkUpdate(){
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",19));
        memberRepository.save(new Member("member3",20));
        memberRepository.save(new Member("member4",21));
        memberRepository.save(new Member("member5",40));


        //when
        int resultCount = memberRepository.bulkAgePlus(20);//20살이거나 20살 이상인 사람들은 모두 +1
      //  em.flush(); //flush를 통해 남아있는, 변경되지 않는 값을 DB에 반영
      //  em.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5); // 쿼리 결과 : member5는 41살이 아닌, 40살로 찍혀있다.
        //member5 = Member(id=5, username=member5, age=40)
        //JPA의 영속성 컨텍스트 개념을 살펴보자.
        //JPA를 통해 쿼리를 날리면 우선 1차 캐시에 값이 저장된다. 이 값을 가지고 변경하고 어쩌고 저쩌고 할 것이다.
        //그런데 BULK 연산을 때려버리면, 영속성 컨텍스트 무시하고 바로 DB에 꼴아박는다. -> 그럼? 영속성 컨텍스트는 그걸 모른다.
        //그래서, 벌크연산 때는 이 영속성 컨텍스트를 다 날려버려야 한다. !
        // 처음엔 em.flush;와 em.clear;가 없었는데,
        // 추후에 추가

        //벌크 연산의 주의점
        // 기존의 JPA에서 엔티티 객체를 이용할 땐, db에 바로 때려박는게 아니라 영속성 컨텍스트 차원에서 관리를 했다.
        // 근데 BULK는 DB에 바로 값을 넣는다
        //JPQL을 적으면 (QUERY)
        // .SAVE 등을 한다고 쳤을 때, 영속성 컨텍스트를 DB에 먼저 보내고 (flush를 하고)
        // 그 다음 쿼리문이 실행된다.
        //JPQL을 적으면, 쿼리 먼저 보내고 그 다음 JPQL 실행

        //그런데, bulk 연산은 그와는 무관하게
        // bulk 연산을 해서 DB에는 41살로 들어가있는데, 영속성 컨텍스트는 40살로 남아있는다. -> 그래서 Test에서 em.clear;를 했다

        //-> 그런데 사실, 스프링 JPA 데이터는 em.flush 없이 다른 기능도 제공한다. ( @Modifying ( clear Automatically )

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy(){
        //given
        //member1 -> teamA 참조
        //member2 -> teamB 참조

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member1", 10, teamA);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();
        //영속성 컨텍스트에 있는 캐시 정보를 완전히 DB에 반영하고 , 영속성 컨텍스트를 완전히 날림

        //WHEN     N+1 현상
        //SELECT Member 1 (쿼리를 한번 날렸는데, 그 결과가 2개 )

        //1.1 FetchJoin 전
        //List<Member> members =  memberRepository.findAll(); 

        
       //순수하게 member 객체만 뽑아옴 select Member
        //1.2
        //List<Member> members =  memberRepository.findMemberFetchJoin(); //1-2 fetch Join 후

        //1.3 EntityGraph 작성
        //List<Member> members =  memberRepository.findAll();

        //1.4 EntityGraphByUsername
        List<Member> members =  memberRepository.findEntityGraphByUsername("member1");
        //한번에 2개 조회 할려고 둘 다 member1로 바꿈
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint(){

        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush(); //save를 하면, 영속성 컨텍스트에 넣고, flush를 하면 실제 DB에 값이 들어간다.
        em.clear(); //그리고서 clear를 하면 영속성 컨텍스트를 지운다.

        //when
       Member findMember = memberRepository.findReadOnlyByUsername("member1");
       findMember.setUsername("member2");

       em.flush(); // 변경되지 않는다.

    }

    @Test
    public void lock(){

        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush(); //save를 하면, 영속성 컨텍스트에 넣고, flush를 하면 실제 DB에 값이 들어간다.
        em.clear(); //그리고서 clear를 하면 영속성 컨텍스트를 지운다.

        //when
        memberRepository.findLockByUsername("member1");

    }

    @Test
    public void callCustom(){
        List<Member> result = memberRepository.findMemberCustom();
    }


}