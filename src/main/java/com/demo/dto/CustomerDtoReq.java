package com.demo.dto;

import com.demo.model.Customer;
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
    private String city;
    private List<OrderDto> orders;
    private Option<MembershipDto> membership;

    public Customer toEntity() {
        var c = new Customer();
        c.setName(name);
        c.setNumber(number);
        c.setI(i);
        c.setCity(city);
        c.setOrders(orders.map(x -> x.toEntity(c)));
        c.setMembership(membership.map(MembershipDto::toEntity));

        return c;
    }
}
