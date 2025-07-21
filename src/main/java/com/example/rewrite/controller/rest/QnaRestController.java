package com.example.rewrite.controller.rest;

import com.example.rewrite.command.user.ApiResponseDto;
import com.example.rewrite.command.user.UserSessionDto;
import com.example.rewrite.entity.Qna;
import com.example.rewrite.repository.product.ProductRepository;
import com.example.rewrite.repository.qna.QnaRepository;
import com.example.rewrite.service.fileUpload.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class QnaRestController {

    private final QnaRepository qnaRepository;
    private final FileUploadService fileUploadService;

    @PostMapping("/qna")
    public ResponseEntity<ApiResponseDto> qnaUpload(
            @RequestParam("category") String category,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "folderPath", required = false) String folderPath,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpSession session) {

        String filePath = null;
        try {
            filePath = fileUploadService.uploadFile(file, folderPath);
        }catch(Exception e) {
            e.printStackTrace();
        }

        UserSessionDto userSession = (UserSessionDto) session.getAttribute("user");
        String userId = userSession.getId();
        
        
        // 에러픽스용
        Qna qna = Qna.builder()
                .answer("답변 대기중입니다.")
                .category(category)
                .title(title)
                .content(content)
                .fileAttachment(filePath)
                .USERID(userId)
                .build();
        try{
            qnaRepository.save(qna);
            return ResponseEntity.ok(ApiResponseDto.success("업로드 성공했습니다."));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.fail("업로드에 실패했습니다." + e.getMessage()));
        }
    }

}
