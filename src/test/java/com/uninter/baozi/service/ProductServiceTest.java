package com.uninter.baozi.service;

import com.uninter.baozi.dto.ProductRequest;
import com.uninter.baozi.model.ProductEntity;
import com.uninter.baozi.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_shouldSaveNormalizedPriceAndReturnId() {
        ProductRequest request = new ProductRequest("Baozi de porco", new BigDecimal("12.5"));
        when(productRepository.save(any(ProductEntity.class))).thenAnswer(invocation -> {
            ProductEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        Long id = productService.createProduct(request);

        assertEquals(1L, id);

        ArgumentCaptor<ProductEntity> captor = ArgumentCaptor.forClass(ProductEntity.class);
        verify(productRepository).save(captor.capture());
        ProductEntity saved = captor.getValue();
        assertEquals("Baozi de porco", saved.getProductName());
        assertEquals(new BigDecimal("12.50"), saved.getPrice());
        assertTrue(saved.isHasStock());
    }

    @Test
    void createProduct_shouldNormalizeNegativePriceToZero() {
        ProductRequest request = new ProductRequest("Baozi", new BigDecimal("-10"));
        when(productRepository.save(any(ProductEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        productService.createProduct(request);

        ArgumentCaptor<ProductEntity> captor = ArgumentCaptor.forClass(ProductEntity.class);
        verify(productRepository).save(captor.capture());
        assertEquals(new BigDecimal("0.00"), captor.getValue().getPrice());
    }

    @Test
    void createProduct_shouldThrowWhenNameIsNull() {
        ProductRequest request = new ProductRequest(null, new BigDecimal("10.00"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct(request)
        );

        assertEquals("É necessário informar o nome e o preço do produto.", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void createProduct_shouldThrowWhenPriceIsNull() {
        ProductRequest request = new ProductRequest("Baozi", null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct(request)
        );

        assertEquals("É necessário informar o nome e o preço do produto.", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void getProductById_shouldReturnProduct() {
        ProductEntity product = new ProductEntity().setId(1L).setProductName("Baozi");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductEntity result = productService.getProductById(1L);

        assertEquals(product, result);
    }

    @Test
    void getProductById_shouldThrowWhenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.getProductById(99L)
        );

        assertEquals("Produto não encontrado.", exception.getMessage());
    }

    @Test
    void getAllProducts_shouldReturnList() {
        List<ProductEntity> products = List.of(new ProductEntity().setId(1L).setProductName("Baozi"));
        when(productRepository.findAll()).thenReturn(products);

        List<ProductEntity> result = productService.getAllProducts();

        assertEquals(products, result);
        verify(productRepository).findAll();
    }

    @Test
    void deleteProductById_shouldDeleteWhenExists() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProductById(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProductById_shouldThrowWhenNotFound() {
        when(productRepository.existsById(99L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.deleteProductById(99L)
        );

        assertEquals("Produto não encontrado.", exception.getMessage());
        verify(productRepository, never()).deleteById(any());
    }
}
