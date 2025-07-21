package com.example.rewrite.controller.rest;

import com.example.rewrite.command.ProductDTO;
import com.example.rewrite.command.user.ApiResponseDto;
import com.example.rewrite.command.user.ChangeRoleDto;
import com.example.rewrite.command.user.UserDTO;
import com.example.rewrite.service.prod.ProdService;
import com.example.rewrite.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {

    private final UserService userService;
    private final ProdService prodService;

    @GetMapping("/users")
    public List<UserDTO> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role) {

        String roleToSearch = role; // role 파라미터 복사

        // role 값이 null이거나, 비어있거나, "none"일 경우 실제 검색 시에는 null 사용 (역할 필터링 안 함)
        if (roleToSearch == null || roleToSearch.trim().isEmpty() || "none".equalsIgnoreCase(roleToSearch)) {
            roleToSearch = null;
        }

        // search 값은 그대로, role 값은 필터링 여부에 따라 null 또는 실제 값 전달
        return userService.findUsers(search, roleToSearch);
    }
//    @GetMapping("/users")
//    public List<UserDTO> getUsers( // Users 엔티티 대신 DTO 사용 권장
//                                   @RequestParam(required = false) String search,
//                                   @RequestParam(required = false) String role) {
//
//        // if문으로 예외처리 해주기
//        if (role.equals("none")) {
//            String getall = null;
//            return userService.findUsers(getall, getall);
//        }
//        return userService.findUsers(search, role);
//    }

    @PostMapping("/setUserRole")
    public ResponseEntity<Object> setUserRole(@RequestBody ChangeRoleDto changeRoleDto) {
        try {
            userService.changeRole(changeRoleDto.getUid(), changeRoleDto.getRole());
            return ResponseEntity.ok(ApiResponseDto.success("role 변경 성공"));
        }catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.fail(e.getMessage()));
        }
    }
    @DeleteMapping("/users/{uid}")
    public ResponseEntity<Object> deleteUser(@PathVariable String uid) {

        Long uidLong = Long.valueOf(uid);
        log.info(uid + "번 유저 삭제");
        try {
            userService.deleteUser(uidLong);
            return ResponseEntity.ok(ApiResponseDto.success("삭제 성공"));
        }catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.fail(e.getMessage()));
        }
    }
    @DeleteMapping("/products/{prodId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long prodId) {

        try{
            prodService.deleteProduct(prodId);
            return ResponseEntity
                    .ok(ApiResponseDto.success("삭제 성공"));
        }catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.fail("삭제 실패" + e.getMessage()));
        }
    }

    @GetMapping("/products")
    public ResponseEntity<?> getProducts(@RequestParam(required = false) Long uid) {
        // 리턴값으로 List<ProductDTO>이나, 리스폰스DTO를 리턴해줘도 받아주는 와일드카드 사용가능

        try {
            List<ProductDTO> prodList = prodService.searchProductByUid(uid);
            return ResponseEntity.ok(prodList);
        }catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.fail("DB에 접근하지 못했습니다.")); // 또는 오류 메시지를 담은 DTO 반환
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.fail("해당 유저가 판매중인 상품이 존재하지 않습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.fail("잘못된 요청입니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.fail("내부 서버 오류입니다. 다시 시도해주세요."));
        }
    }
}