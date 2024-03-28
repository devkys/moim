package com.example.moim.member;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    // 이메일 중복 확인
    @Override
    boolean existsById(String s);

    // 로그인
    @Query(value = "select * from member where email = :email and pw = :pw", nativeQuery = true)
    Optional<Member> login(String email, String pw);

    // 사용자 검색
    @Query(value = "select nickname, email from member where nickname = :searchingString", nativeQuery = true)
    List<Member> search(String searchingString);

    // 회원 가입
    @Override
    <S extends Member> S save(S entity);

    // 채팅방 아이디로 채팅방 유저 조회
    @Query(value = "select * from member where email in (select email from room where sch_number = :roomId)", nativeQuery = true)
    List<Member> invitedUser(Long roomId);

    @Modifying
    @Transactional
    @Query(value = "update member set refresh_token=:refreshToken where email =:email", nativeQuery = true)
    void update(String refreshToken, String email);

    @Query(value = "select refresh_token from member where email=:email", nativeQuery = true)
    String getFreshToken(String email);

}
