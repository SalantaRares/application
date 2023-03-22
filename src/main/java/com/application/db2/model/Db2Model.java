package com.application.db2.model;

import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Db2Model {
    BigDecimal id;
    String bar;
}
