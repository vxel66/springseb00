package ansan.domain.Entity.Room;

import ansan.domain.Entity.BaseTimeEntity;
import ansan.domain.Entity.Member.MemberEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity // DB내 테이블과 연결
@Table( name = "room") // 테이블속성 // db에서 사용할 테이블명
@AllArgsConstructor //풀생성자 [롬북]
@NoArgsConstructor  //빈생성자 [롬북]
@Getter             //필드 get[룸북]
@Setter             //필드 set[룸북]
@ToString(exclude="roomEntitys")           //ToString -> Object [객체의 주소값] : @ToString -> Object [ 객체의 주소값 ]
@Builder            //객체 생성시 안정성 보장 [ new 생성자() <--> Builder : 1.필드 주입순서 X  ]
public class RoomEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "rnum")
    private int rnum;

    @Column(name = "rmname")
    private String rmname;

    @Column(name = "rprice")
    private String rprice;

    @Column(name = "rarea")
    private int rarea;

    @Column(name = "rmanagementfee")
    private int rmanagementfee;

    @Column(name = "rcompletiondate")
    private String rcompletiondate;

    @Column(name = "rindate")
    private String rindate;

    @Column(name = "rstructure")
    private String rstructure;

    @Column(name = "rfloor")
    private String rfloor;

    @Column(name = "rkind")
    private String rkind;

    @Column(name = "raddress")
    private String raddress;

    @Column(name = "rcontents")
    private String rcontents;

    @Column(name = "ractive")
    private String ractive;

    @Column(name = "rtras")
    private String rtras;

   //회원번호관계
    @ManyToOne
    @JoinColumn(name = "mnum")
    private MemberEntity memberEntity;

    //이미지 관계 //룸삭제/변경시 이미지도 같이 삭제/변경 [제약조건 CascadeType.ALL ]
    @OneToMany(mappedBy="roomEntity" ,cascade = CascadeType.REMOVE)
    private List<RoomimgEntity> roomEntitys =  new ArrayList<>();

    // 문의 글 리스트
    @OneToMany( mappedBy = "roomEntity" , cascade = CascadeType.ALL)
    private List<NoteEntity> noteEntities = new ArrayList<>();
}
