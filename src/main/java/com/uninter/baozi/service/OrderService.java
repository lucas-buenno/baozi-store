package com.uninter.baozi.service;

import com.uninter.baozi.dto.OrdemItemRequest;
import com.uninter.baozi.dto.OrderRequest;
import com.uninter.baozi.dto.OrderResponse;
import com.uninter.baozi.model.ClientEntity;
import com.uninter.baozi.model.OrderEntity;
import com.uninter.baozi.model.OrderItemEntity;
import com.uninter.baozi.model.ProductEntity;
import com.uninter.baozi.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientService clientService;
    private final ProductService productService;

    public OrderResponse createOrder(OrderRequest request) {
        ClientEntity client = findClientById(request.clientId());
        List<OrderItemEntity> orderItemEntities = mapOrderItems(request.orderItems());
        BigDecimal totalPrice = calculateTotalPrice(orderItemEntities);

        OrderEntity orderEntity = new OrderEntity()
                .setClient(client)
                .setOrderItems(orderItemEntities)
                .setTotalPrice(totalPrice);

        return OrderResponse.from(orderRepository.save(orderEntity));
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::from)
                .toList();
    }

    public OrderResponse getOrderById(Long orderId) {
        return OrderResponse.from(findOrderById(orderId));
    }

    public void deleteOrder(Long orderId) {
        orderRepository.delete(findOrderById(orderId));
    }

    private OrderEntity findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Não foi possível encontrar o pedido: " + orderId));
    }

    private ClientEntity findClientById(Long clientId) {
        return clientService.getClientById(clientId);
    }

    private List<OrderItemEntity> mapOrderItems(List<OrdemItemRequest> orderItemsRequest) {
        return orderItemsRequest.stream()
                .map(this::getOrderItemEntity)
                .toList();
    }

    private OrderItemEntity getOrderItemEntity(OrdemItemRequest itemRequest) {
        ProductEntity product = productService.getProductById(itemRequest.productId());
        return new OrderItemEntity()
                .setProduct(product)
                .setQuantity(itemRequest.quantity())
                .setPrice(product.getPrice());
    }

    private BigDecimal calculateTotalPrice(List<OrderItemEntity> orderItems) {
        return orderItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
