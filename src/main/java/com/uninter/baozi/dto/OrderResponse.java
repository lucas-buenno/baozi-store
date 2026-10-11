package com.uninter.baozi.dto;

import com.uninter.baozi.model.ClientEntity;
import com.uninter.baozi.model.OrderEntity;
import com.uninter.baozi.model.OrderItemEntity;
import com.uninter.baozi.model.ProductEntity;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        Long id,
        Client client,
        List<OrderItem> orderItems,
        BigDecimal totalPrice
) {

    public static OrderResponse from(OrderEntity order) {
        List<OrderItem> items = order.getOrderItems() == null
                ? List.of()
                : order.getOrderItems().stream().map(OrderItem::from).toList();

        return new OrderResponse(
                order.getId(),
                Client.from(order.getClient()),
                items,
                order.getTotalPrice()
        );
    }

    public record Client(Long id, String name, String phoneNumber) {
        private static Client from(ClientEntity client) {
            return new Client(client.getId(), client.getName(), client.getPhoneNumber());
        }
    }

    public record Product(Long id, String productName) {
        private static Product from(ProductEntity product) {
            return new Product(product.getId(), product.getProductName());
        }
    }

    public record OrderItem(Long id, Product product, Integer quantity, BigDecimal price) {
        private static OrderItem from(OrderItemEntity item) {
            return new OrderItem(
                    item.getId(),
                    Product.from(item.getProduct()),
                    item.getQuantity(),
                    item.getPrice()
            );
        }
    }
}
