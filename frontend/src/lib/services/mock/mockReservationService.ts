import { employees, events, products, reservations } from "$lib/data/mock-data";
import type { Reservation, ReservationWithNames } from "$lib/types/domain";
import type { ReservationService } from "../types";

function getEventName(eventId: string): string {
  return events.find((event) => event.id === eventId)?.name ?? "";
}

function getProductName(productId: string | undefined): string {
  if (!productId) return "—";
  return products.find((product) => product.id === productId)?.name ?? "";
}

function getEmployeeName(employeeId: string | undefined): string {
  if (!employeeId) return "—";
  return employees.find((employee) => employee.id === employeeId)?.name ?? "";
}

function withNames(reservation: Reservation): ReservationWithNames {
  return {
    ...reservation,
    eventName: getEventName(reservation.eventId),
    productName: getProductName(reservation.productId),
    employeeName: getEmployeeName(reservation.employeeId),
  };
}

export const mockReservationService: ReservationService = {
  async getReservations(): Promise<ReservationWithNames[]> {
    return reservations.map(withNames);
  },

  async getReservationsForEvent(
    eventId: string,
  ): Promise<ReservationWithNames[]> {
    return reservations
      .filter((reservation) => reservation.eventId === eventId)
      .map(withNames);
  },

  async hasConflict(reservationId: string): Promise<boolean> {
    return (
      reservations.find((reservation) => reservation.id === reservationId)
        ?.hasConflict ?? false
    );
  },

  async getRawReservations(): Promise<Reservation[]> {
    return reservations;
  },
};
