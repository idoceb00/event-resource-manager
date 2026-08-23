import type {
  Employee,
  Equipment,
  EquipmentCategory,
  Event,
  Performance,
  Reservation,
  ReservationWithNames,
  User,
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
  createEvent(data: {
    name: string;
    startDate: string;
    endDate: string;
    address?: string;
    transport: boolean;
    extraInfo?: string;
  }): Promise<Event>;
  updateEvent(
    id: string,
    data: {
      name?: string;
      startDate?: string;
      endDate?: string;
      address?: string;
      transport?: boolean;
      extraInfo?: string;
    },
  ): Promise<Event>;
  createPerformance(
    eventId: string,
    data: {
      name: string;
      startTime: string;
      duration: number;
      rehearsalTime: string;
    },
  ): Promise<Performance>;
  deletePerformance(eventId: string, performanceId: string): Promise<void>;
  deleteEvent(id: string): Promise<void>;
}

export interface EquipmentService {
  getEquipment(): Promise<Equipment[]>;
  getEquipmentById(id: string): Promise<Equipment>;
  getCatalogue(): Promise<Equipment[]>;
  createEquipment(data: {
    name: string;
    category: EquipmentCategory;
    stock: number;
  }): Promise<Equipment>;
  adjustStock(id: string, delta: number): Promise<Equipment>;
  decatalogue(id: string): Promise<Equipment>;
  recatalogue(id: string): Promise<Equipment>;
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

export interface AuthService {
  login(username: string, password: string): Promise<User>;
  logout(): Promise<void>;
  me(): Promise<User | null>;
}
