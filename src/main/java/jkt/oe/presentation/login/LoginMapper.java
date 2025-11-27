package jkt.oe.presentation.login;

import jkt.oe.application.authentication.login.port.in.dto.LoginCommand;
import jkt.oe.application.authentication.login.port.in.dto.LoginResult;
import jkt.oe.presentation.login.dto.LoginRequest;
import jkt.oe.presentation.login.dto.LoginResponse;

/**
 * 로그인 관련 DTO 변환 Mapper
 */
public class LoginMapper {

    /**
     * 로그인 요청 DTO를 로그인 커맨드로 변환
     * 
     * @param request - 로그인 요청 DTO
     * @return 로그인 커맨드
     */
    public LoginCommand toCommand(LoginRequest request) {
        return new LoginCommand(request.getLoginId(), request.getPassword());
    }

    /**
     * 로그인 결과를 로그인 응답 DTO로 변환
     * 
     * @param loginResult - 로그인 결과
     * @return 로그인 응답 DTO
     */
    public LoginResponse toResponse(LoginResult loginResult) {
        return new LoginResponse(loginResult.getMemberId(), loginResult.getLoginId(), loginResult.getAccessToken(),
                loginResult.getRefreshToken());
    }
}
