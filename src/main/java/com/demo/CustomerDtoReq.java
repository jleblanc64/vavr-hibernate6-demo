package com.demo;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class CustomerDtoReq {
    private Option<String> name;
    private Option<Integer> number;
    private Integer i;
    private List<OrderDto> orders;

    public Customer toEntity() {
        var c = new Customer();
        c.setName(name);
        c.setNumber(number);
        c.setI(i);
        c.setOrders(orders.map(x -> x.toOrder(c)));
        return c;
    }
}
