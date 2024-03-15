package com.example.moim.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberDTO, Long> {
    @Override
    <S extends MemberDTO> S save(S entity);

    @Override
    Optional<MemberDTO> findById(Long aLong);

    @Override
    boolean existsById(Long aLong);

}
