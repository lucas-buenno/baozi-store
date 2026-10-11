package com.uninter.baozi.service;

import com.uninter.baozi.dto.ProductRequest;
import com.uninter.baozi.model.ProductEntity;
import com.uninter.baozi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Long createProduct(ProductRequest request) {

        if (request.name() == null || request.price() == null) {
            throw new IllegalArgumentException("É necessário informar o nome e o preço do produto.");
        }

        ProductEntity productEntity = new ProductEntity()
                .setProductName(request.name())
                .setPrice(ProductEntity.normalizePrice(request.price()))
                .setHasStock(true);

        return productRepository.save(productEntity).getId();
    }

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    public ProductEntity getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
    }

    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Produto não encontrado.");
        }
        productRepository.deleteById(id);
    }
}
