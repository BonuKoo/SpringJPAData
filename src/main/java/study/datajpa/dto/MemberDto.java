package study.datajpa.dto;

import lombok.Data;

@Data   //엔티티면 Setter 때문에 memberDto 쓰면 안되겠지만, DTO니까..
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }
}
