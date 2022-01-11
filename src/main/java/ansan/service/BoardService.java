package ansan.service;


import ansan.domain.Dto.BoardDto;
import ansan.domain.Dto.MemberDto;
import ansan.domain.Entity.Board.BoardEntity;
import ansan.domain.Entity.Board.BoardRepository;
import ansan.domain.Entity.Member.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service    // 필수!!!!!!!
public class BoardService {

    @Autowired
    BoardRepository boardRepository;

    // 글쓰기 메소드
    public void boardwrite( BoardDto boardDto){
        boardRepository.save( boardDto.toentity() );
    }

    // 모든 글출력 메소드
    public ArrayList<BoardDto> boardlist(){

        // 게시물 번호를 정렬해서 엔티티 호출하기
        // SQL : Select * from board order by 필드명 DESC
        // JPA : boardRepository.findAll( Sort.by( Sort.Direction.DESC , "entity 필드명" ) );

        List<BoardEntity> boardEntities
                = boardRepository.findAll( Sort.by( Sort.Direction.DESC , "createdDate" ) ); // 모든 엔티티 호출

        ArrayList<BoardDto> boardDtos = new ArrayList<>(); // 모든 dto 담을 리스트 선언
        for( BoardEntity boardEntity : boardEntities ){ // 모든 엔티티를 반복하면서 하나씩 꺼내오기
            // 엔티티 -> dto 변환

            // 게시물 작성일 날짜형 변환 [ LocalDateTime -> String ]
            // LocalDateTime.format( DateTimeFormatter.ofPattern("yy-MM-dd") ) ;
            String date = boardEntity.getCreatedDate().format( DateTimeFormatter.ofPattern("yy-MM-dd") );
            // 오늘날짜 [ LocalDateTime -> String ]
            String nowdate = LocalDateTime.now().format ( DateTimeFormatter.ofPattern("yy-MM-dd") );
            // 만약에 게시물 작성일이 오늘이면 시간출력 오늘이 아니면 날짜를 출력
            if( date.equals( nowdate ) ){
                date = boardEntity.getCreatedDate().format( DateTimeFormatter.ofPattern("hh:mm:ss") );
            }

            System.out.println(  nowdate );
            BoardDto boardDto = new BoardDto(
                    boardEntity.getB_num() ,
                    boardEntity.getB_title() ,
                    boardEntity.getB_contetns(),
                    boardEntity.getB_writer() ,
                    date ,
                    boardEntity.getB_view() ,
                    boardEntity.getB_img())
                    ;
            boardDtos.add( boardDto ); //  리스트에 저장
        }
        return boardDtos;
    }

    //게시글 view 출력
    public BoardDto getboard(int b_num){

        Optional<BoardEntity> entityOptional = boardRepository.findById(b_num);

        String date = entityOptional.get().getCreatedDate().format(DateTimeFormatter.ofPattern("yy-MM-dd"));

        return BoardDto.builder()
                .b_num(entityOptional.get().getB_num())
                .b_title(entityOptional.get().getB_title())
                .b_contetns(entityOptional.get().getB_contetns())
                .b_writer(entityOptional.get().getB_writer())
                .b_view(entityOptional.get().getB_view())
                .b_img(entityOptional.get().getB_img())
                .b_createdDate(date)
                .build();
    }
    //게시글 삭제
    public boolean boarddelete(int b_num){
        Optional<BoardEntity> entityOptional = boardRepository.findById(b_num);
        if(entityOptional.get().getB_num()==b_num){
            boardRepository.delete(entityOptional.get());
            return true;
        }
        return false;
    }


    @Transactional // 수정중 오류 발생시 rollback : 취소
    public boolean update( BoardDto boardDto ){
        try {
            // 1. 수정할 엔티티 찾는다
            Optional<BoardEntity> entityOptional = boardRepository.findById(boardDto.getB_num());
            // 2. 엔티티를 수정한다
            entityOptional.get().setB_title( boardDto.getB_title());
            entityOptional.get().setB_contetns(boardDto.getB_contetns());

            return true;
        }
        catch ( Exception e ){

            System.out.println( e );
            return false;
        }
    }

}

















