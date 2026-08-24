import { apiClient } from "$lib/api/client";
import type { Event, Performance } from "$lib/types/domain";
import type { EventService } from "../types";

interface EventResponse {
  id: number;
  name: string;
  startDate: string;
  endDate: string;
  address: string;
  transport: boolean;
  extraInfo: string | null;
  performances: PerformanceResponse[];
}

interface PerformanceResponse {
  id: number;
  name: string;
  startTime: string;
  duration: number;
  rehearsalTime: string;
}

function mapEvent(res: EventResponse): Event {
  return {
    id: String(res.id),
    name: res.name,
    startDate: res.startDate,
    endDate: res.endDate,
    address: res.address ?? "",
    transport: res.transport,
    extraInfo: res.extraInfo ?? undefined,
    performances: res.performances.map(mapPerformance),
  };
}

function mapPerformance(res: PerformanceResponse): Performance {
  return {
    id: String(res.id),
    name: res.name,
    startTime: res.startTime,
    duration: res.duration,
    rehearsalTime: res.rehearsalTime,
  };
}

export const apiEventService: EventService = {
  async getEvents(): Promise<Event[]> {
    const res = await apiClient.get<EventResponse[]>("/api/events");
    return res.map(mapEvent);
  },

  async getEvent(id: string): Promise<Event | undefined> {
    try {
      const res = await apiClient.get<EventResponse>(`/api/events/${id}`);
      return mapEvent(res);
    } catch {
      return undefined;
    }
  },

  async createEvent(data: {
    name: string;
    startDate: string;
    endDate: string;
    address?: string;
    transport: boolean;
    extraInfo?: string;
  }): Promise<Event> {
    const res = await apiClient.post<EventResponse>("/api/events", {
      name: data.name,
      startDate: data.startDate,
      endDate: data.endDate,
      address: data.address ?? null,
      transport: data.transport,
      extraInfo: data.extraInfo ?? null,
    });
    return mapEvent(res);
  },

  async updateEvent(
    id: string,
    data: {
      name?: string;
      startDate?: string;
      endDate?: string;
      address?: string;
      transport?: boolean;
      extraInfo?: string;
    },
  ): Promise<Event> {
    const res = await apiClient.patch<EventResponse>(`/api/events/${id}`, {
      name: data.name ?? null,
      startDate: data.startDate ?? null,
      endDate: data.endDate ?? null,
      address: data.address ?? null,
      transport: data.transport ?? null,
      extraInfo: data.extraInfo ?? null,
    });
    return mapEvent(res);
  },

  async createPerformance(
    eventId: string,
    data: {
      name: string;
      startTime: string;
      duration: number;
      rehearsalTime: string;
    },
  ): Promise<Performance> {
    const res = await apiClient.post<PerformanceResponse>(
      `/api/events/${eventId}/performances`,
      {
        name: data.name,
        startTime: data.startTime,
        duration: data.duration,
        rehearsalTime: data.rehearsalTime,
      },
    );
    return mapPerformance(res);
  },

  async deletePerformance(
    eventId: string,
    performanceId: string,
  ): Promise<void> {
    await apiClient.delete<void>(
      `/api/events/${eventId}/performances/${performanceId}`,
    );
  },

  async deleteEvent(id: string): Promise<void> {
    await apiClient.delete<void>(`/api/events/${id}`);
  },
};
