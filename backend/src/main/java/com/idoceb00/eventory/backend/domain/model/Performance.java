package com.idoceb00.eventory.backend.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "performances")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Performance {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  private LocalTime startTime;

  private long duration;

  private LocalTime rehearsalTime;

  @ManyToOne
  @JoinColumn(name = "event_id", nullable = false)
  private Event event;

  public Performance(String name, LocalTime startTime, long duration, LocalTime rehearsalTime) {
    this.name = name;
    this.startTime = startTime;
    this.duration = duration;
    this.rehearsalTime = rehearsalTime;
  }

  public void setEvent(Event event) {
    this.event = event;
  }
}
