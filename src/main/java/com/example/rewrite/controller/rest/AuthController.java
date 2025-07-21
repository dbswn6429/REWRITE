package com.example.rewrite.controller.rest;

import com.example.rewrite.command.user.*;
import com.example.rewrite.entity.Users; // 선택사항: 반환값으로 사용자 정보 일부를 포함하고 싶을 때
import com.example.rewrite.repository.users.UsersRepository;
import com.example.rewrite.service.mail.MailService;
import com.example.rewrite.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UsersRepository usersRepository;
    private final MailService mailService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDto signupRequestDto, HttpServletRequest request) {

        try {
            Users registeredUser = userService.signup(signupRequestDto);

            // 성공시 세션에 로그인데이터 담아서 넣어줌
            HttpSession session = request.getSession();
            UserSessionDto userSessionDto = UserSessionDto.fromEntity(registeredUser); //유저 엔티티 -> 유저세션DTO로 변경
            session.setAttribute("user", userSessionDto); //

            // 회원가입 성공시, 성공 메세지를 body에 담아주면 json형식으로 보내줌.

            // 맵 형식 리팩토링 필요
//            Map<String, Object> successBody = new HashMap<>();
//
//            successBody.put("message", "회원가입을 환영합니다. " + registeredUser.getName() +"님!");
            mailService.sendWelcomeEmail(registeredUser.getEmail(), registeredUser.getId(), registeredUser.getName());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success("회원가입을 환영합니다. " + registeredUser.getName() +"님!"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.fail(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.fail(e.getMessage()));
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request) {

        // 받아온 Dto를 기반으로 auth 토큰 생성
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequestDto.getId(), loginRequestDto.getPw());

        try {
            //auth매니저한테 인증요청 보내봄
            Authentication authentication = authenticationManager.authenticate(token);

            // 얘가 db에 들어가서 알아서 확인하는 역할을 함.

            // DaoAuthenticationProvider 얘를 쓰는거임
            // 1. 유저측에서 받아온 유저네임, 패스워드를 받음
            // 2. authenticationManager 안에 있는 ProviderManager한테 위임.

            // 3. DaoAuthenticationProvider 한테 다시 위임해주고, provide 된 후 내부적으로 처리하기 시작함.
            // DaoAuthenticationProvider -> 스레드 분산같은게 아니고, JWT, Oauth 등 여러가지 인증 토큰을 처리할수 있음
            // 지금 우리가 보내는 토큰은 UsernamePasswordAuthenticationToken 토큰인데, 얘는 DaoAuthenticationProvider가 처리함.
            // 만약 JWT 토큰을 보내준다면, JwtAuthenticationToken을 인식해서 JwtAuthenticationProvider가 처리해주는 구조임.

            // 4. 그 내부에서는 UserDetailsService 에서 유저 정보를 조회하고, PasswordEncoder로 내부적으로 대조해봄.
            // 5. 검증이 완료됐다면, UserDetails 안에 UserDetailsService와 authorities를 담아줌.
            // . 업로드용

            // admin111 로그인해서 principal 찍어보면 ->
            //org.springframework.security.core.userdetails.User [Username=admin111, Password=[PROTECTED], Enabled=true, AccountNonExpired=true,
            //credentialsNonExpired=true, AccountNonLocked=true, Granted Authorities=[ROLE_USER]]


            System.out.println(authentication.getPrincipal());
            System.out.println(authentication.getAuthorities());
            System.out.println(authentication.getDetails());
            System.out.println(authentication.getCredentials());
            System.out.println(authentication.getName());

            // 인증 성공 -> 시큐리티 컨텍스트홀더에 인증 정보 저장
            // 나중에 다른 곳에서도 접근 가능함
            //SecurityContext context = SecurityContextHolder.getContext();
            //Authentication authentication = context.getAuthentication();
            //String username = authentication.getName();
            // 또한 스프링 시큐리티에서 사용자 인증여부도 판단함.
            // 예를 들어
            //.antMatchers("/adminPage").access("hasRole('ROLE_ADMIN')")
            //.antMatchers("/userPage").access("hasRole('ROLE_USER')")
            // 이런 식으로 role에 따라 들어갈 수 있는지 없는지 판단하는 곳에 넣어줌.
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 세션 생성하기
            HttpSession session = request.getSession(true);

            // 응답보내줄 사용자 정보
            // 인증된 사용자 정보 가져오기
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // 유저디테일을 users 엔티티로 변환 (디테일서비스에서는 role 외에는 정보가 딸림)

            // Users 엔티티에 넣어줌.
            Users loggedInUser = usersRepository.findById(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("인증 후 사용자 정보를 찾을 수 없음")); // 예외 처리 필요함

            UserSessionDto userSessionDto = UserSessionDto.fromEntity(loggedInUser);
            session.setAttribute("user", userSessionDto);

            // 성공 응답 반환
            return ResponseEntity.ok(userSessionDto);

        }catch (BadCredentialsException e) { // 인증 실패 -> HashMap 방식에서 ApiResponseDto로 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.fail("아이디나 비밀번호가 일치하지 않습니다"));

        }catch (Exception e) { // 기타 서버 에러 발생시
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.fail("기타 오류가 발생하였습니다."));
        }
    }
    @PostMapping("/find-id")
    public ResponseEntity<ApiResponseDto> findIdByEmail(@Valid @RequestBody FindIdRequestDto requestDto) {

        // 이프(이름, 전화번호, 이메일을 받아서 유저가 있는지 조회하는)문 삭제 - service 안에 넣어서 일관성 있게 해결함
        try {
            userService.sendUserIdToEmail(requestDto);
            // UserService 호출이 성공하면 (예외가 발생하지 않으면)
            String successMessage = "아이디 정보가 [" + requestDto.getEmail() + "] 주소로 발송되었습니다. 메일을 확인해주세요. 메일이 확인되지 않는 경우, 스팸 메일함도 확인해주세요.";
            log.info(successMessage);
            return ResponseEntity.ok(ApiResponseDto.success(successMessage));

            // 캐치해야할거,
            // EntityNotFoundException (sendUserIdToEmail 에서 던져줌)
            // MailException - 메일 보낼때 오류
            // RuntimeException - 서버 오류
        } catch (EntityNotFoundException e) {
            // UserService에서 사용자를 찾지 못했을 때
            log.warn("아이디 찾기 실패 (사용자 없음): {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND) // 404 Not Found
                    .body(ApiResponseDto.fail(e.getMessage())); // sendUserIdToEmail에서 실어둔 메세지가 실림
        } catch (MailException e) {
            log.error("이메일 전송 중 서버 오류 발생: {}", e.getMessage(), e);
            String errorMessage = "이메일 전송중 오류가 발생했습니다. 다시 시도해주세요";
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.fail(errorMessage));
        }
        catch (RuntimeException e) {
            // EmailService에서 메일 발송 실패 등 기타 런타임 예외 처리
            log.error("아이디 찾기 중 서버 오류 발생: {}", e.getMessage(), e); // 스택 트레이스 포함 로깅
            String errorMessage = "아이디 찾기 중 오류가 발생했습니다.";
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500 Internal Server Error
                    .body(ApiResponseDto.fail(errorMessage));
        }
   }
    @PostMapping("/reset-pw")
    public ResponseEntity<ApiResponseDto> resetPassword(@Valid @RequestBody FindIdRequestDto requestDto){

        try {
            userService.sendUserPwdToEmail(requestDto);
            //성공시
            String successMessage = "비밀번호 정보가 [" + requestDto.getEmail() + "] 주소로 발송되었습니다. 메일을 확인해주세요.";
            log.info(successMessage);
            return ResponseEntity
                    .ok(ApiResponseDto.success(successMessage));

        }catch (EntityNotFoundException e) {
            log.warn("비밀번호 리셋 - 유저 찾기 실패 (사용자 없음): {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.fail(e.getMessage()));
        }catch (MailException e) { // 런타임보다 전에 선언돼야함. 런타임 안에 메일익셉션 있음.
            log.error("이메일 전송 중 서버 오류 발생: {}", e.getMessage(), e);
            String errorMessage = "이메일 전송중 오류가 발생했습니다. 다시 시도해주세요";
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.fail(errorMessage));
        }catch (RuntimeException e) {
            log.error("비밀번호 리셋 중 오류 발생 : {}", e.getMessage(), e);
            String errorMessage = "아이디 찾기 처리 중 오류가 발생했습니다.";
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.fail(errorMessage));
        }
    }


}