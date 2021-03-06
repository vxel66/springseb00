package ansan.domain.Entity.Member;

import ansan.domain.Entity.BaseTimeEntity;
import ansan.domain.Entity.Room.NoteEntity;
import ansan.domain.Entity.Room.RoomEntity;
import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity // DB내 테이블과 연결
@Table( name = "member") // 테이블속성 // db에서 사용할 테이블명
@AllArgsConstructor@NoArgsConstructor
@Getter@Setter@ToString@Builder
public class MemberEntity extends BaseTimeEntity {

    @Id // 기본키 pk
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto key
    private int m_num;   // 회원번호
    @Column
    private String mid;    // 회원아이디
    @Column
    private String m_password; // 회원비밀번호
    @Column
    private String m_name; // 회원이름
    @Column
    private String m_sex; // 회원성별
    @Column
    private String m_phone; // 회원연락처
    @Column
    private String memail; // 회원이메일
    @Column
    private String m_address; // 회원주소
    @Column
    private int m_point; // 회원포인트

    @Enumerated(EnumType.STRING)
    @Column
    private Role m_grade; // 회원등급

    //해당 Role에 key 반환 메소드
    public String getRolekey(){
        return this.m_grade.getKey();
    }

    @OneToMany(mappedBy = "memberEntity")
    private List<RoomEntity> roomEntityList = new ArrayList<>();

    // 문의 리스트
    @OneToMany( mappedBy ="memberEntity" )
    private List<NoteEntity> noteEntities = new ArrayList<>();

    //oauth2 에서 동일한 이메일이면 업데이트 처리 메소드
    public MemberEntity update(String name){
        this.m_name =name;
        return this;
    }


}
