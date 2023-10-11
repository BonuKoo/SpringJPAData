package study.datajpa.repository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

//import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false) //얘가 있어서 Test가 실패했다면? , 상관없다. 그래도 실패한다.
class MemberJpaRepositoryTest {
    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        //org.assertj.core.api를 불러야 한다.
        //Assertions에 커서 올려놓고 alt+enter 누르면 스태틱으로 만들 수 있지만 메모를 위해..
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); //같은 인스턴스인게 보장. 1차 캐시

    }//ctrl + alt + v 단축키 개꿀

    //단건 조회 검증
    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        findMember1.setUsername("member!!!");

    //리스트 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        Long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

    //삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long deletedCount = memberJpaRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);
        List<Member> result =                       //리스트에 안담고 뻘짓중이었다..
                memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void paging() throws Exception{ //throws Exception 이 있느냐 없느냐에 따라

        //given
        memberJpaRepository.save(new Member("member1",10));
        memberJpaRepository.save(new Member("member2",10));
        memberJpaRepository.save(new Member("member3",10));
        memberJpaRepository.save(new Member("member4",10));
        memberJpaRepository.save(new Member("member5",10));

        int age = 10;
        int offset = 0;
        int limit = 3;
        //when
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit); //페이징 된 컨텐츠를 가져오고서
        long totalCount = memberJpaRepository.totalCount(age);                     //토탈 카운트를 가져온다.

        //페이지 계산 공식 적용..
        // totalPage = totalCount //size..
        // 마지막 페이지
        // 최초 페이지..

        //then
        assertThat(members.size()).isEqualTo(3); //offset이 0이고, limit이 3이기 때문에 member1, member2, member3 이 뽑힌다.
        assertThat(totalCount).isEqualTo(5);    //총 개수는 member1, member2~ member5 총 5개. 5가 뽑힌다.


    }

    @Test
    public void bulkUpdate(){
        //given
        memberJpaRepository.save(new Member("member1",10));
        memberJpaRepository.save(new Member("member2",19));
        memberJpaRepository.save(new Member("member3",20));
        memberJpaRepository.save(new Member("member4",21));
        memberJpaRepository.save(new Member("member5",40));


        //when
        int resultCount = memberJpaRepository.bulkAgePlus(20);//20살이거나 20살 이상인 사람들은 모두 +1

        //then
        assertThat(resultCount).isEqualTo(3);
    }
}
