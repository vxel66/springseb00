package ansan.domain.Dto;

import ansan.domain.Entity.Member.MemberEntity;
import ansan.domain.Entity.Member.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Oauth2Dto {

    //이름
    String name;
    //이메일
    String email;
    //회원정보
    private Map<String,Object> attribute;
    //요청 토큰[키]
    private String nameattributekey;

    @Builder
    public Oauth2Dto(String name, String email, Map<String, Object> attribute, String nameattributekey) {
        this.name = name;
        this.email = email;
        this.attribute = attribute;
        this.nameattributekey = nameattributekey;
    }

    //클라이언트 구분용 [카카오 or 네이버 or 구글 ]
    public static Oauth2Dto of(String resitrationid, String userNameAttribute,Map<String,Object> attribute){
        if(resitrationid.equals("kakao")){
            return ofkakao(userNameAttribute,attribute);
        }else if(resitrationid.equals("naver")){
            return ofnaver(userNameAttribute,attribute);
        }else{
            return ofgoogle(userNameAttribute,attribute);
        }
    }

    //카카오 정보 dto 변환 메소드
    private static Oauth2Dto ofkakao(String userNameAttribute,Map<String,Object> attribute){
        Map<String , Object> kakao_account = (Map<String, Object>) attribute.get("kakao_account");
        Map<String , Object> profile = (Map<String, Object>) kakao_account.get("profile");

        return Oauth2Dto.builder()
                .name((String)profile.get("nickname"))
                .email((String) kakao_account.get("email"))
                .attribute(attribute)
                .nameattributekey(userNameAttribute)
                .build();
    }

    //구글 정보 dto 변환 메소드
    private static Oauth2Dto ofgoogle(String userNameAttribute,Map<String,Object> attribute){

        return Oauth2Dto.builder()
                .name( (String) attribute.get("name") ) //구글 회원 이름
                .email((String) attribute.get("email")) //구글 회원 이메일
                .attribute( attribute )                 //구글 회원정보
                .nameattributekey( userNameAttribute )  //구글 인증 키
                .build();

    }

    //네이버 정보 dto 변환 메소드
    private static Oauth2Dto ofnaver(String userNameAttribute,Map<String,Object> attribute){

        Map<String, Object> response = (Map<String, Object>) attribute.get("response"); // response 키 호출

        return Oauth2Dto.builder()
                .name( (String) response.get("name") )
                .email((String) response.get("email"))
                .attribute( attribute )
                .nameattributekey( userNameAttribute )
                .build();
    }

    // dto -> entity
    public MemberEntity toEntity(){
        return MemberEntity.builder().m_name(name).memail(email).m_grade(Role.MEMBER).mid(email.split("@")[0]).build();
    }




}
















