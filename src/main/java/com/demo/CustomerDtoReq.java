package com.demo;

import com.demo.functional.ListF;
import com.demo.functional.OptionF;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDtoReq {
    private OptionF<String> name;
    private ListF<OrderDto> orders;

    public Customer toCustomer() {
        var c = new Customer();
        c.setName(name);
        c.setOrders(orders.map(x -> x.toOrder(c)));
        return c;
    }
}
