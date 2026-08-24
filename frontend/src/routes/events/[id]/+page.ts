import { error } from "@sveltejs/kit";
import {
  eventService,
  reservationService,
  equipmentService,
} from "$lib/services";
import type { PageLoad } from "./$types";

export const load: PageLoad = async ({ params }) => {
  const event = await eventService.getEvent(params.id);

  if (!event) {
    throw error(404, "Event not found");
  }

  const [reservations, catalogue] = await Promise.all([
    reservationService.getReservationsForEvent(event.id),
    equipmentService.getCatalogue(),
  ]);

  return {
    event,
    reservations,
    catalogue,
  };
};
