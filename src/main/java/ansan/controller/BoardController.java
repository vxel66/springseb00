package ansan.controller;

import ansan.domain.Dto.BoardDto;
import ansan.domain.Dto.MemberDto;
import ansan.service.BoardService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;

@Controller
public class BoardController {

    @Autowired
    BoardService boardService;  // boardService 메소드 호출용 객체
    @Autowired  //빈 생성 : 자동 메모리 할당
    HttpServletRequest request;
    // http url 연결
    @GetMapping("/board/boardlist")
    public String boardlist( Model model ){

        ArrayList<BoardDto> boardDtos = boardService.boardlist();
        model.addAttribute( "BoardDtos" , boardDtos  );
        return "board/boardlist" ;  // 타임리프 를 통한 html 반환

    }
    //게시물 쓰기 페이지 이동
    @GetMapping("/board/boardwrite")
    public String boardwrite(){
        return "board/boardwrite";
    }

    //게시물 처리
    @SneakyThrows
    @PostMapping("/board/boardwritecontroller")
    public String boardwritecontroller(@RequestParam("b_img") MultipartFile file){

        //파일처리 [ JSP (COS 라이브러리) ]
        //String dir = "C:\\Users\\505\\Desktop\\springseb01\\src\\main\\resources\\static\\upload";
        String dir = "~/apps/springseb00/src/main/resources/static/upload";
        String filepath = dir + "\\" + file.getOriginalFilename();  //저장 경로 + form에서 첨부한 파일이름 호출
        //file.getOriginalFilename(); : form 첨부파일 호출
        file.transferTo(new File(filepath));       //transferTo : 파일 저장 [ 예외 처리 ]


        //제목 내용 호출
        String b_title =request.getParameter("b_title");
        String b_contetns= request.getParameter("b_contetns");
        BoardDto boardDto = BoardDto.builder()
                .b_title(b_title)
                .b_contetns(b_contetns)
                .b_img(file.getOriginalFilename())
                .build();

        System.out.println(boardDto.toString());
        //세션  선언
        HttpSession session = request.getSession();

        //세션 호출
        MemberDto memberDto= (MemberDto)session.getAttribute("logindto");
        boardDto.setB_writer(memberDto.getM_id());

        boardService.boardwrite( boardDto );
        return "redirect:/board/boardlist"; // 글쓰기 성공시 게시판 목록이동
    }

    //게시물 보기 페이지 이동
    @GetMapping("/board/boardview/{b_num}")//Get 방식으로 url 매핑(연결)
    public String boardview(@PathVariable("b_num")int b_num,Model model){

        BoardDto boardDto= boardService.getboard(b_num);

        model.addAttribute("boardDto",boardDto);

        return "board/boardview";
            //타임리프를 이용한 html 반환+
    }

    //게시물 삭제
    @ResponseBody
    @GetMapping("/board/boarddeletecontroller")
    public int boarddelete(@RequestParam("b_num")int b_num){
        System.out.println("비넘 : "+b_num);
        boolean result =boardService.boarddelete(b_num);
        if(result){
            return 1;
        }else{
            return 2;
        }

    }

    //수정페이지 전환
    @GetMapping("/board/boardupdate/{b_num}")
    public String boardupdate(@PathVariable("b_num")int b_num , Model model){

        model.addAttribute("boardDto",boardService.getboard(b_num));

        return "board/boardupdate";
    }

    //수정
    @PostMapping("/board/boardcontroller")
    public String boardupdatecontroller(BoardDto boardDto){
        System.out.println("@@@@@@@@@@@@@@@@@ 222222222222 : "+boardDto.getB_num());
        boolean result = boardService.update(boardDto);
        return "redirect:/board/boardview/"+boardDto.getB_num();
    }







}
