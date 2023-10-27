package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
       Member member = memberRepository.findById(id).get();
       return member.getUsername();
    }

    
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member){
        return member.getUsername();
    }// 스프링이 중간에서
    //Member member = memberRepository.findById(id).get(); 이렇게 컨버팅 하는 과정을 대신 해줌

    @GetMapping("/members") //디폴트로 20개까지 출력. 바꾸고 싶다면? application.yml로 가서 설정 = (1)
    public Page<MemberDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable){

        PageRequest request = PageRequest.of(1,2);
        Page<MemberDto> map = memberRepository.findAll(pageable)
                .map(MemberDto::new);
        return map;
    } //실무에선 거의 인라인 (Ctrl + Alt + N)
      //거의 이대로 쓰면 되지 않을까?

    //1.
    //스프링 데이터는 Page를 0부터 시작한다. 근데 1부터 시작하려면 이렇게 하면 된다.
    //Pageable, Page를 파라미터와 응답 값으로 사용하지 않고,
    //직접 클래스를 만들어서 처리한다.
    // 그리고 직접 Page Request(Pageable 구현체)를 생성해서 리포지토리에 넘긴다.
    //물론 응답값도 page 대신에 직접 만들어서 제공해야 한다.

    //2.
    //spring.data.web.pageable.one-indexed-parameters 를 true로 설정한다.
    // 그런데 이 방법은 web에서 page 파라미터를 -1 처리 할 뿐이다.
    // 따라서 응답값인 Page에 모두 0페이지 인덱스를 사용하는 한계가 있다.

//    @PostConstruct
//    public void init(){
//       for (int i = 0; i< 100; i++){
//           memberRepository.save(new Member("user" + i , i));
//       }
//    }
}
