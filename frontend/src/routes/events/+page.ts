import { eventService, reservationService } from "$lib/services";
import type { Event } from "$lib/types/domain";
import type { PageLoad } from "./$types";

export interface EventRow {
  event: Event;
  reservationCount: number;
}

export const load: PageLoad = async () => {
  const events = await eventService.getEvents();

  const rows: EventRow[] = await Promise.all(
    events.map(async (event) => {
      const reservations = await reservationService.getReservationsForEvent(
        event.id,
      );
      const reservationCount = reservations.reduce(
        (sum, r) => sum + r.lines.length,
        0,
      );
      return { event, reservationCount };
    }),
  );

  return { rows };
};
