    function rdelete(rnum){
        $.ajax({
            url:"/admin/delete",
            data:{
                "rnum" :rnum
            },
            success: function(data){
                if(data==1){
                    location.href = "/admin/roomlist";
                }
            }

        })
    }


    function activeupdate(rnum,act){
                $.ajax({
                    url:"/admin/activeupdate",
                    data:{
                        "rnum" :rnum,
                        "upactive" : act
                    },
                    success: function(data){
                        if(data==1){
                            location.href = "/admin/roomlist";
                        }else{
                            $("#actmsg").html("현재 동일한 상태입니다.");
                        }
                    }
                })
    }

    function update(rkind , rval , rnum){

        document.getElementById("updateinputbox").style.display = "";
        $("#updateinput").val(rval);
        $("#rkind0").val(rkind);
    }

                function updateconfirm(rnum){

                    alert("abv");
                    var rval = $("#updateinput").val();
                    var rkind = $("#rkind0").val();
                    $.ajax({
                        url:"/admin/roomupdate",
                        data:{
                            "rkind" :rkind,
                            "rval" : rval,
                            "rnum":rnum
                        },
                        success: function(data){
                            alert(data);
                            if(data==1){
                                location.href = "/admin/roomlist";
                            }else{

                            }
                        }
                    })
                }

