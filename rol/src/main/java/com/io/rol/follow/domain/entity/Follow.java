package com.io.rol.follow.domain.entity;

import com.io.rol.member.domain.entity.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "follow")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private Member follower; // 팔로워

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    private Member following; // 팔로잉

    //== 연관관계 메서드 ==//
    public void setFollower(Member follower) {
        this.follower = follower;
        follower.getFollowingList().add(this);
    }

    public void setFollowing(Member following) {
        this.following = following;
        following.getFollowerList().add(this);
    }
}
