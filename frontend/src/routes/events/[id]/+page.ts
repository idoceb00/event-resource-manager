import { error } from "@sveltejs/kit";
import { eventService, reservationService } from "$lib/services";
import type { PageLoad } from "./$types";

export const load: PageLoad = async ({ params }) => {
  const event = await eventService.getEvent(params.id);

  if (!event) {
    throw error(404, "Event not found");
  }

  const reservations = await reservationService.getReservationsForEvent(
    event.id,
  );

  return {
    event,
    reservations,
    conflictCount: reservations.filter((reservation) => reservation.hasConflict)
      .length,
  };
};
