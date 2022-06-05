package woowacourse.auth.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import woowacourse.auth.dto.LoginRequest;
import woowacourse.auth.dto.TokenResponse;
import woowacourse.auth.support.JwtTokenProvider;
import woowacourse.customer.application.CustomerService;
import woowacourse.customer.domain.Customer;
import woowacourse.customer.support.passwordencoder.PasswordEncoder;

@Transactional
@Service
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
        final JwtTokenProvider jwtTokenProvider,
        final CustomerService customerService,
        final PasswordEncoder passwordEncoder
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customerService = customerService;
        this.passwordEncoder = passwordEncoder;
    }

    public TokenResponse createToken(final LoginRequest loginRequest) {
        validateLogin(loginRequest);
        final String accessToken = jwtTokenProvider.createToken(loginRequest.getUsername());
        return new TokenResponse(accessToken);
    }

    private void validateLogin(final LoginRequest loginRequest) {
        final Customer customer = customerService.findByUsername(loginRequest.getUsername());
        customer.getPassword().matches(passwordEncoder, loginRequest.getPassword());
    }
}
