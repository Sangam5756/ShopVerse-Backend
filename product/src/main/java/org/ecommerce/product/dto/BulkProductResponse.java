package org.ecommerce.product.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class BulkProductResponse {

    private int total;
    private int success;
    private int failed;

    private List<String> errors;
}
