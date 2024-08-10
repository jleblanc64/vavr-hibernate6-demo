package com.demo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDtoReq {
    private Optional<String> name;
    private List<OrderDto> orders;

    public Customer toCustomer() {
        var c = new Customer();
        c.setName(name);

        if (orders != null)
            c.setOrders(orders.stream().map(OrderDto::toOrder).collect(Collectors.toList()));
        return c;
    }
}
