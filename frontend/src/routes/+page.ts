import {
  equipmentService,
  eventService,
  reservationService,
} from "$lib/services";
import {
  countActiveEquipment,
  countReservationLines,
  getUpcomingEvents,
} from "$lib/utils/selectors";
import type { PageLoad } from "./$types";

export const load: PageLoad = async () => {
  const [events, equipment, reservations] = await Promise.all([
    eventService.getEvents(),
    equipmentService.getEquipment(),
    reservationService.getReservations(),
  ]);

  return {
    stats: {
      activeProducts: countActiveEquipment(equipment),
      upcomingEventsCount: getUpcomingEvents(events).length,
      reservationLines: countReservationLines(reservations),
    },
    upcomingEvents: getUpcomingEvents(events),
  };
};
