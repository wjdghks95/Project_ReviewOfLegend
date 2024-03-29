package com.io.rol.board.controller;

import com.io.rol.member.domain.entity.Member;
import com.io.rol.member.domain.entity.Role;
import com.io.rol.member.exception.MemberException;
import com.io.rol.member.exception.MemberExceptionType;
import com.io.rol.member.repository.MemberRepository;
import com.io.rol.security.context.MemberContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContentsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired EntityManager em;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired MemberRepository memberRepository;

    @Value("${file.dir}/")
    private String fileDirPath;

    private static final String USERNAME = "test@test.com";
    private static final String IMAGE1 = "sample1.png";
    private static final String IMAGE2 = "sample2.png";
    private static final int THUMBIDX = 0;
    private static final String TITLE = "제목";
    private static final String CATEGORY = "food";
    private static final int RATING = 5;
    private static final String DESCRIPTION = "테스트를 하기 위한 20자 이상의 내용입니다.";
    private static final String NEW_BOARD_URL = "/contents/board/new";
    private void clear(){
        em.flush();
        em.clear();
    }

    @BeforeEach
    private void setAuthentication() throws Exception {
        Member member = Member.builder()
                .phone("01012345678")
                .email("test@test.com")
                .password(passwordEncoder.encode("asdf1234!"))
                .name("이름")
                .nickname("테스트")
                .zipcode("12345")
                .address("주소")
                .detailAddress("상세주소")
                .role(Role.USER)
                .build();
        memberRepository.save(member);

        Member findMember = memberRepository.findByEmail(USERNAME).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(findMember.getRole().value()));

        MemberContext memberContext = new MemberContext(findMember, roles);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(memberContext, null, roles));
        SecurityContextHolder.setContext(securityContext);
        clear();
    }

    /*
       게시글 작성
          게시글 작성 시 업로드한 이미지가 한 개라도 없으면 오류
          제목과 내용을 입력하지 않거나 카테고리를 선택하지 않으면 오류
          제목이 50자 이상인 경우 오류
          설명이 20자 이하인 경우 오류
     */
    @Test
    @DisplayName("게시글 작성_성공")
    void board_write_success() throws Exception {
        // given, when
        newBoardPerform(getMockUploadFile(IMAGE1), getMockUploadFile(IMAGE2), THUMBIDX, TITLE, CATEGORY, DESCRIPTION, RATING, createTagNames())
                // then
                .andExpect(status().is3xxRedirection())
                .andDo(print());
    }


    @Test
    @DisplayName("게시글 작성_실패_업로드한 이미지가 없음")
    void board_write_failure_empty_image() throws Exception {
        // given, when
        newBoardPerform(new MockMultipartFile("file", (byte[]) null), new MockMultipartFile("file", (byte[]) null), THUMBIDX, TITLE, CATEGORY, DESCRIPTION, RATING, createTagNames())
                // then
                .andExpect(model().attributeHasFieldErrors("boardDto", "file"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 작성_실패_필수 입력란에 입력하지 않는 필드 존재")
    void board_write_failure_empty_field() throws Exception {
        // given, when
        newBoardPerform(getMockUploadFile(IMAGE1), getMockUploadFile(IMAGE2), THUMBIDX, null, null, null, RATING, createTagNames())
                // then
                .andExpect(model().attributeHasFieldErrors("boardDto", "title"))
                .andExpect(model().attributeHasFieldErrors("boardDto", "category"))
                .andExpect(model().attributeHasFieldErrors("boardDto", "description"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 작성_실패_50자 이상의 제목 입력")
    void board_write_failure_long_title() throws Exception {
        // given, when
        newBoardPerform(getMockUploadFile(IMAGE1), getMockUploadFile(IMAGE2), THUMBIDX, TITLE+"테스트를 위한 제목 테스트를 위한 제목 테스트를 위한 제목 테스트를 위한 제목 테스트를 위한 제목",
                CATEGORY, DESCRIPTION, RATING, createTagNames())
                // then
                .andExpect(model().attributeHasFieldErrors("boardDto", "title"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 작성_실패_20자 이하의 설명 입력")
    void board_write_failure_short_description() throws Exception {
        // given, when
        newBoardPerform(getMockUploadFile(IMAGE1), getMockUploadFile(IMAGE2), THUMBIDX, TITLE,
                CATEGORY, "DESCRIPTION", RATING, createTagNames())
                // then
                .andExpect(model().attributeHasFieldErrors("boardDto", "description"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private ResultActions newBoardPerform(MockMultipartFile mockUploadFile1, MockMultipartFile mockUploadFile2, int thumbidx, String title,
                                          String category, String description, int rating, List<String> tagNames) throws Exception {
        return mockMvc.perform(multipart(NEW_BOARD_URL)
                .file(mockUploadFile1)
                .file(mockUploadFile2)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("thumbnailIdx", String.valueOf(thumbidx))
                .param("title", title)
                .param("category", category)
                .param("description", description)
                .param("rating", String.valueOf(rating))
                .param("tagNames", String.valueOf(tagNames)));
    }

    private MockMultipartFile getMockUploadFile(String fileName) throws IOException {
        return new MockMultipartFile("file", "file.jpg", "image/jpg",
                new FileInputStream(fileDirPath + fileName));
    }

    private List<String> createTagNames() {
        List<String> tagNames = new ArrayList<>();
        tagNames.add("태그1");
        tagNames.add("태그2");
        tagNames.add("태그3");
        return tagNames;
    }
}