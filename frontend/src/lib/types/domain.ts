/**
 * Domain models and status unions.
 *
 * These types are the single contract shared by components, services and mock
 * data. Components must depend on these types and never on the concrete shape
 * of an HTTP response.
 */

export type EquipmentCategory =
  "SOUND" | "LIGHTING" | "MOTORS_AND_STRUCTURES" | "VIDEO";

export type EquipmentStatus = "CATALOGUED" | "DECATALOGUED";

export interface Performance {
  id: string;
  name: string;
  startTime: string;
  duration: number;
  rehearsalTime: string;
}

export interface Event {
  id: string;
  name: string;
  startDate: string;
  endDate: string;
  address: string;
  transport: boolean;
  extraInfo?: string;
  performances: Performance[];
}

export interface Equipment {
  id: string;
  name: string;
  category: EquipmentCategory;
  status: EquipmentStatus;
  stock: number;
}

export interface ReservationLine {
  id: string;
  equipment: Equipment;
  author: User;
  quantity: number;
}

export interface Reservation {
  id: string;
  event: Event;
  lines: ReservationLine[];
}

export type UserRole = "admin" | "employee";

export interface User {
  id: string;
  username: string;
  name: string;
  role: UserRole;
  active: boolean;
}
