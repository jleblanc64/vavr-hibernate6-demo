package com.demo;

import com.demo.functional.IListF;
import com.demo.functional.IOptionF;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.demo.functional.ListF.f;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDtoReq {
    private IOptionF<String> name;
    private IListF<OrderDto> orders;

    public Customer toCustomer() {
        var c = new Customer();
        c.setName(name);

        if (orders != null)
            c.setOrders(f(orders).map(x -> x.toOrder(c)));
        return c;
    }
}
