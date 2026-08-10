package com.idoceb00.eventory.backend.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DateOverlapUtilsTest {

  @Test
  void totalOverlap() {
    LocalDateTime aStart = LocalDateTime.of(2026, 6, 1, 8, 0);
    LocalDateTime aEnd = LocalDateTime.of(2026, 6, 10, 18, 0);
    LocalDateTime bStart = LocalDateTime.of(2026, 6, 3, 10, 0);
    LocalDateTime bEnd = LocalDateTime.of(2026, 6, 7, 16, 0);

    assertThat(DateOverlapUtils.overlaps(aStart, aEnd, bStart, bEnd)).isTrue();
    assertThat(DateOverlapUtils.overlaps(bStart, bEnd, aStart, aEnd)).isTrue();
  }

  @Test
  void partialOverlap() {
    LocalDateTime aStart = LocalDateTime.of(2026, 6, 1, 8, 0);
    LocalDateTime aEnd = LocalDateTime.of(2026, 6, 5, 18, 0);
    LocalDateTime bStart = LocalDateTime.of(2026, 6, 4, 10, 0);
    LocalDateTime bEnd = LocalDateTime.of(2026, 6, 8, 16, 0);

    assertThat(DateOverlapUtils.overlaps(aStart, aEnd, bStart, bEnd)).isTrue();
    assertThat(DateOverlapUtils.overlaps(bStart, bEnd, aStart, aEnd)).isTrue();
  }

  @Test
  void noOverlapAendsBeforeBstarts() {
    LocalDateTime aStart = LocalDateTime.of(2026, 6, 1, 8, 0);
    LocalDateTime aEnd = LocalDateTime.of(2026, 6, 3, 18, 0);
    LocalDateTime bStart = LocalDateTime.of(2026, 6, 5, 10, 0);
    LocalDateTime bEnd = LocalDateTime.of(2026, 6, 8, 16, 0);

    assertThat(DateOverlapUtils.overlaps(aStart, aEnd, bStart, bEnd)).isFalse();
  }

  @Test
  void noOverlapBendsBeforeAstarts() {
    LocalDateTime aStart = LocalDateTime.of(2026, 6, 10, 8, 0);
    LocalDateTime aEnd = LocalDateTime.of(2026, 6, 15, 18, 0);
    LocalDateTime bStart = LocalDateTime.of(2026, 6, 1, 10, 0);
    LocalDateTime bEnd = LocalDateTime.of(2026, 6, 5, 16, 0);

    assertThat(DateOverlapUtils.overlaps(aStart, aEnd, bStart, bEnd)).isFalse();
  }

  @Test
  void touchingBoundariesDoNotOverlap() {
    LocalDateTime aStart = LocalDateTime.of(2026, 6, 1, 8, 0);
    LocalDateTime aEnd = LocalDateTime.of(2026, 6, 5, 18, 0);
    LocalDateTime bStart = LocalDateTime.of(2026, 6, 5, 18, 0);
    LocalDateTime bEnd = LocalDateTime.of(2026, 6, 10, 16, 0);

    assertThat(DateOverlapUtils.overlaps(aStart, aEnd, bStart, bEnd)).isFalse();
    assertThat(DateOverlapUtils.overlaps(bStart, bEnd, aStart, aEnd)).isFalse();
  }

  @Test
  void rangeInsideAnother() {
    LocalDateTime aStart = LocalDateTime.of(2026, 6, 1, 8, 0);
    LocalDateTime aEnd = LocalDateTime.of(2026, 6, 20, 18, 0);
    LocalDateTime bStart = LocalDateTime.of(2026, 6, 5, 10, 0);
    LocalDateTime bEnd = LocalDateTime.of(2026, 6, 10, 16, 0);

    assertThat(DateOverlapUtils.overlaps(aStart, aEnd, bStart, bEnd)).isTrue();
    assertThat(DateOverlapUtils.overlaps(bStart, bEnd, aStart, aEnd)).isTrue();
  }

  @Test
  void identicalRangesOverlap() {
    LocalDateTime start = LocalDateTime.of(2026, 6, 1, 8, 0);
    LocalDateTime end = LocalDateTime.of(2026, 6, 10, 18, 0);

    assertThat(DateOverlapUtils.overlaps(start, end, start, end)).isTrue();
  }
}
