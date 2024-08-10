package com.demo;

import com.demo.functional.IListF;
import com.demo.functional.IOptionF;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static com.demo.functional.ListF.empty;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private IOptionF<String> name;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private IListF<Order> orders = empty();
}
