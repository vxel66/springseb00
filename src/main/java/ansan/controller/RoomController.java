package ansan.controller;

import ansan.domain.Dto.MemberDto;
import ansan.domain.Entity.Member.MemberEntity;
import ansan.domain.Entity.Room.NoteEntity;
import ansan.domain.Entity.Room.NoteRepository;
import ansan.domain.Entity.Room.RoomEntity;
import ansan.domain.Entity.Room.RoomRepository;
import ansan.service.MemberService;
import ansan.service.RoomService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller // view <---매핑---> Controller [매핑]
@RequestMapping("/room")
public class RoomController {

    @Autowired
    RoomService roomService;

    @Autowired  //빈 생성 : 자동 메모리 할당
    HttpServletRequest request;

    @GetMapping("/roomwrite")
    private String write() {
        return "room/roomwrite"; //타임리프 반환 [앞에 / 제거]
    }

    @PostMapping("/roomwritecontroller")
    public String writecontroller(RoomEntity roomEntity,
                                  @RequestParam("file") List<MultipartFile> files,
                                  @RequestParam("addressy") Double addressy,
                                  @RequestParam("addressx") Double addressx) {
        roomEntity.setRaddress(roomEntity.getRaddress() + "," + addressy + "," + addressx);

        roomService.roomwrite(roomEntity, files);

        return "main";
    }
    @GetMapping("/roomview")
    public String roomview(){
        return"room/roomview";
    }

    //json 반환 [지도에
    @GetMapping("/chicken.json")
    @ResponseBody
    public JSONObject chicken(){
        //Map <--> Json [키 : 값 ] => 엔트리
            // 중첩이 가능하다.
        JSONObject jsonObject =new JSONObject();   //json [응답 용]
        JSONArray jsonArray = new JSONArray(); //json 안에 들어가는 리스트

        List<RoomEntity> getroomlist =roomService.getroomlist(); //모든 방 [위도,경도 포함]

        for(RoomEntity roomEntity : getroomlist){
            JSONObject data = new JSONObject(); // 리스트 안에 들어가는 키:값
            data.put("lat",roomEntity.getRaddress().split(",")[1]); // 주소[0],위도[1],경도[2]
            data.put("lng",roomEntity.getRaddress().split(",")[2]);
            data.put("rnum",roomEntity.getRnum());
            data.put("ract",roomEntity.getRactive());
            jsonArray.add(data);
        }

        jsonObject.put("positions",jsonArray); //json 전체에 리스트 넣기

        return jsonObject;
    }

    //방번호를 이용한 방정보 html 반환
    @GetMapping("/getroom")
    public String getroom(@RequestParam("rnum")int rnum, Model model){
        model.addAttribute("room",roomService.getroom(rnum));
        return "room/room";//room html 반환
    }

    // 문의 등록
    @GetMapping("/notewrite")
    @ResponseBody
    public String notewrite ( @RequestParam("rnum") int rnum ,
                              @RequestParam("ncontents") String ncontents ) {
        System.out.println(rnum);
        System.out.println(ncontents);
        boolean result = roomService.notewrite( rnum , ncontents );
        if( result ){ return "1"; }
        else{ return "2"; }

    }

    // 방 쪽지 확인 페이지
    @GetMapping("/notelist")
    public String notelist( Model model ){
        model.addAttribute( "rooms" , roomService.getmyroomlist() );
        model.addAttribute( "notes" , roomService.getmynotelist() );
        return "member/notelist";
    }






}


