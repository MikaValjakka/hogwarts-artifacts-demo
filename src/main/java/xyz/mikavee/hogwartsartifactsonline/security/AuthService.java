package xyz.mikavee.hogwartsartifactsonline.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import xyz.mikavee.hogwartsartifactsonline.hogwartsuser.HogwartsUser;
import xyz.mikavee.hogwartsartifactsonline.hogwartsuser.MyUserPrincipal;
import xyz.mikavee.hogwartsartifactsonline.hogwartsuser.converter.UserToUserDtoConverter;
import xyz.mikavee.hogwartsartifactsonline.hogwartsuser.dto.UserDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final JwtProvider jwtProvider;
    private final UserToUserDtoConverter userToUserDtoConverter;

    public AuthService(JwtProvider jwtProvider, UserToUserDtoConverter userToUserDtoConverter) {
        this.jwtProvider = jwtProvider;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    public Map<String, Object> createLoginInfo(Authentication authentication) {
        // Create user info
        MyUserPrincipal principal = (MyUserPrincipal)authentication.getPrincipal();
        HogwartsUser hogwartsUser = principal.getHogwartsUser();
        UserDto userDto = this.userToUserDtoConverter.convert(hogwartsUser);

        // Create JWT
        String token = this.jwtProvider.createToken(authentication);


        // Create and put userDto token to key/value pairs
        Map<String, Object> loginResultMap = new HashMap<>();
        loginResultMap.put("userInfo", userDto);
        loginResultMap.put("token", token);

        return loginResultMap;
    }
}
