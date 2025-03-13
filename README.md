# Vavr + Spring boot 3 + Hibernate 6 demo

### 1) Add dependency to pom.xml
```xml
<dependency>
    <groupId>io.github.jleblanc64</groupId>
    <artifactId>vavr-hibernate6</artifactId>
    <version>1.0.1</version>
</dependency>
```

### 2) Add class ConfigInit
```java
import io.github.jleblanc64.hibernate6.hibernate.VavrHibernate6;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class ConfigInit implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        VavrHibernate6.override();
    }
}
```

and register the class in the file: `src/main/resources/META-INF/spring.factories` (replace `com.demo.spring` with your own package) :
```
org.springframework.context.ApplicationContextInitializer=com.demo.spring.ConfigInit
```

### 3) Add class WebMvcConfig
```java
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jleblanc64.hibernate6.jackson.deser.UpdateOM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    ObjectMapper om;

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        UpdateOM.update(om, converters);
    }
}
```

### 4) Write some Hibernate entities code using Vavr
```java
import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Option<String> name;

    private Option<Integer> number;

    private Option<String> city;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "membership_id")
    private Option<Membership> membership;
}
```

### 5) Run Spring boot tests showing that Hibernate 6 + Vavr works

https://github.com/jleblanc64/vavr-hibernate6-demo/blob/main/src/test/java/com/demo/ApplicationTests.java
