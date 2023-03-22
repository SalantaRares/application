package com.application.db1.model;

import lombok.*;

import java.math.BigDecimal;

@Data
@Setter
@Getter
@ToString
@NoArgsConstructor
public class Db1Model {
    BigDecimal id;
    String name;
    BigDecimal someNumber;
}
