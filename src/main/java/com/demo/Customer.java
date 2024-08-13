package com.demo;

import io.github.jleblanc64.libcustom.functional.ListF;
import io.github.jleblanc64.libcustom.functional.OptionF;
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

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private ListF<Order> orders;
}
