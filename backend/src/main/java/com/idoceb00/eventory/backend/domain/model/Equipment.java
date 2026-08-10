package com.idoceb00.eventory.backend.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "equipment")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EquipmentCategory category;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EquipmentStatus status;

  private int stock;

  public Equipment(String name, EquipmentCategory category, EquipmentStatus status, int stock) {
    this.name = name;
    this.category = category;
    this.status = status;
    this.stock = stock;
  }
}
