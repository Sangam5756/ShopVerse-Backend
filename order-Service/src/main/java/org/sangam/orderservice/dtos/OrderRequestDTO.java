package org.sangam.orderservice.dtos;


import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
        private  Long customerId;
        private List<OrderItemRequestDTO> items;
}
