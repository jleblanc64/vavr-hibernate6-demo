package com.demo;

import io.github.jleblanc64.libcustom.functional.OptionF;
import io.vavr.collection.List;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private OptionF<String> name;

    private OptionF<Integer> number;

    private Integer i;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders;
}
