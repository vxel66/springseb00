package ansan.domain.Entity.Board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

public interface BoardRepository extends JpaRepository<BoardEntity , Integer> {
    //JPA 메소드 만들기
    //네이티브쿼리 = 실제 sql
    @Query(nativeQuery=true,value="select * from board where b_title like %:serch%")
    Page<BoardEntity> findAlltitle(@Param("serch")String serch , Pageable pageable);

    @Query(nativeQuery=true,value="select * from board where b_contetns like %:serch%")
    Page<BoardEntity> findAllcontent(@Param("serch")String serch , Pageable pageable);

    @Query(nativeQuery=true,value="select * from board where b_writer like %:serch%")
    Page<BoardEntity> findAllwriter(@Param("serch")String serch , Pageable pageable);

}
