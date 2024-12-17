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
    private int number;
    private Integer i;
    private List<OrderDto> orders;

    public CustomerDtoResp(Customer c) {
        id = c.getId();
        name = c.getName().orElse("default");
        number = c.getNumber().orElse(-10);
        i = c.getI();
        orders = c.getOrders().map(OrderDto::new);
    }
}
