/**
 * Domain models and status unions.
 *
 * These types are the single contract shared by components, services and mock
 * data. Components must depend on these types and never on the concrete shape
 * of an HTTP response.
 */

export type EquipmentCategory = "SOUND" | "LIGHTING" | "MOTORS_AND_STRUCTURES" | "VIDEO";

export type EquipmentStatus = "CATALOGUED" | "DECATALOGUED";

export type EmployeeStatus = "active" | "inactive";

export interface Performance {
  id: string;
  eventId: string;
  name: string;
  date: string;
  time: string;
}

export interface Event {
  id: string;
  name: string;
  startDate: string;
  endDate: string;
  location: string;
  transportInfo?: string;
  performances: Performance[];
}

export interface Equipment {
  id: string;
  name: string;
  category: EquipmentCategory;
  status: EquipmentStatus;
  stock: number;
}

export interface Employee {
  id: string;
  name: string;
  role: string;
  status: EmployeeStatus;
  email: string;
  phone: string;
}

export interface Reservation {
  id: string;
  eventId: string;
  productId?: string;
  employeeId?: string;
  startDate: string;
  endDate: string;
  hasConflict?: boolean;
}

export interface ReservationWithNames extends Reservation {
  eventName: string;
  productName: string;
  employeeName: string;
}

export type UserRole = "admin" | "employee";

export interface User {
  id: string;
  name: string;
  role: UserRole;
}
