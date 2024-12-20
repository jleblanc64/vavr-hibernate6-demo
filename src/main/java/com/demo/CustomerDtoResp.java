package com.demo;

import io.github.jleblanc64.libcustom.functional.OptionF;
import io.vavr.collection.List;
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
    private OptionF<Integer> numberOpt;
    private Integer i;
    private List<OrderDto> orders;

    public CustomerDtoResp(Customer c) {
        id = c.getId();
        name = c.getName().orElse("default");
        number = c.getNumber().orElse(-10);
        numberOpt = c.getNumber();
        i = c.getI();
        orders = c.getOrders().map(OrderDto::new);
    }
}
