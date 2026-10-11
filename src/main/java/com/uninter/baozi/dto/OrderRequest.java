package com.uninter.baozi.dto;

import java.util.List;

public record OrderRequest(Long clientId, List<OrdemItemRequest> orderItems) {
}
