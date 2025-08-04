package org.mbc.board.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mbc.board.dto.BoardDTO;
import org.mbc.board.dto.PageRequestDTO;
import org.mbc.board.security.dto.MemberJoinDTO;
import org.mbc.board.security.dto.MemberSecurityDTO;
import org.mbc.board.service.MemberService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/member")
@Log4j2
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;  // 데이터베이스까지 엔티티와 dto를 처리

    @GetMapping("/login")
    public void loginGet(String error, String logout){
        // http://localhost/member/login?error=???
        // http://localhost/member/login?logout=???
        log.info("MemberController.loginGet메서드 실행....");
        log.info("logout: " + logout); //데이터베이스에서 활용
        log.info("error: " + error); //데이터베이스에서 활용

        if(logout != null){
            log.info("logout 처리됨!!! : " + logout);
        }

    } // 로그인 메서드 종료


    @GetMapping("/join")    // http://localhost:8000/member/join
    public void joinGet(){
        // void -> templates/member/join.html
        log.info("MemberController.join get......");
        // url이 넘어오면 프론트로 페이지 출력용
    }

    @PostMapping("/join")    // http://localhost:8000/member/join
    public String joinPost(MemberJoinDTO memberJoinDTO, RedirectAttributes redirectAttributes){
        // html에서 넘어오는 데이터 처리용

        log.info("MemberController.join post......");
        log.info(memberJoinDTO);

        try{
            memberService.join(memberJoinDTO);  // 회원가입 처리됨!
        }catch(MemberService.MidExistException e){
            redirectAttributes.addFlashAttribute("error", "mid");
            // 회원가입시 id 중복되는 예외처리
            return "redirect:/member/join"; // 회원가입 페이지로 다시 감
        }
        redirectAttributes.addFlashAttribute("result", "success");
        return "redirect:/member/login";    // 회원 가입 성공시 로그인 페이지로 이동

        // return "redirect:/member/login"; // 회원 가입 후에 로그인 페이지로 이동
        // return "redirect:/board/list";  // 회원가입 후에 리스트 페이지로 이동

    }


    @GetMapping("/modify")
    public void ModifyGet(@AuthenticationPrincipal MemberSecurityDTO memberSecurityDTO , Model model){
        log.info("=====MemberController.ModifyGet()=====");
        /*  MemberSecurityDTO dto = new MemberSecurityDTO();*/
        model.addAttribute("member", memberSecurityDTO);

    }// ModifyGet 메서드

    /* @PreAuthorize("principal.mid == #memberSecurityDTO.mid")*/
    @PostMapping("/modify")
    public String ModifyPost(@AuthenticationPrincipal MemberSecurityDTO memberSecurityDTO, RedirectAttributes redirectAttributes, Model model) {


        log.info("MemberController.ModifyPost..... ");
        log.info(memberSecurityDTO);

        if(memberSecurityDTO == null){
            log.info("인증되지 않는 사용자 ");
            redirectAttributes.addFlashAttribute("error", "로그인하세요");
            return "redirect:/member/login";
        }

        memberService.modify(memberSecurityDTO);
        redirectAttributes.addFlashAttribute("result", "modified");
        /*redirectAttributes.addAttribute("mid", memberSecurityDTO.getMid());*/

        return "redirect:/board/list";

    } // ModifyPost() 종료
}
