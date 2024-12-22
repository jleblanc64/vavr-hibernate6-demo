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
    private String city;
    private List<OrderDto> orders;
    private Option<MembershipDto> membership;

    public CustomerDtoResp(Customer c) {
        id = c.getId();
        name = c.getName().getOrElse("default");
        number = c.getNumber().getOrElse(-10);
        numberOpt = c.getNumber();
        i = c.getI();
        city = c.getCity();
        orders = c.getOrders().map(OrderDto::new);
        membership = Option.of(c.getMembership()).map(MembershipDto::new);
    }
}
