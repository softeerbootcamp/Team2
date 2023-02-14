package softeer.carbook.domain.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;
import softeer.carbook.domain.user.dto.LoginForm;
import softeer.carbook.domain.user.dto.ModifyPasswordForm;
import softeer.carbook.domain.user.exception.*;
import softeer.carbook.global.dto.Message;
import softeer.carbook.domain.user.dto.SignupForm;
import softeer.carbook.domain.user.model.User;
import softeer.carbook.domain.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signup_Success() {
        // Given
        SignupForm signupForm = new SignupForm("email@example.com", "password", "nickname");
        given(userRepository.isEmailDuplicated(any())).willReturn(false);
        given(userRepository.isNicknameDuplicated(any())).willReturn(false);

        // When
        Message result = userService.signup(signupForm);

        // Then
        assertThat(result.getMessage()).isEqualTo("SignUp Success");
        verify(userRepository).isEmailDuplicated(signupForm.getEmail());
        verify(userRepository).isNicknameDuplicated(signupForm.getNickname());
    }

    @Test
    @DisplayName("회원가입 중복된 이메일 입력될 경우")
    void signupEmailDuplicate() {
        // Given
        SignupForm signupForm = new SignupForm("email@example.com", "password", "nickname");
        given(userRepository.isEmailDuplicated(any())).willReturn(true);

        // When
        Throwable exception = assertThrows(SignupEmailDuplicateException.class, () -> {
            Message resultMsg = userService.signup(signupForm);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("ERROR: Duplicated email");
        verify(userRepository).isEmailDuplicated(signupForm.getEmail());
    }

    @Test
    @DisplayName("회원가입 중복된 닉네임 입력될 경우")
    void signupNicknameDuplicate() {
        // Given
        SignupForm signupForm = new SignupForm("email@example.com", "password", "nickname");
        given(userRepository.isEmailDuplicated(any())).willReturn(false);
        given(userRepository.isNicknameDuplicated(any())).willReturn(true);

        // When
        Throwable exception = assertThrows(NicknameDuplicateException.class, () -> {
            Message resultMsg = userService.signup(signupForm);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("ERROR: Duplicated nickname");
        verify(userRepository).isEmailDuplicated(signupForm.getEmail());
        verify(userRepository).isNicknameDuplicated(signupForm.getNickname());
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccess() {
        // Given
        LoginForm loginForm = new LoginForm("test@gmail.com",
                "password");
        User user = new User("test@gmail.com", "nickname",
                BCrypt.hashpw("password", BCrypt.gensalt()));
        given(userRepository.findUserByEmail(any())).willReturn(user);

        // When
        Message resultMsg = userService.login(loginForm, new MockHttpSession());

        // Then
        assertThat(resultMsg.getMessage()).isEqualTo("Login Success");
        verify(userRepository).findUserByEmail(loginForm.getEmail());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 이메일 없을 경우")
    void loginEmailNotExist() {
        // Given
        LoginForm loginForm = new LoginForm("test@gmail.com", "password");
        given(userRepository.findUserByEmail(any())).willThrow(new LoginEmailNotExistException());

        // When
        Throwable exception = assertThrows(LoginEmailNotExistException.class, () -> {
            Message resultMsg = userService.login(loginForm, new MockHttpSession());
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("ERROR: Email not exist");
        verify(userRepository).findUserByEmail(loginForm.getEmail());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호 불일치")
    void loginPasswordNotMatch() {
        // Given
        LoginForm loginForm = new LoginForm("test@gmail.com", "password");
        User user = new User("test@gmail.com", "nickname",
                BCrypt.hashpw("password123", BCrypt.gensalt()));
        given(userRepository.findUserByEmail(any())).willReturn(user);

        // When
        Throwable exception = assertThrows(PasswordNotMatchException.class, () -> {
            Message resultMsg = userService.login(loginForm, new MockHttpSession());
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("ERROR: Password not match");
        verify(userRepository).findUserByEmail(loginForm.getEmail());
    }

    @Test
    @DisplayName("닉네임 변경 테스트 - 성공")
    void modifyNicknameSuccess(){
        // Given
        String nickname = "nickname";
        String newNickname = "newnickname";
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", 14);
        httpServletRequest.setSession(session);

        given(userRepository.isNicknameDuplicated(nickname)).willReturn(true);
        given(userRepository.isNicknameDuplicated(newNickname)).willReturn(false);

        // When
        Message resultMsg = userService.modifyNickname(nickname, newNickname, httpServletRequest);

        // Then
        assertThat(resultMsg.getMessage()).isEqualTo("Nickname modified successfully");
        verify(userRepository).isNicknameDuplicated(nickname);
        verify(userRepository).isNicknameDuplicated(newNickname);
        verify(userRepository).modifyNickname(nickname, newNickname);
    }

    @Test
    @DisplayName("닉네임 변경 테스트 - 로그인 상태 아닐 때")
    void modifyNicknameNotLogin(){
        // Given
        String nickname = "nickname";
        String newNickname = "newnickname";
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

        // When
        Throwable exception = assertThrows(NotLoginStatementException.class, () -> {
            Message resultMsg = userService.modifyNickname(nickname, newNickname, httpServletRequest);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("ERROR: Session Has Expired");
    }

    @Test
    @DisplayName("닉네임 변경 테스트 - 기존 닉네임이 데이터베이스에 없을 때")
    void modifyNicknameNotExistNickname(){
        // Given
        String nickname = "nickname";
        String newNickname = "newnickname";
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", 14);
        httpServletRequest.setSession(session);

        given(userRepository.isNicknameDuplicated(nickname)).willReturn(false);

        // When
        Throwable exception = assertThrows(NicknameNotExistException.class, () -> {
            Message resultMsg = userService.modifyNickname(nickname, newNickname, httpServletRequest);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("ERROR: Nickname not exist");
        verify(userRepository).isNicknameDuplicated(nickname);
    }

    @Test
    @DisplayName("닉네임 변경 테스트 - 새로운 닉네임이 중복")
    void modifyNicknameDuplicatedNewNickname(){
        // Given
        String nickname = "nickname";
        String newNickname = "newnickname";
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", 14);
        httpServletRequest.setSession(session);

        given(userRepository.isNicknameDuplicated(nickname)).willReturn(true);
        given(userRepository.isNicknameDuplicated(newNickname)).willReturn(true);

        // When
        Throwable exception = assertThrows(NicknameDuplicateException.class, () -> {
            Message resultMsg = userService.modifyNickname(nickname, newNickname, httpServletRequest);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("ERROR: Duplicated nickname");
        verify(userRepository).isNicknameDuplicated(nickname);
        verify(userRepository).isNicknameDuplicated(newNickname);
    }

    @Test
    @DisplayName("비밀번호 변경 테스트 - 성공")
    void modifyPasswordSuccess(){
        // Given
        ModifyPasswordForm modifyPasswordForm = new ModifyPasswordForm("password", "newPassword");
        User user = new User("test@gmail.com", "nickname",
                BCrypt.hashpw("password", BCrypt.gensalt()));
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", 14);
        httpServletRequest.setSession(session);

        given(userRepository.findUserById(14)).willReturn(user);

        // When
        Message resultMsg = userService.modifyPassword(modifyPasswordForm, httpServletRequest);

        // Then
        assertThat(resultMsg.getMessage()).isEqualTo("Password modified successfully");
        verify(userRepository).findUserById(14);
    }

    @Test
    @DisplayName("비밀번호 변경 테스트 - 로그인 상태 아닐 때")
    void modifyPasswordNotLogin(){
        // Given
        ModifyPasswordForm modifyPasswordForm = new ModifyPasswordForm("password", "newPassword");
        User user = new User("test@gmail.com", "nickname",
                BCrypt.hashpw("password", BCrypt.gensalt()));
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

        // When
        Throwable exception = assertThrows(NotLoginStatementException.class, () -> {
            Message resultMsg = userService.modifyPassword(modifyPasswordForm, httpServletRequest);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("ERROR: Session Has Expired");
    }

    @Test
    @DisplayName("비밀번호 변경 테스트 - 기존 비밀번호와 맞지 않을 경우")
    void modifyPasswordNotMatchPassword(){
        // Given
        ModifyPasswordForm modifyPasswordForm = new ModifyPasswordForm("password12", "newPassword");
        User user = new User("test@gmail.com", "nickname",
                BCrypt.hashpw("password", BCrypt.gensalt()));
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", 14);
        httpServletRequest.setSession(session);

        given(userRepository.findUserById(14)).willReturn(user);

        // When
        Throwable exception = assertThrows(PasswordNotMatchException.class, () -> {
            Message resultMsg = userService.modifyPassword(modifyPasswordForm, httpServletRequest);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("ERROR: Password not match");
        verify(userRepository).findUserById(14);
    }



}
