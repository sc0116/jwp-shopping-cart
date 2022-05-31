package woowacourse.shoppingcart.ui;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import woowacourse.shoppingcart.dto.UpdateCustomerRequest;
import woowacourse.shoppingcart.application.CustomerService;
import woowacourse.shoppingcart.domain.customer.Customer;
import woowacourse.shoppingcart.dto.SignupRequest;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest signupRequest) {
        Customer customer = customerService.save(signupRequest);
        return ResponseEntity.created(URI.create("/api/customers/" + customer.getId())).build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody UpdateCustomerRequest updateCustomerRequest,
        HttpServletRequest request) {
        customerService.update((String)request.getAttribute("username"), updateCustomerRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT.value()).build();
    }
}
