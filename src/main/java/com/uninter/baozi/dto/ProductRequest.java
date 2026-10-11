package com.uninter.baozi.dto;

import java.math.BigDecimal;

public record ProductRequest(String name, BigDecimal price) {
}
