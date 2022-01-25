package ansan.service;

import ansan.domain.Dto.MemberDto;
import ansan.domain.Entity.Board.BoardEntity;
import ansan.domain.Entity.Member.MemberEntity;
import ansan.domain.Entity.Member.MemberRepository;
import ansan.domain.Entity.Room.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomService {

    @Autowired
    RoomRepository roomRepository ;

    @Autowired
    MemberRepository memberRepository ;

    @Autowired
    RoomimgRepository roomimgRepository ;

    @Autowired
    MemberService memberService;

    @Autowired
    private HttpServletRequest request;



    public boolean roomwrite(RoomEntity roomEntity , List<MultipartFile> files){

        //등록한 회원번호 찾기 [세션에 로그인 정보 ]
        HttpSession session = request.getSession();
        MemberDto memberDto = (MemberDto)session.getAttribute("logindto");
        //화원번호 -> 회원 엔티티 가져오기
        MemberEntity memberEntity = memberService.getmnum(memberDto.getM_num());
        //룸엔티티 회원 엔티티 넣기
        roomEntity.setMemberEntity(memberEntity);
        //방상태
        roomEntity.setRactive("검토중");
        //룸 엔티티 저장후에 룸엔티티 번호 가져오기
        int rnum=roomRepository.save(roomEntity).getRnum();
        //회원 엔티티 룸리스트에 룸엔티티 추가
        RoomEntity roomsaved = roomRepository.findById(rnum).get();
        memberEntity.getRoomEntityList().add(roomsaved);

        //파일처리
        String dir = "C:\\Users\\505\\Desktop\\springseb01\\src\\main\\resources\\static\\roomimg";
        String uuidfile = null;
        //파일사이즈가 0이아니면 존재하면
        if(files.size() != 0){
            //파일 리스트만큼 반복문 돌려서
            for(MultipartFile file : files){
                UUID uuid = UUID.randomUUID();
                //식별난수값 + _ + 파일명 (만약에 파일명에 _가 있으면 - 변환)
                uuidfile = uuid.toString()+"_"+file.getOriginalFilename().replaceAll("_","-");
                String filepath = dir+"\\"+uuidfile;
                try {
                    file.transferTo(new File(filepath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //엔티티에 파일 경로 저장[roo]
                RoomimgEntity roomimgEntity = RoomimgEntity.builder()
                        .rimg(uuidfile)
                        //룸엔티티 넣기
                        .roomEntity(roomsaved)
                        .build();
                //room 엔티티내
                int roomimgnum = roomimgRepository.save(roomimgEntity).getRimgnum();
                roomsaved.getRoomEntitys().add(roomimgRepository.findById(roomimgnum).get());
            }
        }
        return true;
    }

    //모든룸 가져오기
    public List<RoomEntity> getroomlist(){
        return roomRepository.findAll();
    }

    //특정 룸 가져오기
    public RoomEntity getroom(int rnum){
        return roomRepository.findById(rnum).get();
    }

    //룸 삭제
    public boolean deleteroom(int rnum){

        roomRepository.delete(roomRepository.findById(rnum).get());
        return true;
    }
    //룸상태변경
    @Transactional
    public boolean activeupdate(int rnum,String ract){
        RoomEntity roomEntity = roomRepository.findById(rnum).get(); //엔티티호출

        if(roomEntity.getRactive().equals(ract)){
            return  false;
            //현재 동일한 폼 상태
        }else{
            roomEntity.setRactive(ract);
            return true;
        }

    }

    @Transactional
    public boolean updateroom(String rkind,String rval, int rnum){
        RoomEntity roomEntity =roomRepository.findById(rnum).get();
        if(rkind.equals("rmname")){
            roomEntity.setRmname(rval);
        }else if(rkind.equals("rprice")){
            roomEntity.setRprice(rval);
        }else if(rkind.equals("rarea")){
            roomEntity.setRarea(Integer.parseInt(rval));
        }else if(rkind.equals("rmanagementfee")){
            roomEntity.setRmanagementfee(Integer.parseInt(rval));
        }else if(rkind.equals("rstructure")){
            roomEntity.setRstructure(rval);
        }else if(rkind.equals("rfloor")){
            roomEntity.setRfloor(rval);
        }else if(rkind.equals("rkind")){
            roomEntity.setRkind(rval);
        }else if(rkind.equals("rcontents")){
            roomEntity.setRcontents(rval);
        }else if(rkind.equals("rtras")){
            roomEntity.setRtras(rval);
        }
        return true;

    }

    @Autowired
    NoteRepository noteRepository;

    // 문의 등록
    public boolean notewrite( int rnum , String ncontents ) {

        // 로그인된 회원정보를 가져온다. [ 작성자 ]
        HttpSession session = request.getSession();
        MemberDto memberDto =
                (MemberDto)session.getAttribute("logindto");
        // 만약에 로그인이 안되어 있으면
        if( memberDto == null ){ return  false; } // 등록 실패
        // 문의 엔티티 생성
        NoteEntity noteEntity2 = NoteEntity.builder()
                .ncontents(ncontents)
                .memberEntity(memberService.getmnum(memberDto.getM_num()))
                .roomEntity(roomRepository.findById(rnum).get())
        .build();

        // 문의 엔티티 생성
//        NoteEntity noteEntity = new NoteEntity();
//        noteEntity.setNcontents( ncontents ); // 작성내용
//        noteEntity.setMemberEntity(  memberService.getmnum( memberDto.getM_num()) ); // 작성자 엔티티
//        noteEntity.setRoomEntity( roomRepository.findById( rnum ).get() );  // 방 엔티티

        // 문의 엔티티 저장
        int nnum =  noteRepository.save( noteEntity2 ).getNnum(); //
        // 해당 룸엔티티의 문의리스트에 문의엔티티 저장
        roomRepository.findById( rnum ).get().getNoteEntities().add( noteEntity2  );
        // 해당 회원엔티티의 문의리스트에 문의엔티티 저장
        memberService.getmnum( memberDto.getM_num() ).getNoteEntities().add(  noteEntity2  );
        return true; // 등록 성공
    }


    // 로그인 된 회원이 등록한 방 출력
    public List<RoomEntity> getmyroomlist(  ){
        HttpSession session = request.getSession();
        MemberDto memberDto =
                (MemberDto)session.getAttribute("logindto");
        MemberEntity memberEntity =
                memberService.getmnum( memberDto.getM_num() );
        return memberEntity.getRoomEntityList();
    }
    // 로그인 된 회원이 등록한 문의 출력
    public List<NoteEntity> getmynotelist( ){
        HttpSession session = request.getSession();
        MemberDto memberDto =
                (MemberDto)session.getAttribute("logindto");
        MemberEntity memberEntity =
                memberService.getmnum( memberDto.getM_num() );
        return memberEntity.getNoteEntities();
    }



}
