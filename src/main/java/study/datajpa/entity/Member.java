package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity @Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id","username","age"})
@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username =:username"
)
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id") //db 테이블은 이제 member_id로 pk를 얘랑 매핑하게 된다.
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id") //외부키 명
    private Team team;

    
//    protected Member (){
//    } //프록시 객체 만들고 지우고 하는 짓 때문에 private 말고 protected
//    이마저도 그냥 NoArgsCons 어노테이션으로 대신 생성
    public Member(String username) {
        this.username = username;
    }


    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if( team!=null){
            changeTeam(team);
        }
    }

    public void changeTeam(Team team){
        this.team=team;
        team.getMembers().add(this);
    }
}
