package ansan.domain.Entity.Room;

import ansan.domain.Entity.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity // DB내 테이블과 연결
@Table( name = "roomimg") // 테이블속성 // db에서 사용할 테이블명
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude="roomEntity")
@Builder
public class RoomimgEntity extends BaseTimeEntity {

    //번호
    @Id // 기본키 pk
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto key
    @Column(name = "rimgnum")
    private int rimgnum;

    @Column(name="rimg")
    private String rimg;

    @ManyToOne
    @JoinColumn(name="rnum")
    private RoomEntity roomEntity;



}
