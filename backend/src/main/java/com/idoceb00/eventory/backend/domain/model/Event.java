package com.idoceb00.eventory.backend.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private LocalDateTime startDate;

  @Column(nullable = false)
  private LocalDateTime endDate;

  private String address;

  private boolean transport;

  private String extraInfo;

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Performance> performances = new ArrayList<>();

  public Event(String name, LocalDateTime startDate, LocalDateTime endDate) {
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public Event(
      String name,
      LocalDateTime startDate,
      LocalDateTime endDate,
      String address,
      boolean transport,
      String extraInfo) {
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
    this.address = address;
    this.transport = transport;
    this.extraInfo = extraInfo;
  }

  public void addPerformance(Performance performance) {
    performances.add(performance);
    performance.setEvent(this);
  }

  public void removePerformance(Performance performance) {
    performances.remove(performance);
    performance.setEvent(null);
  }

  public boolean canAddPerformance(Performance candidate) {
    for (Performance p : performances) {
      if (p.getName().equals(candidate.getName())
          && p.getStartTime().equals(candidate.getStartTime())
          && p.getDuration() == candidate.getDuration()
          && p.getRehearsalTime().equals(candidate.getRehearsalTime())) {
        return false;
      }
    }
    return true;
  }
}
