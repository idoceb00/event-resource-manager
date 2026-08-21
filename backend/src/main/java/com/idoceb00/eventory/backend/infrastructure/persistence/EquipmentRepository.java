package com.idoceb00.eventory.backend.infrastructure.persistence;

import com.idoceb00.eventory.backend.domain.model.Equipment;
import com.idoceb00.eventory.backend.domain.model.EquipmentStatus;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

  List<Equipment> findByStatus(EquipmentStatus status);

  /**
   * Pessimistic lock (PESSIMISTIC_WRITE) because the conflict is over shared stock of the same
   * equipment: concurrent reservations for the same item in overlapping dates compete for the same
   * row. Optimistic locking would not detect this since different reservation rows are inserted,
   * not the same equipment row updated. The pessimistic lock serializes access to the equipment's
   * stock during the read-check-insert cycle.
   */
  boolean existsByName(String name);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT e FROM Equipment e WHERE e.id = :id")
  Optional<Equipment> findByIdForUpdate(Long id);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE Equipment e SET e.stock = e.stock + :quantity WHERE e.id = :id")
  int addStock(@Param("id") Long id, @Param("quantity") int quantity);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE Equipment e SET e.stock = :stock WHERE e.id = :id")
  int updateStock(@Param("id") Long id, @Param("stock") int stock);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE Equipment e SET e.status = :status, e.stock = :stock WHERE e.id = :id")
  int updateStatusAndStock(
      @Param("id") Long id, @Param("status") EquipmentStatus status, @Param("stock") int stock);
}
