import type {
  Equipment,
  EquipmentCategory,
  Event,
  Performance,
  Reservation,
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

export interface UserService {
  getUsers(): Promise<User[]>;
  getUserById(id: string): Promise<User>;
  createUser(data: {
    username: string;
    name: string;
    password: string;
    role: "admin" | "employee";
    active: boolean;
  }): Promise<User>;
  updateUser(
    id: string,
    data: { name?: string; role?: "admin" | "employee" },
  ): Promise<User>;
  changePassword(id: string, newPassword: string): Promise<void>;
  setActivation(
    id: string,
    active: boolean,
    confirmPassword?: string,
  ): Promise<void>;
}

export interface CreateReservationResult {
  succeeded: number;
  failed: number;
  errors: string[];
}

export interface ReservationService {
  getReservations(): Promise<Reservation[]>;
  getReservationsForEvent(eventId: string): Promise<Reservation[]>;
  createReservation(
    eventId: string,
    lines: Array<{
      equipmentId: number;
      quantity: number;
    }>,
  ): Promise<CreateReservationResult>;
  deleteReservationLine(lineId: string): Promise<void>;
}

export interface AuthService {
  login(username: string, password: string): Promise<User>;
  logout(): Promise<void>;
  me(): Promise<User | null>;
}
