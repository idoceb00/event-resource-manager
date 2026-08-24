import { apiClient, ApiError } from "$lib/api/client";
import type {
  Equipment,
  Event,
  Reservation,
  User,
} from "$lib/types/domain";
import type { CreateReservationResult, ReservationService } from "../types";

interface BackendEquipmentResponse {
  id: number;
  name: string;
  category: string;
  status: string;
  stock: number;
}

interface BackendUserResponse {
  id: number;
  username: string;
  name: string;
  role: string;
  active: boolean;
}

interface BackendPerformanceResponse {
  id: number;
  name: string;
  startTime: string;
  duration: number;
  rehearsalTime: string;
}

interface BackendEventResponse {
  id: number;
  name: string;
  startDate: string;
  endDate: string;
  address: string;
  transport: boolean;
  extraInfo: string;
  performances: BackendPerformanceResponse[];
}

interface BackendReservationLineResponse {
  id: number;
  equipment: BackendEquipmentResponse;
  author: BackendUserResponse;
  quantity: number;
}

interface BackendReservationResponse {
  id: number;
  event: BackendEventResponse;
  lines: BackendReservationLineResponse[];
}

interface CreateReservationLineRequest {
  equipmentId: number;
  quantity: number;
}

function mapEquipment(r: BackendEquipmentResponse): Equipment {
  return {
    id: String(r.id),
    name: r.name,
    category: r.category as Equipment["category"],
    status: r.status as Equipment["status"],
    stock: r.stock,
  };
}

function mapUser(r: BackendUserResponse): User {
  return {
    id: String(r.id),
    username: r.username,
    name: r.name ?? r.username,
    role: r.role === "ADMINISTRATOR" ? "admin" : "employee",
    active: r.active,
  };
}

function mapEvent(r: BackendEventResponse): Event {
  return {
    id: String(r.id),
    name: r.name,
    startDate: r.startDate,
    endDate: r.endDate,
    address: r.address ?? "",
    transport: r.transport,
    extraInfo: r.extraInfo ?? undefined,
    performances: (r.performances ?? []).map((p) => ({
      id: String(p.id),
      name: p.name,
      startTime: p.startTime,
      duration: p.duration,
      rehearsalTime: p.rehearsalTime,
    })),
  };
}

function mapReservation(r: BackendReservationResponse): Reservation {
  return {
    id: String(r.id),
    event: mapEvent(r.event),
    lines: (r.lines ?? []).map((l) => ({
      id: String(l.id),
      equipment: mapEquipment(l.equipment),
      author: mapUser(l.author),
      quantity: l.quantity,
    })),
  };
}

async function postLine(
  eventId: string,
  line: CreateReservationLineRequest,
): Promise<Reservation> {
  const response = await apiClient.post<BackendReservationResponse>(
    "/api/reservations",
    { eventId: Number(eventId), ...line },
  );
  return mapReservation(response);
}

export const apiReservationService: ReservationService = {
  async getReservations(): Promise<Reservation[]> {
    const response =
      await apiClient.get<BackendReservationResponse[]>("/api/reservations");
    return response.map(mapReservation);
  },

  async getReservationsForEvent(eventId: string): Promise<Reservation[]> {
    const response = await apiClient.get<BackendReservationResponse[]>(
      `/api/events/${eventId}/reservations`,
    );
    return response.map(mapReservation);
  },

  async createReservation(
    eventId: string,
    lines: Array<{
      equipmentId: number;
      quantity: number;
    }>,
  ): Promise<CreateReservationResult> {
    let succeeded = 0;
    let failed = 0;
    const errors: string[] = [];

    for (const line of lines) {
      try {
        await postLine(eventId, line);
        succeeded++;
      } catch (e) {
        failed++;
        if (e instanceof ApiError) {
          errors.push(e.message);
        } else {
          errors.push("Unknown error");
        }
      }
    }

    return { succeeded, failed, errors };
  },

  async deleteReservationLine(lineId: string): Promise<void> {
    await apiClient.delete(`/api/reservations/lines/${lineId}`);
  },
};
