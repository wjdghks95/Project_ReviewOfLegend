package com.io.rol.board.repository;

import com.io.rol.board.domain.entity.Board;
import com.io.rol.domain.entity.QCategory;
import com.io.rol.domain.entity.QImage;
import com.io.rol.member.domain.entity.QMember;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.io.rol.domain.entity.QBoard.board;
import static com.io.rol.domain.entity.QCategory.*;
import static com.io.rol.domain.entity.QImage.*;
import static com.io.rol.member.domain.entity.QMember.*;

@Repository
@RequiredArgsConstructor
public class BoardQueryRepositoryImpl implements BoardQueryRepository{

    private final JPAQueryFactory queryFactory;

    // 리뷰 목록을 페이징 처리하여 조회
    @Override
    public Page<Board> findPagingBoardList(Pageable pageable, String category, String keyword) {
        List<Board> boardList = queryFactory
                .selectFrom(board)
                .join(board.category, QCategory.category).fetchJoin()
                .join(board.member, member).fetchJoin()
                .join(board.images, image).fetchJoin()
                .where(categoryCon(category), titleCon(keyword))
                .orderBy(board.lastModifiedDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = getCount(category, keyword);

        return PageableExecutionUtils.getPage(boardList, pageable, () -> count.fetchOne());
    }

    // 지정한 필드에 따른 순서로 게시글 전체 조회
    @Override
    public List<Board> findAllByOrder(OrderSpecifier<?> orderSpecifier) {
        return queryFactory.selectFrom(board)
                .orderBy(orderSpecifier)
                .fetch();
    }

    private JPAQuery<Long> getCount(String category, String keyword) {
        return queryFactory
                .select(board.count())
                .from(board)
                .where(categoryCon(category), titleCon(keyword));
    }

    private BooleanExpression categoryCon(String category) {
        return category == null || category.equals("") ? null : board.category.name.eq(category);
    }

    private BooleanExpression titleCon(String keyword) {
        return keyword == null || keyword.equals("") ? null : board.title.contains(keyword);
    }
}
