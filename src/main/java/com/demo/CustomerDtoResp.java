package com.demo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDtoResp {
    private long id;
    private String name;
    private List<OrderDto> orders;

    public CustomerDtoResp(Customer c) {
        id = c.getId();
        name = c.getName().orElse("default");
        orders = c.getOrders().map(OrderDto::new);
    }
}
