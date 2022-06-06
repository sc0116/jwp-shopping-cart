package woowacourse.cartitem.ui;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import woowacourse.auth.support.AuthenticationPrincipal;
import woowacourse.cartitem.application.CartItemService;
import woowacourse.cartitem.dto.CartItemAddRequest;
import woowacourse.cartitem.dto.CartItemResponses;

@RequestMapping("/api/cartItems")
@RestController
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(final CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping
    public ResponseEntity<CartItemResponses> getCartItems(@AuthenticationPrincipal final String customerName) {
        return ResponseEntity.ok().body(cartItemService.findCartsByCustomerName(customerName));
    }

    @PostMapping
    public ResponseEntity<Void> addCartItem(
        @AuthenticationPrincipal final String username,
        @Valid @RequestBody final CartItemAddRequest cartItemAddRequest
    ) {
        final Long cartItemId = cartItemService.addCartItem(username, cartItemAddRequest);
        return ResponseEntity.created(URI.create("/api/cartItems/" + cartItemId)).build();
    }

    @PatchMapping("/{cartItemId}")
    public ResponseEntity<Void> updateQuantity(
        @AuthenticationPrincipal final String username,
        @PathVariable final Long cartItemId,
        @RequestParam final Integer quantity
    ) {
        cartItemService.updateQuantity(username, cartItemId, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(
        @AuthenticationPrincipal final String username,
        @PathVariable final Long cartItemId
    ) {
        cartItemService.deleteCart(username, cartItemId);
        return ResponseEntity.noContent().build();
    }
}
