package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    @Value("#{target.username + '' + target.age}")
    String getUsername();
}

//이러면 username과 age를 둘 다 가져온다
//오픈 프로젝션