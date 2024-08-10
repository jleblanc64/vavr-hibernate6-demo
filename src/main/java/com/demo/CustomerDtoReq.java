package com.demo;

import com.demo.functional.IListF;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

import static com.demo.functional.ListF.f;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDtoReq {
    private Optional<String> name;
    private IListF<OrderDto> orders;

    public Customer toCustomer() {
        var c = new Customer();
        c.setName(name);

        if (orders != null)
            c.setOrders(f(orders).map(x -> x.toOrder(c)));
        return c;
    }
}
