package xyz.mikavee.hogwartsartifactsonline.demosetup;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoTokenController {

    private final JwtEncoder jwtEncoder;

    public DemoTokenController(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @GetMapping("/fixed-token")
    public String generateFixedDemoToken() {
        Instant now = Instant.now();
        Instant exp = now.plus(365, ChronoUnit.DAYS); // 1 vuosi

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("demo") // tai sun baseUrl
                .subject("demo-recruiter")
                .audience(List.of("hogwarts-api"))
                .issuedAt(now)
                .expiresAt(exp)
                .claim("authorities", List.of("ROLE_USER", "ROLE_ADMIN")) // tai sun claim-nimi
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}