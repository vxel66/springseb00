package ansan.domain.Entity.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity , Integer> {
    //필드 검색 findby필드명
    @Query
    Optional<MemberEntity> findByMid(String mid);

    @Query
    Optional<MemberEntity> findByMemail(String memail);

}
