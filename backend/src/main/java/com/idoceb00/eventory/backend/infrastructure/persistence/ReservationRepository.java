package com.idoceb00.eventory.backend.infrastructure.persistence;

import com.idoceb00.eventory.backend.domain.model.Event;
import com.idoceb00.eventory.backend.domain.model.Reservation;
import com.idoceb00.eventory.backend.domain.model.ReservationLine;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  @Query(
      "SELECT COALESCE(SUM(rl.quantity), 0) FROM ReservationLine rl "
          + "JOIN rl.reservation r "
          + "JOIN r.event e "
          + "WHERE rl.equipment.id = :equipmentId "
          + "AND e.startDate < :endDate AND e.endDate > :startDate")
  int sumReservedQuantityByEquipmentAndOverlappingDates(
      @Param("equipmentId") Long equipmentId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query(
      "SELECT rl FROM ReservationLine rl "
          + "JOIN FETCH rl.reservation r "
          + "JOIN FETCH r.event e "
          + "WHERE rl.equipment.id = :equipmentId "
          + "AND e.endDate > :now")
  List<ReservationLine> findAffectedLinesByEquipment(
      @Param("equipmentId") Long equipmentId, @Param("now") LocalDateTime now);

  Optional<Reservation> findByEvent(Event event);

  @EntityGraph(
      attributePaths = {"event", "event.performances", "lines", "lines.equipment", "lines.user"})
  List<Reservation> findByEventId(@Param("eventId") Long eventId);

  @Query(
      "SELECT DISTINCT rl.equipment.id FROM ReservationLine rl "
          + "JOIN rl.reservation r WHERE r.event.id = :eventId")
  List<Long> findDistinctEquipmentIdsByEventId(@Param("eventId") Long eventId);

  void deleteByEventId(@Param("eventId") Long eventId);

  @Query(
      "SELECT CASE WHEN COUNT(rl) > 0 THEN true ELSE false END FROM ReservationLine rl "
          + "WHERE rl.user.id = :userId "
          + "AND rl.equipment.id = :equipmentId "
          + "AND rl.reservation.id = :reservationId")
  boolean existsLineByUserAndEquipmentAndReservation(
      @Param("userId") Long userId,
      @Param("equipmentId") Long equipmentId,
      @Param("reservationId") Long reservationId);

  @Query(
      "SELECT CASE WHEN COUNT(rl) > 0 THEN true ELSE false END "
          + "FROM ReservationLine rl WHERE rl.user.id = :userId")
  boolean existsLineByUserId(@Param("userId") Long userId);

  @Query("SELECT r FROM Reservation r")
  @EntityGraph(
      attributePaths = {"event", "event.performances", "lines", "lines.equipment", "lines.user"})
  List<Reservation> findAllWithLines();
}
