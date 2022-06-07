package woowacourse.customer.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import woowacourse.customer.dto.CustomerResponse;
import woowacourse.auth.dto.LoginRequest;
import woowacourse.auth.dto.TokenResponse;
import woowacourse.customer.dto.PasswordConfirmRequest;
import woowacourse.customer.dto.SignupRequest;
import woowacourse.customer.dto.UpdateCustomerRequest;
import woowacourse.customer.dto.UpdatePasswordRequest;
import woowacourse.shoppingcart.acceptance.AcceptanceTest;
import woowacourse.shoppingcart.dto.ExceptionResponse;

@DisplayName("회원 관련 기능")
public class CustomerAcceptanceTest extends AcceptanceTest {

    private final String username = "username";
    private final String password = "password1234";

    @DisplayName("회원가입")
    @Test
    void addCustomer() {
        // given
        final SignupRequest signupRequest = new SignupRequest(username, password, "01001012323", "인천 서구 검단로");

        // when
        final ExtractableResponse<Response> response = 회원_가입_요청(signupRequest);

        // then
        회원_가입됨(response);
    }

    @DisplayName("회원가입 시 username은 3자 이상 ~ 15자 이하로만 이루어져 있어야 된다.")
    @Test
    void validateUsernameLength() {
        // given
        final SignupRequest signupRequest = new SignupRequest("do", password, "01012123434", "인천 서구 검단로");

        // when
        final ExtractableResponse<Response> response = 회원_가입_요청(signupRequest);
        final ExceptionResponse exceptionResponse = response.as(ExceptionResponse.class);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(exceptionResponse.getMessages())
                .hasSize(1)
                .containsExactly("사용자 이름의 길이는 3자 이상 15자 이하여야 합니다.")
        );
    }

    @DisplayName("비밀번호는 8자 이상 ~ 20자 이하로만 이루어져 있어야 한다.")
    @Test
    void validateFields() {
        // given
        final SignupRequest signupRequest = new SignupRequest("domain", "a", "01011112222", "인천 서구");

        // when
        final ExtractableResponse<Response> response = 회원_가입_요청(signupRequest);
        final ExceptionResponse exceptionResponse = response.as(ExceptionResponse.class);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(exceptionResponse.getMessages()).contains("비밀번호의 길이는 8자 이상 20자 이하여야 합니다.")
        );
    }

    @DisplayName("회원가입 시 이미 존재하는 username을 사용하면 예외를 반환해야 한다.")
    @Test
    void validateUniqueUsername() {
        // given
        final SignupRequest signupRequest = new SignupRequest("jjang9", "password1234", "01012121212", "서울시 여러분");
        회원_가입_요청(signupRequest);

        // when
        final ExtractableResponse<Response> response = 회원_가입_요청(signupRequest);
        final ExceptionResponse exceptionResponse = response.as(ExceptionResponse.class);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(exceptionResponse.getMessages())
                .hasSize(1)
                .containsExactly("이미 존재하는 username입니다.")
        );
    }

    @DisplayName("내 정보 조회")
    @Test
    void getMe() {
        // given
        final SignupRequest signupRequest = new SignupRequest(username, password, "01022728572", "인천 서구 검단로");
        회원_가입_요청(signupRequest);

        final LoginRequest loginRequest = new LoginRequest(username, password);
        final String accessToken = 로그인되어_토큰_가져옴(loginRequest);

        // when
        final CustomerResponse customerResponse = findCustomerInfo(accessToken);

        // then
        assertAll(
            () -> assertThat(customerResponse.getUsername()).isEqualTo(signupRequest.getUsername()),
            () -> assertThat(customerResponse.getPhoneNumber()).isEqualTo(signupRequest.getPhoneNumber()),
            () -> assertThat(customerResponse.getAddress()).isEqualTo(signupRequest.getAddress())
        );
    }

    @DisplayName("비밀밀호가 일치하는지 확인한다.")
    @Test
    void confirmPassword() {
        final SignupRequest signupRequest = new SignupRequest(username, password, "01022728572", "인천 서구 검단로");
        회원_가입_요청(signupRequest);

        final LoginRequest loginRequest = new LoginRequest(username, password);
        final String accessToken = 로그인되어_토큰_가져옴(loginRequest);

        final PasswordConfirmRequest passwordConfirmRequest = new PasswordConfirmRequest(password);
        RestAssured.given().log().all()
            .auth().oauth2(accessToken)
            .body(passwordConfirmRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/api/customers/password")
            .then().log().all()
            .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("phoneNumber, address 수정")
    @Test
    void updateMe() {
        // given
        final SignupRequest signupRequest = new SignupRequest(username, password, "01022728572", "인천 서구 검단로");
        회원_가입_요청(signupRequest);

        final LoginRequest loginRequest = new LoginRequest(username, password);
        final String accessToken = 로그인되어_토큰_가져옴(loginRequest);

        UpdateCustomerRequest updateCustomerRequest = new UpdateCustomerRequest("01011112222", "서울시 강남구");
        ValidatableResponse validatableResponse = RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .body(updateCustomerRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/api/customers")
            .then().log().all();

        final CustomerResponse customerResponse = findCustomerInfo(accessToken);

        assertAll(
            () -> validatableResponse.statusCode(HttpStatus.NO_CONTENT.value()),
            () -> assertThat(customerResponse.getPhoneNumber()).isEqualTo(updateCustomerRequest.getPhoneNumber()),
            () -> assertThat(customerResponse.getAddress()).isEqualTo(updateCustomerRequest.getAddress())
        );
    }

    @DisplayName("password 수정")
    @Test
    void updatePassword() {
        // given
        final SignupRequest signupRequest = new SignupRequest(username, password, "01022728572", "인천 서구 검단로");
        회원_가입_요청(signupRequest);

        final LoginRequest loginRequest = new LoginRequest(username, password);
        final String accessToken = 로그인되어_토큰_가져옴(loginRequest);

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest("password1234");
        RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .body(updatePasswordRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().patch("/api/customers/password")
            .then().log().all().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("회원탈퇴")
    @Test
    void deleteMe() {
        // given
        final SignupRequest signupRequest = new SignupRequest(username, password, "01022728572", "인천 서구 검단로");
        회원_가입_요청(signupRequest);

        final LoginRequest loginRequest = new LoginRequest(username, password);
        final String accessToken = 로그인되어_토큰_가져옴(loginRequest);

        RestAssured.given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/api/customers")
            .then().log().all()
            .extract();

        RestAssured
            .given().log().all()
            .body(new LoginRequest(username, password))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/api/customers/login")
            .then().log().all().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    public static ExtractableResponse<Response> 회원_가입_요청(final SignupRequest signupRequest) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(signupRequest)
            .when().post("/api/customers/signup")
            .then().log().all()
            .extract();
    }

    public static String 로그인되어_토큰_가져옴(final LoginRequest loginRequest) {
        return RestAssured
            .given().log().all()
            .body(loginRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/api/customers/login")
            .then().log().all().extract().as(TokenResponse.class).getAccessToken();
    }

    private CustomerResponse findCustomerInfo(final String accessToken) {
        return RestAssured
            .given().log().all()
            .auth().oauth2(accessToken)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/api/customers")
            .then().log().all()
            .statusCode(HttpStatus.OK.value()).extract().as(CustomerResponse.class);
    }

    public static void 회원_가입됨(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
