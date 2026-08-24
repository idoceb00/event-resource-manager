import { events, products } from "$lib/data/mock-data";
import type {
  Reservation,
  ReservationLine,
  User,
} from "$lib/types/domain";
import type { ReservationService } from "../types";

const mockUser: User = {
  id: "user-001",
  username: "admin",
  name: "Admin",
  role: "admin",
  active: true,
};

function makeLine(
  id: string,
  equipmentId: string,
  quantity: number,
): ReservationLine {
  return {
    id,
    equipment: products.find((p) => p.id === equipmentId)!,
    author: mockUser,
    quantity,
  };
}

const mockReservations: Reservation[] = [
  {
    id: "res-001",
    event: events[0],
    lines: [
      makeLine("line-001", "prod-001", 3),
      makeLine("line-002", "prod-003", 4),
    ],
  },
  {
    id: "res-002",
    event: events[1],
    lines: [makeLine("line-003", "prod-006", 2)],
  },
];

export const mockReservationService: ReservationService = {
  async getReservations(): Promise<Reservation[]> {
    return mockReservations;
  },

  async getReservationsForEvent(eventId: string): Promise<Reservation[]> {
    return mockReservations.filter((r) => r.event.id === eventId);
  },

  async createReservation(
    eventId: string,
    lines: Array<{
      equipmentId: number;
      quantity: number;
    }>,
  ) {
    return { succeeded: lines.length, failed: 0, errors: [] };
  },

  async deleteReservationLine(): Promise<void> {},
};
