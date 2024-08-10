package com.demo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

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
        orders = c.getOrders().stream().map(OrderDto::new).collect(Collectors.toList());
    }
}
