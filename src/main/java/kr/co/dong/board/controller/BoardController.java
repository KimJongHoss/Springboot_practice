package kr.co.dong.board.controller;

import kr.co.dong.board.entity.Board;
import kr.co.dong.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class BoardController {
    @Autowired
    private BoardService boardService;

//    기본 주소가 메인 화면으로 이동
    @GetMapping("/")
    public String goHome(){

        return "index";
    }

//    게시판 작성 폼으로 이동
    @GetMapping("/board/write") //localhost:8080/board/write
    public String boardWriteForm(){

        return "boardWrite";
    }

//    게시판 작성 기능
    @PostMapping("/board/writepro")
    public String boardWritePro(Board board, Model model, @RequestParam("file") MultipartFile file) throws Exception{

        System.out.println("롬복으로 받은 제목 : "+board.getTitle());

        boardService.write(board, file);

        model.addAttribute("message", "글 작성이 완료되었습니다.");

        model.addAttribute("searchUrl", "/board/list");

       return "message";
    }

//    게시판 목록 출력
    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 10, sort = "id",
                                    direction = Sort.Direction.DESC) Pageable pageable,
                            @RequestParam(value = "searchKeyword", required = false) String searchKeyword){

        Page<Board> list = null;

        if(searchKeyword ==null){
            list = boardService.boardList(pageable);
        }else {
           list = boardService.boardSearchList(searchKeyword, pageable);
        }

        int nowPage = list.getPageable().getPageNumber() +1;
        int startPage = Math.max(nowPage-4, 1);
        int endPage = Math.min(nowPage+5, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "boardlist";
    }

//    게시판 세부 내용 출력
    @GetMapping("/board/view")// localhost:8080/board/view?id=1
    public String boardview(Model model,
                            @RequestParam("id") Integer id){

        Board board = boardService.boardView(id);
        System.out.println(board);
        model.addAttribute("board", board);

        return "boardview";
    }

//    게시물 삭제 기능
    @GetMapping("/board/delete")
    public String boardDelete(@RequestParam("id") Integer id, Model model){

        boardService.boardDelete(id);

        model.addAttribute("message", "글 삭제 완료!!");

        model.addAttribute("searchUrl", "/board/list");


        return "message";
    }

//    게시판 수정 폼으로 이동
    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id
                            ,Model model) {

    model.addAttribute("board", boardService.boardView(id));

        return "boardmodify";
    }

//    게시판 수정 기능
    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, @RequestParam("file") MultipartFile file) throws Exception{

    Board boardTemp = boardService.boardView(id);
    boardTemp.setTitle(board.getTitle());
    boardTemp.setContent(board.getContent());

        System.out.println(boardTemp);

        boardService.write(boardTemp, file);

        model.addAttribute("message", "글 수정이 완료되었습니다.");

        model.addAttribute("searchUrl", "/board/list");


        return "message";
    }

}
