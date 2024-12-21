package com.demo;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDtoResp {
    private long id;
    private String name;
    private int number;
    private Option<Integer> numberOpt;
    private Integer i;
    private List<OrderDto> orders;

    public CustomerDtoResp(Customer c) {
        id = c.getId();
        name = c.getName().getOrElse("default");
        number = c.getNumber().getOrElse(-10);
        numberOpt = c.getNumber();
        i = c.getI();
        orders = c.getOrders().map(OrderDto::new);
    }
}
