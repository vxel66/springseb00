

    function deleteboard(b_num){
        $.ajax({
            url : "/board/boarddeletecontroller",
            data : {b_num : b_num},
            method : "get",
            success : function(result){
                if(result==1){
                    location.href = "/board/boardlist";
                }else{
                    alert("삭제 오류");
                }
            }
        })
    }

    function boardwrite(){
        //폼태그 가져오기
        var formData = new FormData(form);
        //폼을 컨트롤러에게 전송
        $.ajax({
            type:"post",
            url : "/board/boardwritecontroller",
            data : formData,
            processData : false,
            contentType : false,    //첨부파일 보낼때,
            success :function(data){
               if(data==1){
                location.href = "/board/boardlist";
               }
            }
        })
    }
    //썸머노트
    $(document).ready(function() {
      $('#summernote').summernote({
            //한글지원
          lang: 'ko-KR', // default: 'en-US'
                minHeight : 400, //최소높이
                maxHeight : null //최대높이
      });
    });

    //댓글 등록

    function replywrite(b_num){
        var rcontents = document.getElementById("rcontents").value;
        $.ajax({
            url: "/board/replywrite",
            data : {
                "b_num" : b_num,
                "rcontents" : rcontents
            },
            success : function(data){
                if(data==1){
                    //특정 태그만 새로고침 [JQuery]
                    $("#replytable").load(location.href+" #replytable");
                }
            }
        })
    }

    //댓글 삭제
    function deletereply(rnum){
        $.ajax({
            url:"/board/replydelete",
            data: {
                "rnum" : rnum
            },
            success : function(data){
                if(data==1){
                    //특정 태그만 새로고침 [JQuery]
                    $("#replytable").load(location.href+" #replytable");
                }
            }
        })
    }























