package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

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

}