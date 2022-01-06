
//아이디 유효성 검사
$(function(){
    $("#m_id").keyup(function(){
            //해당 아이디에 키보드가 눌렸을떄
          var m_id = $("#m_id").val();

          var idj=/^[a-z0-9]{5,15}$/    //정규 표현식 영소문자 5~15글자만 허용

          if(!idj.test(m_id)){ // 정규표현식이 다를 경우
            $("#idresult").html("영소문자 5~15 글자만 가능합니다");
            return false;   //함수종료
          }
          //아이디 중복 체크 비동기 통신
          $.ajax({
          method:"post",
          url:"/member/idcheck",
          data:{'m_id':m_id},
          success:function(data){
            if(data==1){
                $("#idresult").html("현재 사용중인 아이디입니다");
                return false; //폼 전송 막기
            }else{
                 $("#idresult").html("사용가능");
            }
          }
          })
    })

    $("#m_password").keyup(function(){
        var m_password = $("#m_password").val();
        var m_passwordconfirm = $("#m_passwordconfirm").val();

        var pwj=/^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\d~!@#$%^&*()+|=]{8,16}$/

        if(!pwj.test(m_password)){
            $("#pwresult").html("'숫자','문자','특수문자'포함 최소 8자에서 최대 16자 가능합니다");
            return false;
        }else{
            $("#pwresult").html("사용가능");
        }
    })

    $("#m_passwordconfirm").keyup(function(){
        var m_password = $("#m_password").val();
        var m_passwordconfirm = $("#m_passwordconfirm").val();
        var pwj=/^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\d~!@#$%^&*()+|=]{8,15}$/

        if(!pwj.test(m_passwordconfirm)){
                    $("#pwresult").html("'숫자','문자','특수문자'포함 최소 8자에서 최대 15자 가능합니다");
                    return false;
        }else if(m_password!=m_passwordconfirm){
            $("#pwresult").html("서로 비밀번호가 다릅니다");
            return false;
        }else{
            $("#pwresult").html("사용가능");
        }
    })

    $("#m_name").keyup(function(){
        var namej= /[가-힣]{2,}/;
        var name = $("#m_name").val();

        if(!namej.test(name)){
            $("#nameresult").html("한글만 사용가능합니다");
            return false;
        }else{
            $("#nameresult").html("사용가능");
        }

    })

    //성별 유효성 검사
/*    $("#m_sex").keyup(function(){
        alert("m_sex")
        var sex1 = document.getElementById("sex1").sex1.checked;	// checked 유무 가져오기
        var sex2 = document.getElementById("sex2").sex2.checked;
        if(!$("#sex1").checked||!$("#sex2").checked){
            return false;
        }
    })*/

    //연락처 유효성 검사
    $("#m_phone").keyup(function(){
        var phonj = /^01([0|1|6|7|8|9]?)-?([0-9]{3,4})-?([0-9]{4})$/;
        var m_phon = $("#m_phone").val();
        if(!phonj.test(m_phon)){
            $("#phoneresult").html("000-0000-0000 형식으로 입력해주세요");
            return false;
        }else{
            $("#phoneresult").html("사용가능");
        }
    })

    //이메일 유효성 검사
    $("#m_email").keyup(function(){
        var m_email = $("#m_email").val();
        var emailj = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;

        if(!emailj.test(m_email)){
            $("#emailresult").html("이메일 형식으로 입력해주세요");
            return false;
        }
          $.ajax({
          method:"post",
          url:"/member/emailcheck",
          data:{'m_email':m_email},
          success:function(data){
            if(data==1){
                $("#emailresult").html("현재 사용중인 이메일입니다");
                return false; //폼 전송 막기
            }else{
                 $("#emailresult").html("사용가능");
            }
          }
          })

    })

    $("#sample4_postcode").keyup(function(){
        var address1 = $("#sample4_postcode").val();
        if(address1.indexOf("/")!=-1){
            $("#addressresult").html("주소에 '/'를 포함할 수 없습니다")
        }
        else{
            $("#addressresult").html("사용가능")
        }
    })
       $("#sample4_roadAddress").keyup(function(){
            var address1 = $("#sample4_roadAddress").val();
            if(address1.indexOf("/")!=-1){
                $("#addressresult").html("주소에 '/'를 포함할 수 없습니다")
            }
            else{
                        $("#addressresult").html("사용가능")
            }
        })
           $("#sample4_jibunAddress").keyup(function(){
                var address1 = $("#sample4_jibunAddress").val();
                if(address1.indexOf("/")!=-1){
                    $("#addressresult").html("주소에 '/'를 포함할 수 없습니다")
                }
                else{
                            $("#addressresult").html("사용가능")
                }
            })
               $("#sample4_detailAddress").keyup(function(){
                    var address1 = $("#sample4_detailAddress").val();
                    if(address1.indexOf("/")!=-1){
                        $("#addressresult").html("주소에 '/'를 포함할 수 없습니다")
                    }
                    else{
                                $("#addressresult").html("사용가능")
                    }
                })

    $("#formsubmit").click(function(){
        if(!$(""))
        if($("#idresult").html()!="사용가능"){
            alert("아이디 사용불가입니다");
        }else if($("#pwresult").html()!="사용가능"){
            alert("비밀번호 사용불가입니다");
        }else if($("#nameresult").html()!="사용가능"){
            alert("이름 사용불가입니다");
        }else if($("#phoneresult").html()!="사용가능"){
            alert("연락처 사용불가입니다");
        }else if($("#emailresult").html()!="사용가능"){
            alert("이메일 사용불가입니다");
        }else if($("#addressresult").html()!="사용가능"){
            alert("주소 사용불가입니다");
        }else{
            $("form").submit(); //모든유효성 검사 통과시 폼 전송
        }
    })
})








