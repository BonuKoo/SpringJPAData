package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id","name"})
public class Team {

    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team") //일대 다   //한 팀에는 member가 여러 명 들어간다. 몇명인지 모르니까 어레이 리스트. 인원 수가 정해져있다면 다른 거로 했겠지?
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}   //mappedBy는 가급적 외부키가 없는 쪽에 거는게 좋다.
