package org.sangam.orderservice.dtos;


import lombok.Data;

@Data
public class ProductResponseDTO {

    private Long id;
    private String name;
    private Double price;
    private Integer stockQuantity;

}
