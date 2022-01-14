package ansan.controller;

import ansan.domain.Dto.BoardDto;
import ansan.domain.Dto.MemberDto;
import ansan.domain.Entity.Board.BoardEntity;
import ansan.service.BoardService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.UUID;

@Controller
public class BoardController {

    @Autowired
    BoardService boardService;  // boardService 메소드 호출용 객체
    @Autowired  //빈 생성 : 자동 메모리 할당
    HttpServletRequest request;
    // http url 연결
    @GetMapping("/board/boardlist")
    public String boardlist(Model model ,@PageableDefault Pageable pageable){
        //검색 서비스
        String keyword = request.getParameter("keyword");
        String serch = request.getParameter("serch");

        HttpSession session =request.getSession();

        if(keyword!=null||serch!=null){
           session.setAttribute("keyword" , keyword);
           session.setAttribute("serch" , serch);
        }else{
            keyword = (String)session.getAttribute("keyword");
            serch = (String)session.getAttribute("serch");
        }
        //ArrayList<BoardDto> boardDtos = boardService.boardlist(pageble);
        Page<BoardEntity> boardDtos = boardService.boardlist(pageable,keyword,serch);

        model.addAttribute( "BoardDtos" , boardDtos  );

        System.out.println( "페이지넘버 : " + boardDtos.getNumber() );

        return "board/boardlist" ;  // 타임리프 를 통한 html 반환
    }
    //게시물 쓰기 페이지 이동
    @GetMapping("/board/boardwrite")
    public String boardwrite(){
        return "board/boardwrite";
    }

    //게시물 처리
    @PostMapping("/board/boardwritecontroller")
    @ResponseBody
    public String boardwritecontroller(@RequestParam("b_img") MultipartFile file){
// /home/ec2-user/apps/springseb00/
        try {
            UUID uuid = UUID.randomUUID(); // 고유 식별자 객체 난수생성 메소드 호출

            //만약에 파일명에 _존재하면 - 변경
            String OriginalFilename = file.getOriginalFilename();
            String uuidfile= uuid.toString()+"_"+OriginalFilename.replaceAll("_","-");
            if(!file.getOriginalFilename().equals("")){
                                    //고유 식별자 _ 파일명
//        //파일처리 [ JSP (COS 라이브러리) ]
            String dir = "C:\\Users\\505\\Desktop\\springseb01\\src\\main\\resources\\static\\upload";
//          String dir ="\\home\\ec2-user\\apps\\springseb00\\src\\main\\resources\\static\\upload";
            String filepath = dir + "\\" + uuidfile;  //저장 경로 + form에서 첨부한 파일이름 호출

            file.transferTo(new File(filepath));       //transferTo : 파일 저장 [ 예외 처리 ]
            }else{
                uuidfile=null;
            }

        HttpSession session = request.getSession();
//        String root_path = session.getServletContext().getRealPath("/");
//        String uploadPath =root_path + "apps/springseb00/src/main/resources/upload/" + file.getOriginalFilename();
//        file.transferTo(new File(uploadPath));

        // 세션 호출
        MemberDto memberDto =  (MemberDto) session.getAttribute( "logindto");

        //제목 내용 호출
        String b_title =request.getParameter("b_title");
        String b_contetns= request.getParameter("b_contetns");
        BoardDto boardDto = BoardDto.builder()
                .b_title(b_title)
                .b_contetns(b_contetns)
                .b_writer(memberDto.getM_id())
                .b_img(uuidfile)
                .build();

        System.out.println(boardDto.toString());

        //세션 호출
        boardDto.setB_writer(memberDto.getM_id());

        boardService.boardwrite( boardDto );
        return "1"; // 글쓰기 성공시 게시판 목록이동
        } catch (IOException e) {
            e.printStackTrace(); return "2";
        }
    }

    @Autowired
    HttpServletResponse response;

    //첨부파일 다운로드 처리
    @GetMapping("/board/filedownload")
    public void firedownload(@RequestParam("b_img")String b_img,HttpServletResponse response) throws IOException {
        System.out.println(b_img);

        String path = "C:\\Users\\505\\Desktop\\springseb01\\src\\main\\resources\\static\\upload\\"+b_img;
        //객체화

        //다운로드
        response.setHeader("Content-Disposition" , "attachment;filename=" + URLEncoder.encode( b_img.split("_")[1],"utf-8") );
        //파일 객체 내보내기
        OutputStream outputStream = response.getOutputStream();
        //내보내기 할 대상 읽어오기
        FileInputStream inputStream = new FileInputStream(path);

        int read = 0;
        byte[] buffer = new byte[1024*1024]; //읽어올 바이트 배열을 저장할 배열
        while((read= inputStream.read(buffer))!=-1){ //파일 읽기[.read]
            //파일 쓰기 [wrtie]
            outputStream.write(buffer, 0,read);
        }


    }


    //게시물 보기 페이지 이동
    @GetMapping("/board/boardview/{b_num}")//Get 방식으로 url 매핑(연결)
    public String boardview(@PathVariable("b_num")int b_num,Model model){

        BoardDto boardDto= boardService.getboard(b_num);
        if(boardDto.getB_img()!=null){
            boardDto.setB_realimg(boardDto.getB_img().split("_")[1]);
        }
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

        BoardDto boardDto = boardService.getboard( b_num );

        // 첨부파일 존재하면 uuid가 제거된 파일명 변환해서 b_realimg 담기
        if( boardDto.getB_img() != null ) boardDto.setB_realimg( boardDto.getB_img().split("_")[1] );

        return "board/boardupdate";
    }

    //수정
    @PostMapping("/board/boardcontroller")
    public String boardupdatecontroller(@RequestParam("b_num") int bnum,
                                        @RequestParam("b_newimg") MultipartFile b_newimg,
                                        @RequestParam("b_title") String b_title,
                                        @RequestParam("b_contetns") String b_contetns,
                                        @RequestParam("b_img") String b_img){

        System.out.println("b_num"+bnum);
        System.out.println("b_newimg"+b_newimg);
        System.out.println("b_contetns"+b_contetns);
        System.out.println("b_title"+b_title);
        System.out.println("b_img"+b_img);

        if(!b_newimg.getOriginalFilename().equals("")){
            try {
            UUID uuid = UUID.randomUUID(); // 고유 식별자 객체 난수생성 메소드 호출
            //만약에 파일명에 _존재하면 - 변경
            String OriginalFilename = b_newimg.getOriginalFilename();
            String uuidfile= uuid.toString()+"_"+OriginalFilename.replaceAll("_","-");
            //고유 식별자 _ 파일명
//        //파일처리 [ JSP (COS 라이브러리) ]
            String dir = "C:\\Users\\505\\Desktop\\springseb01\\src\\main\\resources\\static\\upload";
//          String dir ="\\home\\ec2-user\\apps\\springseb00\\src\\main\\resources\\static\\upload";
            String filepath = dir + "\\" + uuidfile;  //저장 경로 + form에서 첨부한 파일이름 호출
                b_newimg.transferTo(new File(filepath));       //transferTo : 파일 저장 [ 예외 처리 ]
                boardService.update(
                        BoardDto.builder().b_num(bnum).b_title(b_title).b_contetns(b_contetns).b_img( uuidfile ) .build()
                );
            } catch (IOException e) {
                e.printStackTrace();
            }


        }else{
            boardService.update(
                    BoardDto.builder().b_num(bnum).b_title(b_title).b_contetns(b_contetns).b_img( b_img ) .build()  );
        }
        int b_num = 0;
        return "redirect:/board/boardview/"+bnum;

    }







}
