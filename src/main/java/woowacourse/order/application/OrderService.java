package woowacourse.order.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import woowacourse.cartitem.application.CartItemService;
import woowacourse.cartitem.domain.CartItem;
import woowacourse.customer.dao.CustomerDao;
import woowacourse.order.dao.OrderDao;
import woowacourse.order.dao.OrdersDetailDao;
import woowacourse.order.dto.OrderAddRequest;
import woowacourse.product.dao.ProductDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class OrderService {

    private final OrderDao orderDao;
    private final OrdersDetailDao ordersDetailDao;
    private final CartItemService cartItemService;
    private final CustomerDao customerDao;
    private final ProductDao productDao;

    public OrderService(final OrderDao orderDao, final OrdersDetailDao ordersDetailDao,
                        final CartItemService cartItemService, final CustomerDao customerDao, final ProductDao productDao) {
        this.orderDao = orderDao;
        this.ordersDetailDao = ordersDetailDao;
        this.cartItemService = cartItemService;
        this.customerDao = customerDao;
        this.productDao = productDao;
    }

    public Long addOrder(final String customerName, final List<OrderAddRequest> orderAddRequests) {
        final Long customerId = customerDao.findIdByUserName(customerName);
        final Long ordersId = orderDao.addOrders(customerId);

        for (final OrderAddRequest orderAddRequest : orderAddRequests) {
            final CartItem cartItem = cartItemService.findCartById(orderAddRequest.getCartItemId());

            ordersDetailDao.addOrdersDetail(ordersId, cartItem.getProductId(), cartItem.getQuantity().getValue());
            cartItemService.deleteCart(customerName, cartItem.getId());
        }

        return ordersId;
    }
    //
    // public Orders findOrderById(final String customerName, final Long orderId) {
    //     validateOrderIdByCustomerName(customerName, orderId);
    //     return findOrderResponseDtoByOrderId(orderId);
    // }
    //
    // private void validateOrderIdByCustomerName(final String customerName, final Long orderId) {
    //     final Long customerId = customerDao.findIdByUserName(customerName);
    //
    //     if (!orderDao.isValidOrderId(customerId, orderId)) {
    //         throw new InvalidOrderException("유저에게는 해당 order_id가 없습니다.");
    //     }
    // }
    //
    // public List<Orders> findOrdersByCustomerName(final String customerName) {
    //     final Long customerId = customerDao.findIdByUserName(customerName);
    //     final List<Long> orderIds = orderDao.findOrderIdsByCustomerId(customerId);
    //
    //     return orderIds.stream()
    //             .map(orderId -> findOrderResponseDtoByOrderId(orderId))
    //             .collect(Collectors.toList());
    // }
    //
    // private Orders findOrderResponseDtoByOrderId(final Long orderId) {
    //     final List<OrderDetail> ordersDetails = new ArrayList<>();
    //     for (final OrderDetail productQuantity : ordersDetailDao.findOrdersDetailsByOrderId(orderId)) {
    //         //final Product product = productDao.findProductById(productQuantity.getProductId());
    //         final int quantity = productQuantity.getQuantity();
    //         //ordersDetails.add(new OrderDetail(product, quantity));
    //     }
    //
    //     return new Orders(orderId, ordersDetails);
    // }
}
