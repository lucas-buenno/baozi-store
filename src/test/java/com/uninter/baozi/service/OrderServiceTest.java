package com.uninter.baozi.service;

import com.uninter.baozi.dto.OrdemItemRequest;
import com.uninter.baozi.dto.OrderRequest;
import com.uninter.baozi.dto.OrderResponse;
import com.uninter.baozi.model.ClientEntity;
import com.uninter.baozi.model.OrderEntity;
import com.uninter.baozi.model.OrderItemEntity;
import com.uninter.baozi.model.ProductEntity;
import com.uninter.baozi.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientService clientService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_shouldSaveOrderWithCalculatedTotal() {
        ClientEntity client = client();
        ProductEntity product = product();
        OrderRequest request = new OrderRequest(1L, List.of(new OrdemItemRequest(10L, 2)));

        when(clientService.getClientById(1L)).thenReturn(client);
        when(productService.getProductById(10L)).thenReturn(product);
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(invocation -> {
            OrderEntity entity = invocation.getArgument(0);
            entity.setId(5L);
            entity.getOrderItems().get(0).setId(1L);
            return entity;
        });

        OrderResponse created = orderService.createOrder(request);

        assertEquals(5L, created.id());
        assertEquals(1L, created.client().id());
        assertEquals("Maria", created.client().name());
        assertEquals("11999999999", created.client().phoneNumber());
        assertEquals(1, created.orderItems().size());
        assertEquals(1L, created.orderItems().get(0).id());
        assertEquals(10L, created.orderItems().get(0).product().id());
        assertEquals("Baozi", created.orderItems().get(0).product().productName());
        assertEquals(2, created.orderItems().get(0).quantity());
        assertEquals(new BigDecimal("12.50"), created.orderItems().get(0).price());
        assertEquals(new BigDecimal("25.00"), created.totalPrice());
        verify(orderRepository).save(any(OrderEntity.class));
    }

    @Test
    void getOrderById_shouldReturnOrderResponse() {
        when(orderRepository.findById(5L)).thenReturn(Optional.of(order()));

        OrderResponse result = orderService.getOrderById(5L);

        assertEquals(5L, result.id());
        assertEquals("Maria", result.client().name());
        assertEquals("Baozi", result.orderItems().get(0).product().productName());
        assertEquals(new BigDecimal("25.00"), result.totalPrice());
    }

    @Test
    void getOrderById_shouldThrowWhenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.getOrderById(99L)
        );

        assertEquals("Não foi possível encontrar o pedido: 99", exception.getMessage());
    }

    @Test
    void getAllOrders_shouldReturnList() {
        when(orderRepository.findAll()).thenReturn(List.of(order()));

        List<OrderResponse> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).id());
        assertEquals("Maria", result.get(0).client().name());
        verify(orderRepository).findAll();
    }

    @Test
    void deleteOrder_shouldDeleteWhenExists() {
        OrderEntity order = order();
        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));

        orderService.deleteOrder(5L);

        verify(orderRepository).delete(order);
    }

    @Test
    void deleteOrder_shouldThrowWhenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.deleteOrder(99L)
        );

        assertEquals("Não foi possível encontrar o pedido: 99", exception.getMessage());
        verify(orderRepository, never()).delete(any());
    }

    private ClientEntity client() {
        return new ClientEntity()
                .setId(1L)
                .setName("Maria")
                .setPhoneNumber("11999999999");
    }

    private ProductEntity product() {
        return new ProductEntity()
                .setId(10L)
                .setProductName("Baozi")
                .setPrice(new BigDecimal("12.50"));
    }

    private OrderEntity order() {
        OrderItemEntity item = new OrderItemEntity()
                .setId(1L)
                .setProduct(product())
                .setQuantity(2)
                .setPrice(new BigDecimal("12.50"));

        return new OrderEntity()
                .setId(5L)
                .setClient(client())
                .setOrderItems(List.of(item))
                .setTotalPrice(new BigDecimal("25.00"));
    }
}
