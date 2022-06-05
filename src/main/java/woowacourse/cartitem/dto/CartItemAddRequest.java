package woowacourse.cartitem.dto;

import javax.validation.constraints.NotNull;

import woowacourse.cartitem.domain.CartItem;
import woowacourse.cartitem.domain.Quantity;

public class CartItemAddRequest {

    @NotNull(message = "상품 id를 입력해 주세요.")
    private Long productId;

    @NotNull(message = "수량을 입력해 주세요.")
    private Integer quantity;

    private CartItemAddRequest() {
    }

    public CartItemAddRequest(final Long productId, final Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public CartItem toCartItem(final Long customerId) {
        return new CartItem(customerId, productId, new Quantity(quantity));
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
