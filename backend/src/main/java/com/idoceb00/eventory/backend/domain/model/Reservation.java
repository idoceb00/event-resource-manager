package com.idoceb00.eventory.backend.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "event_id", nullable = false)
  private Event event;

  @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ReservationLine> lines = new LinkedHashSet<>();

  public Reservation(Event event) {
    this.event = event;
  }

  public void addLine(ReservationLine line) {
    lines.add(line);
    line.setReservation(this);
  }

  public void removeLine(ReservationLine line) {
    lines.remove(line);
    line.setReservation(null);
  }
}
