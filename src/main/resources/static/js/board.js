

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
