package com.SWP391.G3PCoffee;

import com.SWP391.G3PCoffee.model.UserLoginDto;
import com.SWP391.G3PCoffee.model.UserRegisterDto;
import com.SWP391.G3PCoffee.model.Role;
import com.SWP391.G3PCoffee.model.User;
import com.SWP391.G3PCoffee.repository.UserRepository;
import com.SWP391.G3PCoffee.security.JwtUtils;
import com.SWP391.G3PCoffee.service.UserService;
import com.SWP391.G3PCoffee.service.member_ship.MembershipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private MembershipService membershipService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDetails testUserDetails;
    private UserRegisterDto testRegisterDto;
    private UserLoginDto testLoginDto;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPhone("1234567890");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.CUSTOMER.getValue());
        testUser.setAddress("123 Test St");

        // Setup test user details
        testUserDetails = org.springframework.security.core.userdetails.User.builder()
                .username("test@example.com")
                .password("encodedPassword")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(Role.CUSTOMER.getValue())))
                .build();

        // Setup test register DTO
        testRegisterDto = new UserRegisterDto();
        testRegisterDto.setFullName("Test User");
        testRegisterDto.setEmail("test@example.com");
        testRegisterDto.setPhone("1234567890");
        testRegisterDto.setPassword("password123");

        // Setup test login DTO
        testLoginDto = new UserLoginDto();
        testLoginDto.setEmail("test@example.com");
        testLoginDto.setPassword("password123");
    }

    @Test
    void getAllCustomers_ReturnsOnlyCustomers() {
        // Arrange
        User customer1 = new User();
        customer1.setRole("customer");

        User customer2 = new User();
        customer2.setRole("customer");

        User admin = new User();
        admin.setRole("admin");

        List<User> allUsers = Arrays.asList(customer1, customer2, admin);

        when(userRepository.findAll()).thenReturn(allUsers);

        // Act
        List<User> result = userService.getAllCustomers();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(user -> "customer".equalsIgnoreCase(user.getRole())));
    }
    // Kiểm tra phương thức getCustomerById trả về đúng khách hàng khi ID tồn tại
    @Test
    void getCustomerById_CustomerExists_ReturnsCustomer() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getCustomerById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
    }

    // Kiểm tra phương thức getCustomerById trả về null khi ID không tồn tại
    @Test
    void getCustomerById_UserNotFound_ReturnsNull() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        User result = userService.getCustomerById(999L);

        // Assert
        assertNull(result);
    }

    // Kiểm tra đăng ký người dùng thành công
    // Xác minh rằng người dùng được lưu vào DB và token JWT được trả về
    @Test
    void registerUser_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByPhone(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(testUserDetails);
        when(jwtUtils.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        // Act
        String token = userService.registerUser(testRegisterDto);

        // Assert
        assertNotNull(token);
        assertEquals("jwt-token", token);
        verify(userRepository, times(1)).save(any(User.class));
        verify(membershipService, times(1)).initMemberShip(any(User.class));
    }
    // Kiểm tra đăng ký thất bại khi email đã tồn tại
    // Đảm bảo ngoại lệ được ném ra và không có người dùng được lưu
    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.registerUser(testRegisterDto));
        verify(userRepository, times(0)).save(any(User.class));
    }
    // Kiểm tra đăng ký thất bại khi số điện thoại đã tồn tại
    // Đảm bảo ngoại lệ được ném ra và không có người dùng được lưu
    @Test
    void registerUser_PhoneAlreadyExists_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByPhone("1234567890")).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.registerUser(testRegisterDto));
        verify(userRepository, times(0)).save(any(User.class));
    }

    // Kiểm tra đăng nhập thành công
    // Xác minh rằng token JWT và vai trò được trả về đúng
    @Test
    void loginByEmailAndPassword_Success() {
        // Arrange
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(testUserDetails);
        when(jwtUtils.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        // Act
        Map<String, String> result = userService.loginByEmailAndPassword(testLoginDto);

        // Assert
        assertNotNull(result);
        assertEquals("jwt-token", result.get("token"));
        assertEquals(Role.CUSTOMER.getValue(), result.get("role"));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    // Kiểm tra đăng nhập thất bại khi cung cấp thông tin đăng nhập sai
    // Đảm bảo ngoại lệ được ném ra với mã trạng thái UNAUTHORIZED
    @Test
    void loginByEmailAndPassword_BadCredentials_ThrowsException() {
        // Arrange
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.loginByEmailAndPassword(testLoginDto));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    // Kiểm tra cập nhật thông tin người dùng thành công
    // Xác minh rằng thông tin mới được lưu và trả về đúng
    @Test
    void updateUser_Success() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setName("Updated Name");
        updatedUser.setPhone("9876543210");
        updatedUser.setAddress("456 New St");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.updateUser(testUserDetails, updatedUser);

        // Assert
        assertNotNull(result);
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(updatedUser.getPhone(), result.getPhone());
        assertEquals(updatedUser.getAddress(), result.getAddress());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // Kiểm tra cập nhật thất bại khi không tìm thấy người dùng
    // Đảm bảo không có thông tin nào được lưu và kết quả trả về là null
    @Test
    void updateUser_UserNotFound_ReturnsNull() {
        // Arrange
        User updatedUser = new User();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act
        User result = userService.updateUser(testUserDetails, updatedUser);

        // Assert
        assertNull(result);
        verify(userRepository, times(0)).save(any(User.class));
    }









// phần làm thêm

    // Kiểm tra phương thức getCustomerByEmail trả về đúng khách hàng khi email tồn tại
    @Test
    void getCustomerByEmail_CustomerExists_ReturnsCustomer() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getCustomerByEmail("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
    }

    // Kiểm tra phương thức getCustomerByEmail trả về null khi email không tồn tại

    @Test
    void getCustomerByEmail_UserNotFound_ReturnsNull() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        User result = userService.getCustomerByEmail("nonexistent@example.com");

        // Assert
        assertNull(result);
    }

    // Kiểm tra đổi mật khẩu thành công
    // Xác minh rằng mật khẩu mới được mã hóa và lưu vào DB
    @Test
    void changePassword_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");

        // Act
        Map<String, String> result = userService.changePassword(
                "oldPassword", "newPassword123", "newPassword123", "test@example.com");

        // Assert
        assertEquals("Đổi mật khẩu thành công!", result.get("message"));
        assertEquals("success", result.get("messageType"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    // Kiểm tra đổi mật khẩu thất bại khi có trường trống
    // Đảm bảo thông báo lỗi được trả về và không có mật khẩu nào được thay đổi
    @Test
    void changePassword_EmptyFields_ReturnsError() {
        // Act
        Map<String, String> result = userService.changePassword(
                "", "newPassword123", "newPassword123", "test@example.com");

        // Assert
        assertEquals("Vui lòng điền đầy đủ tất cả các trường!", result.get("message"));
        assertEquals("error", result.get("messageType"));
        verify(userRepository, times(0)).save(any(User.class));
    }

    // Kiểm tra đổi mật khẩu thất bại khi mật khẩu cũ không đúng
    // Đảm bảo thông báo lỗi được trả về và không có mật khẩu nào được thay đổi
    @Test
    void changePassword_IncorrectOldPassword_ReturnsError() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act
        Map<String, String> result = userService.changePassword(
                "wrongPassword", "newPassword123", "newPassword123", "test@example.com");

        // Assert
        assertEquals("Mật khẩu cũ không đúng!", result.get("message"));
        assertEquals("error", result.get("messageType"));
        verify(userRepository, times(0)).save(any(User.class));
    }

    // Kiểm tra đổi mật khẩu thất bại khi mật khẩu mới yếu
    // Đảm bảo thông báo lỗi được trả về và không có mật khẩu nào được thay đổi
    @Test
    void changePassword_WeakNewPassword_ReturnsError() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

        // Act
        Map<String, String> result = userService.changePassword(
                "oldPassword", "weak", "weak", "test@example.com");

        // Assert
        assertEquals("Mật khẩu mới phải có ít nhất 6 ký tự và bao gồm cả chữ và số!", result.get("message"));
        assertEquals("error", result.get("messageType"));
        verify(userRepository, times(0)).save(any(User.class));
    }

    // Kiểm tra đổi mật khẩu thất bại khi mật khẩu mới và xác nhận không khớp
    // Đảm bảo thông báo lỗi được trả về và không có mật khẩu nào được thay đổi
    @Test
    void changePassword_PasswordsDoNotMatch_ReturnsError() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

        // Act
        Map<String, String> result = userService.changePassword(
                "oldPassword", "newPassword123", "differentPassword123", "test@example.com");

        // Assert
        assertEquals("Mật khẩu mới không khớp!", result.get("message"));
        assertEquals("error", result.get("messageType"));
        verify(userRepository, times(0)).save(any(User.class));
    }
    // Kiểm tra đổi mật khẩu thất bại khi không tìm thấy người dùng
    // Đảm bảo thông báo lỗi được trả về và không có mật khẩu nào được thay đổi
    @Test
    void changePassword_UserNotFound_ReturnsError() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act
        Map<String, String> result = userService.changePassword(
                "oldPassword", "newPassword123", "newPassword123", "test@example.com");

        // Assert
        assertEquals("Mật khẩu cũ không đúng!", result.get("message"));
        assertEquals("error", result.get("messageType"));
        verify(userRepository, times(0)).save(any(User.class));
    }
}