package ansan.service;


import ansan.domain.Dto.BoardDto;
import ansan.domain.Dto.MemberDto;
import ansan.domain.Entity.Board.BoardEntity;
import ansan.domain.Entity.Board.BoardRepository;
import ansan.domain.Entity.Board.ReplyEntity;
import ansan.domain.Entity.Board.ReplyRepository;
import ansan.domain.Entity.Member.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    //모든 글 출력 페이지처리 o
    public Page<BoardEntity> boardlist(Pageable pageable , String keyword ,String serch){
        // 현재 페이지
        int page = 0;

        if(pageable.getPageNumber()==0) page=0; //0이면 0페이지
        else page = pageable.getPageNumber()-1; //1이면-1 0페이지 2이면-1 1페이지
                                                //페이지는 0페이지부터
        //pageable = PageRequest.of(  0 , 10 );   // 첫번째 페이지 1~10 까지 출력
        //pageable = PageRequest.of(  1 , 10 );   // 두번째 페이지 11~20 까지 출력
        //pageable = PageRequest.of(  2 , 10 );   // 세번째 페이지 21~30 까지 출력
        //페이지 속성 [PageRequest.of(페이지번호,페이지당게시물수,정렬기준)    //sort '_' 인식못함
        pageable = PageRequest.of(  page , 5 ,Sort.by(Sort.Direction.DESC,"bnum"));   //  해당 변수 페이지 에 10 개 출력
        //만약에 검색이 있을 경우
        if(keyword!=null&&keyword.equals("b_title"))return boardRepository.findAlltitle(serch,pageable);
        if(keyword!=null&&keyword.equals("b_contetns"))return boardRepository.findAllcontent(serch,pageable);
        if(keyword!=null&&keyword.equals("b_writer"))return boardRepository.findAllwriter(serch,pageable);

        return boardRepository.findAll( pageable );
    }

    // 모든 글출력 메소드   페이지처리 x
//    public ArrayList<BoardDto> boardlist(){
//
//        // 게시물 번호를 정렬해서 엔티티 호출하기
//        // SQL : Select * from board order by 필드명 DESC
//        // JPA : boardRepository.findAll( Sort.by( Sort.Direction.DESC , "entity 필드명" ) );
//
//        List<BoardEntity> boardEntities
//                = boardRepository.findAll( Sort.by( Sort.Direction.DESC , "createdDate" ) ); // 모든 엔티티 호출
//
//        ArrayList<BoardDto> boardDtos = new ArrayList<>(); // 모든 dto 담을 리스트 선언
//        for( BoardEntity boardEntity : boardEntities ){ // 모든 엔티티를 반복하면서 하나씩 꺼내오기
//            // 엔티티 -> dto 변환
//
//            // 게시물 작성일 날짜형 변환 [ LocalDateTime -> String ]
//            // LocalDateTime.format( DateTimeFormatter.ofPattern("yy-MM-dd") ) ;
//            String date = boardEntity.getCreatedDate().format( DateTimeFormatter.ofPattern("yy-MM-dd") );
//            // 오늘날짜 [ LocalDateTime -> String ]
//            String nowdate = LocalDateTime.now().format ( DateTimeFormatter.ofPattern("yy-MM-dd") );
//            // 만약에 게시물 작성일이 오늘이면 시간출력 오늘이 아니면 날짜를 출력
//            if( date.equals( nowdate ) ){
//                date = boardEntity.getCreatedDate().format( DateTimeFormatter.ofPattern("hh:mm:ss") );
//            }
//
//            System.out.println(  nowdate );
//            BoardDto boardDto = new BoardDto(
//                    boardEntity.getB_num() ,
//                    boardEntity.getB_title() ,
//                    boardEntity.getB_contetns(),
//                    boardEntity.getB_writer() ,
//                    date ,
//                    boardEntity.getB_view() ,
//                    boardEntity.getB_img(),
//                    null)
//                    ;
//            boardDtos.add( boardDto ); //  리스트에 저장
//        }
//        return boardDtos;
//    }
    @Autowired
    HttpServletRequest request;


    //게시글 view 출력
    @Transactional
    public BoardDto getboard(int b_num){

        Optional<BoardEntity> entityOptional = boardRepository.findById(b_num);

        String date = entityOptional.get().getCreatedDate().format(DateTimeFormatter.ofPattern("yy-MM-dd"));

        //세션이용한 조회수 중복 방지
        HttpSession session = request.getSession();
        if(session.getAttribute(b_num+"")==null){
            //조회수 변경
            entityOptional.get().setB_view(entityOptional.get().getB_view()+1);
            //세션 부여
            session.setAttribute(String.valueOf(b_num),1);
            //해당 세션 시간
            session.setMaxInactiveInterval(60*60*24);
        }
        return BoardDto.builder()
                .b_num(entityOptional.get().getBnum())
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
        if(entityOptional.get().getBnum()==b_num){
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
            entityOptional.get().setB_img(boardDto.getB_img());
            return true;
        }
        catch ( Exception e ){

            System.out.println( e );
            return false;
        }
    }

    @Autowired
    private ReplyRepository repository ;

    //댓글등록
    public boolean replywrite(int bnum, String rcontents , String rwriter){

        Optional<BoardEntity> entityOptional = boardRepository.findById(bnum); //게시물번호에 해당하는 게시물 엔티티 출력

        ReplyEntity replyEntity = ReplyEntity.builder()
                .rcontents(rcontents)
                .rwriter(rwriter)
                .boardEntity(entityOptional.get())//해당 게시물의 엔티티 넣기
                .build();

        repository.save(replyEntity);   //게시물 -> 댓글저장

        //해당 게시물내 댓글 저장 [ 댓글 -> 게시글 저장]
        entityOptional.get().getReplyEntities().add(replyEntity);

        return true;
    }

    //모든 댓글 출력
    public List<ReplyEntity> getreplylist(int bnum){

        Optional<BoardEntity> entityOptional = boardRepository.findById(bnum);

        List<ReplyEntity> replyEntities = entityOptional.get().getReplyEntities();

        return replyEntities;

    }

    //댓글 삭제
    public boolean deletereply(int rnum){
        Optional<ReplyEntity> replyEntity = repository.findById(rnum);
        if(replyEntity.get().getRnum()==rnum){
            repository.delete(replyEntity.get());
        }
        return true;
    }





}

















