package com.io.rol.common.controller;

import com.io.rol.board.domain.entity.Board;
import com.io.rol.board.service.BoardService;
import com.io.rol.member.service.MemberService;
import com.querydsl.core.types.OrderSpecifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;

import static com.io.rol.domain.entity.QBoard.board;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberService memberService;
    private final BoardService boardService;

    @GetMapping("/")
    public String home(Model model) {

        OrderSpecifier<LocalDateTime> createdDate = board.createdDate.desc();
        OrderSpecifier<Integer> likeCount = board.likeCount.desc();
        OrderSpecifier<Integer> views = board.views.desc();

        List<Board> likeCountSortedBoardList = boardService.getBoardList(likeCount);
        List<Board> createdDateSortedBoardList = boardService.getBoardList(createdDate);
        List<Board> viewsSortedBoardList = boardService.getBoardList(views);

        model.addAttribute("likeCountSortedBoardList", likeCountSortedBoardList);
        model.addAttribute("createdDateSortedBoardList", createdDateSortedBoardList);
        model.addAttribute("viewsSortedBoardList", viewsSortedBoardList);

        return "index";
    }
}
