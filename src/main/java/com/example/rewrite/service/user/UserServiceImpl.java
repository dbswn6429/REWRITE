package com.example.rewrite.service.user;

import com.example.rewrite.command.UserVO;
import com.example.rewrite.command.user.ApiResponseDto;
import com.example.rewrite.command.user.FindIdRequestDto;
import com.example.rewrite.command.user.SignupRequestDto;
import com.example.rewrite.command.user.UserDTO;
import com.example.rewrite.entity.Cart;
import com.example.rewrite.entity.Orders;
import com.example.rewrite.entity.Product;
import com.example.rewrite.entity.Users;
import com.example.rewrite.repository.address.AddressRepository;
import com.example.rewrite.repository.cart.CartRepository;
import com.example.rewrite.repository.order.OrderRepository;
import com.example.rewrite.repository.ordercart.OrderCartRepository;
import com.example.rewrite.repository.product.ProductRepository;
import com.example.rewrite.repository.qna.QnaRepository;
import com.example.rewrite.repository.review.ReviewRepository;
import com.example.rewrite.repository.users.UsersRepository;
import com.example.rewrite.repository.wishlist.WishlistRepository;
import com.example.rewrite.service.cart.CartService;
import com.example.rewrite.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Slf4j
@Service("UserService")
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션, 쓰기 작업 메소드에 @Transactional 추가
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;
    private final QnaRepository qnaRepository;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final OrderCartRepository ordersCartRepository;

    @Override
    @Transactional
    public Users signup(SignupRequestDto signupRequestDto) {

        if (usersRepository.existsById(signupRequestDto.getId())) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다");
        }
        if (usersRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다");
        }
        if (usersRepository.existsByNickname(signupRequestDto.getNickname())) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다");
        }
        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPw());

        Users user = Users.builder()
                .id(signupRequestDto.getId())
                .pw(encodedPassword)
                .name(signupRequestDto.getName())
                .nickname(signupRequestDto.getNickname())
                .email(signupRequestDto.getEmail())
                .birth(signupRequestDto.getBirth())
                .phone(signupRequestDto.getPhone())
                .build();

        return usersRepository.save(user);
    }

    @Override
    public User loadUserByUsername(String id) throws UsernameNotFoundException { //로그인에 쓰는 메서드
        Users user = usersRepository.findById(id) // 아이디가 있는지 체크함
                .orElseThrow(() -> new UsernameNotFoundException(id + "사용자를 찾을 수 없습니다.")); // 없으면 에러던짐

        List<GrantedAuthority> authorities = Collections //권한처리, GrantedAuthority = 사용자 권한 표현 인터페이스
                .singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()));

        // 싱글톤리스트 쓰는 이유
        // 1. 스프링 시큐리티 User객체 = 무조건 Collection으로 권한을 받도록 만들어짐
        // 2. 대부분 하나의 권한만 주는데, 그럴때 효과적임
        // 3. 코드가 간결해지고, 효율성 좋고, 불변성임.
        // 하지만 여러 권한이 필요한 경우에는 ArrayList를 사용해야함 (한명이 여러개의 권한을 동적으로 가지는 경우)
        return new User(user.getId(), user.getPw(), authorities);
    }

    @Transactional(readOnly = true) // 조회 작업이므로 readOnly
    public void sendUserIdToEmail(FindIdRequestDto requestDto) {

        //사용자 존재 체크 + 유저 불러오기
        Users foundUser = usersRepository.findByNameAndPhoneAndEmail(
                requestDto.getName(),
                requestDto.getPhone(),
                requestDto.getEmail()
        ).orElseThrow(() -> new EntityNotFoundException("입력한 정보와 일치하는 유저가 없습니다22."));

        // 사용자가 존재하면 EmailService를 통해 아이디 발송
        mailService.sendUserIdByEmail(foundUser.getEmail(), foundUser.getId());
    }


    @Override
    public boolean checkUserByIdAndEmailAndPhoneAndPassword(FindIdRequestDto requestDto) {
        Optional<Users> user = usersRepository.findByIdAndNameAndPhoneAndEmail(
                requestDto.getId(),
                requestDto.getName(),
                requestDto.getPhone(),
                requestDto.getEmail()
        );
        return user.isPresent(); // 존재시 true 반환
    }

    @Override
    @Transactional
    public void sendUserPwdToEmail(FindIdRequestDto requestDto) {

        // 사용자 조회
        Users foundUser = usersRepository.findByIdAndNameAndPhoneAndEmail(
                requestDto.getId(),
                requestDto.getName(),
                requestDto.getPhone(),
                requestDto.getEmail()
            ).orElseThrow(() -> new EntityNotFoundException("해당 정보로 가입된 사용자를 찾을 수 없습니다."));

        String tempPassword = UUID.randomUUID().toString().substring(0, 10);
        String encodedPassword = passwordEncoder.encode(tempPassword);


        // 메일로 임시비밀번호 전송
        mailService.sendUserIdPasswordByEmail(foundUser.getEmail(), foundUser.getId(), tempPassword);

        foundUser.setPw(encodedPassword);
        log.info("비밀번호 변경 - 바뀐 비밀번호" + foundUser.getPw());
        usersRepository.save(foundUser);
    }

    @Override
    public boolean checkUserByNameAndPhoneAndEmail(FindIdRequestDto requestDto) {
        return false;
    }

    @Override
    public Users getUserInfo(Long uid) {
        return usersRepository.findUserByUid(uid);
    }

    //회원정보 수정
    @Override
    @Transactional
    public void userModify(UserVO user) {
        // 비밀번호가 입력된 경우에만 암호화하여 저장
        if(user.getPw() != null && !user.getPw().isEmpty()) {
            user.setPw(passwordEncoder.encode(user.getPw()));
        }

        usersRepository.userModify(user);
    }

    //회원탈퇴
    @Override
    @Transactional
    public void userDelete(Long uid) {
        String genId = "deleted_" + UUID.randomUUID().toString().substring(0, 8);
        usersRepository.userDelete(uid, genId);
    }

    //마이페이지 판매수
    @Override
    public String sellCount(Long uid) {
        return usersRepository.sellCount(uid);
    }

    //마이페이지 프로필 조회
    @Override
    public Users getProfile(Long uid) {
        return usersRepository.findUserByUid(uid);
    }

    //마이페이지 판매내역 조회
    @Override
    public List<Product> getSellProd(Long uid) {
        // PageRequest.of(페이지 번호, 페이지당 항목 수)
        Pageable pageable = PageRequest.of(0, 4); // 첫 번째 페이지에서 4개 항목
        Page<Product> productPage = usersRepository.getSellProd(uid, pageable);
        return productPage.getContent(); // Page에서 List로 변환
    }

    @Override
    public List<UserDTO> findUsers(String search, String role) {
        List<Users> usersList = usersRepository.searchUsers(search, role);

        System.out.println("유저디티오 디버깅");

        List<UserDTO> userDTOList = new ArrayList<>();
        for (Users users : usersList) {
            System.out.println(users);
            UserDTO user = UserDTO.fromEntity(users);
            userDTOList.add(user);
        }
        for (UserDTO userDTO : userDTOList) {

            System.out.println(userDTO.toString());
        }

        return userDTOList;
    }

    @Override
    @Transactional
    public void changeRole(Long uid, String role) {
        Users user = usersRepository.findUserByUid(uid);
        user.setRole(role);
        usersRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long uid) {
        Users user = usersRepository.findUserByUid(uid);

        if (user != null) {

            // 1. 사용자가 등록한 상품과 관련된 다른 레코드들 삭제
            List<Product> products = productRepository.findProductsByUserUid(uid);
            for (Product product : products) {
                // 다른 유저의 장바구니에 담긴 내 상품 삭제
                cartRepository.deleteByProduct(product);
                // 다른 유저가 내 상품을 찜한 목록 삭제
                wishlistRepository.deleteByProduct(product);
                // 내 상품에 달린 리뷰 삭제 (정책에 따라 다를 수 있음)
                reviewRepository.deleteByProduct(product);
                // 내 상품이 포함된 주문 상세 내역 삭제
                ordersCartRepository.deleteByProduct(product);
            }

            // 2. 사용자와 직접 관련된 레코드 삭제
            // 2-1. 장바구니 삭제 (내 장바구니)
            cartRepository.deleteByUserUid(uid); // User FK 기준

            // 2-2. 찜 목록 삭제
            wishlistRepository.deleteByUserUid(uid);

            // 2-3. 리뷰 삭제 (내가 쓴 리뷰)
            reviewRepository.deleteByUserUid(uid);


            // 2-5. 주문 및 주문 상세 삭제
            List<Orders> orders = orderRepository.findByBuyerUid(uid);
            for (Orders order : orders) {
                ordersCartRepository.deleteByOrdersOrderId(order.getOrderId()); // 수정된 호출
            }
            orderRepository.deleteByBuyerUid(uid); // User FK 기준

            // 2-6. 주소 삭제
            addressRepository.deleteByUserUid(uid); // User FK 기준 (가정)

            // 3. 사용자 상품 삭제
            productRepository.deleteByUserUid(uid); // User FK 기준

            // 4. 사용자 삭제
            usersRepository.delete(user);

            // 참고: cartRepository.findCartsByProduct 및 deleteAll(carts) 로직은
            // cartRepository.deleteByProduct(product) 로 대체 가능할 수 있습니다.
            // 기존 로직 (`List<Cart> carts = cartRepository.findCartsByProduct(product); cartRepository.deleteAll(carts);`) 은
            // deleteByProduct(product) 가 동일한 기능을 수행한다면 제거해도 됩니다.
            // 마찬가지로 기존 `cartRepository.deleteByUserUid(uid);` 호출 위치는
            // 관련된 모든 자식 데이터가 삭제된 후 User 삭제 직전으로 옮기는 것이 더 명확할 수 있습니다. (위 예시처럼)
        }
    }

    // 비밀번호 확인
    @Override
    public boolean checkCurrentPassword(Long uid, String password) {
        Users user = usersRepository.findUserByUid(uid);
        if(user != null) {
            return passwordEncoder.matches(password, user.getPw());
        }
        return false;
    }

    @Override
    public Integer buyCount(Long uid) {

        return orderRepository.findByBuyerUid(uid).size();
    }
}
