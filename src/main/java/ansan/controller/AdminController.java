package ansan.controller;

import ansan.domain.Entity.Room.RoomEntity;
import ansan.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    RoomService roomService;

    @GetMapping("/roomlist")
    public String roomlist(Model model){

        List<RoomEntity> roomEntity = roomService.getroomlist();

        model.addAttribute("roomEntity",roomEntity);

        return "admin/roomlist";
    }

    @GetMapping("/delete")
    @ResponseBody
    public String delete(@RequestParam("rnum")int rnum){

        if(roomService.deleteroom(rnum)){
            return "1";
        }else{
            return "2";
        }
    }

    @GetMapping("/activeupdate")
    @ResponseBody
    public String activeupdate(@RequestParam("rnum")int rnum , @RequestParam("upactive")String act){

        if(roomService.activeupdate(rnum,act)){
            return "1";
        }else{
            return "2";
        }
    }

    @GetMapping("/roomupdate")
    @ResponseBody
    public String updateroom(@RequestParam("rkind")String rkind,
                             @RequestParam("rval")String rval,
                             @RequestParam("rnum")int rnum){
        roomService.updateroom(rkind,rval,rnum);
        return "1";
    }


}
