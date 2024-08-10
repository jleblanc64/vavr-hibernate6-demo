package com.demo;

import com.demo.functional.IListF;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

import static com.demo.functional.ListF.empty;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Optional<String> name;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private IListF<Order> orders = empty();
}
