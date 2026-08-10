package com.idoceb00.eventory.backend.domain.service;

import java.time.LocalDateTime;

public final class DateOverlapUtils {

  private DateOverlapUtils() {}

  /**
   * Determines whether two date ranges overlap using open boundaries. If range A ends exactly when
   * range B starts (or vice versa), they do NOT overlap.
   *
   * @param startA start of range A
   * @param endA end of range A
   * @param startB start of range B
   * @param endB end of range B
   * @return true if the ranges overlap, false otherwise
   */
  // FUTURE: logistical feasibility check (teardown + travel time + setup)
  // is a separate future rule, not covered here. Only temporal overlap for now.
  public static boolean overlaps(
      LocalDateTime startA, LocalDateTime endA, LocalDateTime startB, LocalDateTime endB) {
    return startA.isBefore(endB) && startB.isBefore(endA);
  }
}
