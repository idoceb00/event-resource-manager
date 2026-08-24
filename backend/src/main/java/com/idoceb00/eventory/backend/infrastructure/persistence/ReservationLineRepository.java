package com.idoceb00.eventory.backend.infrastructure.persistence;

import com.idoceb00.eventory.backend.domain.model.ReservationLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationLineRepository extends JpaRepository<ReservationLine, Long> {}
