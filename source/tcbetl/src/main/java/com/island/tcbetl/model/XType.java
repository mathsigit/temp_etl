package com.island.tcbetl.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

@Data
@Entity
public class XType implements Serializable {

    @Id
    @Column(name = "id")
    UUID newid;

    @Column(name = "literal")
    String literal;

    @Column(name = "hex4")
    String hex4;

    @Column(name = "hex2")
    String hex2;
}
