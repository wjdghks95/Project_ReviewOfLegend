// 좋아요
$('.board__like-button').on('click', function() {
    $(this).children().toggleClass('active');
})

// 팔로우
$('.board__follow-button').on('click', function() {

    if($(this).hasClass('anonymous')) {
        alert('팔로우 서비스는 로그인 후 이용가능합니다.');
    } else {
        const followBtn = $(this).children();
        const href = location.href;
        const id = href.slice(-1);

        $.ajax({
            url: "/follow.do",
            method: "GET",
            data: {"id" : id},
            success: function() {
                follow(followBtn);
            }, 
            error: function() {
            }
        })
    }
})

function follow(followBtn) {
    $(followBtn).toggleClass('active');

    if ($(followBtn).hasClass('active')) {
        $(followBtn).html('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" style="width:10px; height:10px;"><!--! Font Awesome Pro 6.2.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license (Commercial License) Copyright 2022 Fonticons, Inc. --><path d="M470.6 105.4c12.5 12.5 12.5 32.8 0 45.3l-256 256c-12.5 12.5-32.8 12.5-45.3 0l-128-128c-12.5-12.5-12.5-32.8 0-45.3s32.8-12.5 45.3 0L192 338.7 425.4 105.4c12.5-12.5 32.8-12.5 45.3 0z"/></svg> 팔로잉');
    } else {
        $(followBtn).html('+ 팔로우');
    }
}