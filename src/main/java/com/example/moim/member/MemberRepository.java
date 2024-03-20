package com.example.moim.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    @Override
    boolean existsById(String s);

    @Query(value = "select * from MEMBER where email = :email and pw = :pw", nativeQuery = true)
    Member login(String email, String pw);

    // 사용자 검색
    @Query(value = "select nickname, email from MEMBER where nickname = :searchingString", nativeQuery = true)
    List<Member> search(String searchingString);

    @Override
    <S extends Member> S save(S entity);
}
