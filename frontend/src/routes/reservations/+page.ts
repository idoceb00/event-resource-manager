import { reservationService } from "$lib/services";
import { getConflictingReservations } from "$lib/utils/selectors";
import type { PageLoad } from "./$types";

export const load: PageLoad = async () => {
  const reservations = await reservationService.getReservations();
  return {
    reservations,
    conflicts: getConflictingReservations(reservations),
  };
};
