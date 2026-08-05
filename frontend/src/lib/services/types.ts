import type {
  Employee,
  Equipment,
  Event,
  Reservation,
  ReservationWithNames,
} from "$lib/types/domain";

/**
 * Service contracts for the data the UI needs.
 *
 * Components depend on these interfaces (via `$lib/services`), never on the
 * concrete implementation or the shape of an HTTP response. Swapping the mock
 * for a real API later only changes the bindings in `$lib/services/index.ts`.
 */

export interface EventService {
  getEvents(): Promise<Event[]>;
  getEvent(id: string): Promise<Event | undefined>;
}

export interface EquipmentService {
  getEquipment(): Promise<Equipment[]>;
}

export interface StaffService {
  getStaff(): Promise<Employee[]>;
}

export interface ReservationService {
  getReservations(): Promise<ReservationWithNames[]>;
  getReservationsForEvent(eventId: string): Promise<ReservationWithNames[]>;
  hasConflict(reservationId: string): Promise<boolean>;
  /** Avoid client-only consumers importing raw reservations directly. */
  getRawReservations(): Promise<Reservation[]>;
}
