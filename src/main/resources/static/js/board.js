

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


















