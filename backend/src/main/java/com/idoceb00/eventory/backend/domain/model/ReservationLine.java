package com.idoceb00.eventory.backend.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "reservation_lines",
    uniqueConstraints =
        @UniqueConstraint(columnNames = {"user_id", "equipment_id", "reservation_id"}))
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationLine {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "equipment_id", nullable = false)
  private Equipment equipment;

  @ManyToOne
  @JoinColumn(name = "reservation_id", nullable = false)
  private Reservation reservation;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private int quantity;

  public ReservationLine(Equipment equipment, User user, int quantity) {
    this.equipment = equipment;
    this.user = user;
    this.quantity = quantity;
  }

  public void setReservation(Reservation reservation) {
    this.reservation = reservation;
  }
}
