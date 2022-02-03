package ansan.controller;


import ansan.domain.Dto.MemberDto;
import ansan.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class MemberController {

    @Autowired
    MemberService memberService ;
    @Autowired
    HttpServletRequest request; // 요청 객체    [ jsp : 내장객체(request)와 동일  ]

    // 로그인페이지 연결
    @GetMapping("/member/login")
    public String login(){
        return "member/login";
    }

    // 회원가입페이지 연결
    @GetMapping("/member/signup")
    public String signup(){
        return "member/signup";
    }

    @PostMapping("/member/signupcontroller") // 회원가입 처리 연결
    public String signupcontroller(MemberDto memberDto ,@RequestParam("address1") String address1,
                                   @RequestParam("address2") String address2,
                                   @RequestParam("address3") String address3,
                                   @RequestParam("address4") String address4 ){
        memberDto.setM_address(address1+"/"+address2+"/"+address3+"/"+address4);
     // 자동주입 : form 입력한 name 과 dto의 필드명 동일하면 자동주입 // 입력이 없는 필드는 초기값[ 문자=null , 숫자 = 0 ]
        memberService.membersignup(memberDto);
        return "redirect:/";  // 회원가입 성공시 메인페이지 연결
    }

//    // 로그인처리  [ 스프링시큐리티 사용시 로그인 처리 메소드 제공받기 때문에 사용x ]
//    @PostMapping("/member/logincontroller")
//    @ResponseBody
//    public String logincontroller( @RequestBody MemberDto memberDto ){
//                                             // 폼 사용시에는 자동주입 O
//                                             // AJAX 사용시에는 자동주입 X -> @RequestBody
//          MemberDto loginDto =   memberService.login( memberDto );
//          if( loginDto !=null ){
//              HttpSession session = request.getSession();   // 서버내 세션 가져오기
//              session.setAttribute( "logindto" , loginDto );    // 세션 설정
//              // session.getAttribute("logindto") ; // 세션 호출
//              return "1";
//          }else{
//              return "2";
//          }
//            // 타임리프를 설치했을경우  RETRUN URL , HTML
//            // html 혹은 url 아닌 값 반환할때  @ResponseBody
//    }

//    // 로그아웃 처리
//    @GetMapping("/member/logout")
//    public String logout(){
//        HttpSession session = request.getSession();
//        session.setAttribute( "logindto" , null);   // 기존 세션을 null 로 변경
//        return "redirect:/"; // 로그아웃 성공시 메인페이지로 이동
//    }

    // 회원정보찾기 페이지로 연결
    @GetMapping("/member/findid")
    public String findid(){
        return "member/findid";
    }

    @PostMapping("/member/findidcontroller")
    public String findidcontroller(MemberDto memberDto , Model model){
        String result = memberService.findid(memberDto);
        if( result != null ){
            String msg = " 회원님의 아이디 : " + result ;
            model.addAttribute("findidmsg", msg);
        }else{
            String msg = " 동일한 회원정보가 없습니다." ;
            model.addAttribute("findidmsg", msg);
        }

        return "member/findid";
    }

    @PostMapping("/member/findpasswordcontroller")
    public String findpassword( MemberDto memberDto , Model model ){
        boolean result = memberService.findpassword( memberDto ) ;
        if( result ){
            String msg = " 해당 이메일로 임시비밀번호 발송했습니다." ;
            model.addAttribute("findpwmsg", msg);
        }else{
            String msg = " 동일한 회원정보가 없습니다." ;
            model.addAttribute("findpwmsg", msg);
        }
        return "member/findid";
    }


    
    //아이디 중복체크
    @PostMapping("/member/idcheck")
    @ResponseBody   //클라이언트에게 객체로 반환
    public int idcheck(@RequestParam("m_id") String m_id){
        boolean result = memberService.idcheck(m_id);
        if(result){
            return 1;
        }else{
            return 2;
        }

    }

    //이메일 중복체크
    @PostMapping("/member/emailcheck")
    @ResponseBody
    public int emailcheck(@RequestParam("m_email") String m_email){
        boolean check = memberService.emailcheck(m_email);
        if(check){
            return 1;
        }else{
            return 2;
        }

    }

    //마이페이지 이동
    @GetMapping("/member/info")
    public String myinfo(Model model){
        //1.로그인 세션 호출
        HttpSession session = request.getSession();
        MemberDto loginDto = (MemberDto) session.getAttribute("logindto");
        //2.세션의 회원번호를 서비스에 전달해서 동일한 회원본호에 회원정보 가져오기
        MemberDto memberDto = memberService.getmemberDto(loginDto.getM_num());
        //3.찾은 회원정보를 model 인터페이스를 이용한 view
        model.addAttribute("memberDto",memberDto);
        return "member/myinfo";
    }

    //마이페이지 이동
    @GetMapping("/member/mdelete")
    @ResponseBody
    public String mdelete(@RequestParam String passwordconfirm){

        HttpSession session = request.getSession();
        MemberDto memberDto = (MemberDto) session.getAttribute("logindto");

        boolean result = memberService.delete1(memberDto.getM_num(),passwordconfirm);
        if(result){
            return "1";
        }else{
            return "2";
        }
    }
}











