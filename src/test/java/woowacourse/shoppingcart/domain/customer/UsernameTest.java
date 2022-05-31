package woowacourse.shoppingcart.domain.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UsernameTest {

    @DisplayName("username을 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"abc", "abcdefghijklmno"})
    void createUsername(final String value) {
        final Username username = new Username(value);

        assertThat(username.getValue()).isEqualTo(value);
    }

    @DisplayName("길이가 맞지 않는 username을 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"ab", "abcdefghijklmnop"})
    void createInvalidLengthUsername(final String value) {
        assertThatThrownBy(() -> new Username(value))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("패턴이 맞지 않는 username을 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"한글입니다", "@!&@#&!"})
    void createInvalidPatternUsername(final String value) {
        assertThatThrownBy(() -> new Username(value))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
