package com.demo;

import io.github.jleblanc64.libcustom.functional.ListF;
import io.github.jleblanc64.libcustom.functional.OptionF;
import io.vavr.collection.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class CustomerDtoReq {
    private OptionF<String> name;
    private OptionF<Integer> number;
    private Integer i;
    private List<OrderDto> orders;

    public Customer toCustomer() {
        var c = new Customer();
        c.setName(name);
        c.setNumber(number);
        c.setI(i);
        c.setOrders(orders.map(x -> x.toOrder(c)));
        return c;
    }
}
