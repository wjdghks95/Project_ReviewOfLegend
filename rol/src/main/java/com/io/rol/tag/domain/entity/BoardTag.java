package com.io.rol.tag.domain.entity;

import com.io.rol.board.domain.entity.Board;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

// ManyToMany 양방향 연결 Entity
@Entity(name = "board_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board; // 게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag; // 태그

    @Builder
    public BoardTag(Board board, Tag tag) {
        this.board = board;
        board.getBoardTagList().add(this);
        this.tag = tag;
        tag.getBoardTagList().add(this);
    }
}
