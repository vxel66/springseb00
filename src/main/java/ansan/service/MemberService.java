package ansan.service;

import ansan.domain.Dto.IntergratedDto;
import ansan.domain.Dto.MemberDto;
import ansan.domain.Entity.Member.MemberEntity;
import ansan.domain.Entity.Member.MemberRepository;
import ansan.domain.Entity.Room.NoteEntity;
import ansan.domain.Entity.Room.RoomEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class MemberService implements UserDetailsService {
                                    //로그인 [ 인증 ] 절차 지원 인터페이스

    @Autowired
    MemberRepository memberRepository ;

    // 회원등록 메소드
    public boolean membersignup ( MemberDto memberDto ){

        //패스워드 암호화 [ BCryptPasswordEncoder ]
        //1.암호화 클래스 객체 생성
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        //2.입력받은 memberDto에 패스워드 재설정 [ 암호화객체명.encode (입력받은 패스워드)]
        memberDto.setM_password(passwordEncoder.encode(memberDto.getM_password()));

        memberRepository.save( memberDto.toentity()  );  // save(entity) : insert / update :  Entity를 DB에 저장
        return true;
    }
    // 회원 로그인 메소드 [ 스프링시큐리티 사용시 로그인 처리 메소드 제공받기 때문에 사용x ]
//    public MemberDto login(MemberDto memberDto ){
//        List<MemberEntity>  memberEntityList =  memberRepository.findAll();
//        System.out.println(memberDto.getM_id());
//        System.out.println(memberDto.getM_password());
//        for( MemberEntity memberEntity :  memberEntityList ){
//            if( memberEntity.getMid().equals( memberDto.getM_id()) &&
//                memberEntity.getM_password().equals(memberDto.getM_password())){
//                return MemberDto.builder()
//                        .m_id( memberEntity.getMid() )
//                        .m_num( memberEntity.getM_num() ) .build();
//            }
//        }
//        return null;
//    }

    // 회원 아이디 찾기
    public String findid( MemberDto memberDto ){
        // 1. 모든 엔티티 호출
        List<MemberEntity> memberEntities = memberRepository.findAll();
        // 2. 반복문 이용한 모든 엔티티를 하나씩 꺼내보기
        for( MemberEntity memberEntity  :  memberEntities){
            // 3. 만약에 해당 엔티티가 이름과 이메일이 동일하면
            if( memberEntity.getM_name().equals(memberDto.getM_name()) &&
                memberEntity.getMemail().equals( memberDto.getM_email() )){
                // 4. 아이디를 반환한다
                return memberEntity.getMid();
            }
        }
        // 5. 만약에 동일한 정보가 없으면
        return null;
    }

    @Autowired
    private JavaMailSender javaMailSender;  //  자바메일 객체

    // 회원 비밀번호 찾기 -> 메일 전송 [ 임시비밀번호 ]
    // 회원 비밀번호 찾기 -> 메일 전송 [ 임시비밀번호 ]
    @Transactional
    public boolean findpassword( MemberDto memberDto ) {

        List<MemberEntity> memberEntities = memberRepository.findAll();

        for( MemberEntity memberEntity  :  memberEntities) {
            if(memberEntity.getMemail().equals(memberDto.getM_email())){
            StringBuilder body = new StringBuilder();   // StringBuilder  : 문자열 연결 클래스  [ 문자열1+문자열2 ]
            body.append("<html> <body><h1> Ansan 계정 임시 비밀번호 </h1>");    // 보내는 메시지에 html 추가

            Random random = new Random();
            // 임시 비밀번호 만들기
            StringBuilder temppassword = new StringBuilder();
            for (int i = 0; i < 12; i++) {  // 12 자리 만들기
                // 랜덤 숫자 -> 문자 변환
                temppassword.append((char) ((int) (random.nextInt(26)) + 97));
            }
            body.append("<div>" + temppassword + "</div></html>");      // 보내는 메시지에 임시비밀번호를 html에 추가

            // !!!엔티티내 패스워드 변경

                memberEntity.setM_password(temppassword.toString());     //JPA


            try {
                // Mime : 전자우편 포멧 프로토콜[통신 규약]
                // SMTP : 전자우편 전송 프로토콜 [ 통신 규약 ]
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "utf-8");
                mimeMessageHelper.setFrom("alsdnr5341@naver.com", "Ansan");      // 보내는사람  //  이름
                mimeMessageHelper.setTo(memberDto.getM_email());                                                 //  받는사람
                mimeMessageHelper.setSubject("Ansan 계정 임시 비밀번호 발송 ");                      // 메일 제목
                mimeMessageHelper.setText(body.toString(), true);                                    // 메일 내용    // html 형식유무
                javaMailSender.send(message);     // 메일 전송

                return true;
            } catch (Exception e) {
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                System.out.println("메일전송 실패 " + e);
            }
        }
        }
        return false;
    }

    //회원아이디 중복체크
    public boolean idcheck(String m_id){
        //1.모든 엔티티 가져오기
        List<MemberEntity> memberEntities =memberRepository.findAll();
        //2.모든엔티티 반복문 돌려서 엔티티 하나씩 가져오기
        for(MemberEntity memberEntity : memberEntities){
            //3.해당아이디가 입력한 아이디와 동일하면
            if(memberEntity.getMid().equals(m_id)){
                
                return true; //중복
            }
        }return false;  //중복 없음
    }

    //이메일 중복체크
    public boolean emailcheck(String m_email){
        List<MemberEntity> members = memberRepository.findAll();
        for(MemberEntity memberEntity : members){
            if(memberEntity.getMemail().equals(m_email)){
                return true;
            }
        }
        return false;
    }

    //회원번호 -> 회원정보 반환
    public MemberDto getmemberDto(int m_num){

        //memberRepository.findfindById(pk값) : 해당 pk값의 엔티티 호출
        Optional<MemberEntity> memberEntity = memberRepository.findById(m_num);
        //2.찾은 entity를 dto 변경후 반환
        return MemberDto.builder()
                .m_id(memberEntity.get().getMid())
                .m_name(memberEntity.get().getM_name())
                .m_address(memberEntity.get().getM_address())
                .m_email(memberEntity.get().getMemail())
                .m_grade(memberEntity.get().getM_grade())
                .m_phone(memberEntity.get().getM_phone())
                .m_point(memberEntity.get().getM_point())
                .m_sex(memberEntity.get().getM_sex())
                .m_createdDate(memberEntity.get().getCreatedDate())
                .build();
    }

    public boolean delete1(int m_num, String m_password){
        Optional<MemberEntity> memberEntity = memberRepository.findById(m_num);
        if(memberEntity.get().getM_password().equals(m_password)){
            memberRepository.delete(memberEntity.get());
            return true;
        }
        return false;
    }

    //회원번호 -> 회원엔티티 반환
    public MemberEntity getmnum(int m_num){
        Optional<MemberEntity> memberEntity =memberRepository.findById(m_num);
        return  memberEntity.get();
    }

    @Autowired
    private HttpServletRequest request;

    @Override   //member/logincontroller url 호출시 실행되는 메소드 [로그인처리(인증처리) 메소드]
    public UserDetails loadUserByUsername(String mid) throws UsernameNotFoundException {

        Optional<MemberEntity> entityOptional = memberRepository.findByMid(mid);
        MemberEntity memberEntity = entityOptional.orElse(null);
                                    // .orElse(null) : 만약에 엔티티가 없으면 null
        //찾은 회원엔티티의 권한을 리스트에 담기
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(memberEntity.getRolekey()));

        MemberDto loginDto = MemberDto.builder().m_id(memberEntity.getMid()).m_num(memberEntity.getM_num()).build();
        HttpSession session = request.getSession();
        session.setAttribute( "logindto" , loginDto );
        //회원정보와 권한을 갖는UserDetail 반환
        return new IntergratedDto(memberEntity,authorities);
    }
}
















