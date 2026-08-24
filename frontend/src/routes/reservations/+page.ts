import { reservationService } from "$lib/services";
import type { PageLoad } from "./$types";

export const load: PageLoad = async () => {
  const reservations = await reservationService.getReservations();
  return { reservations };
};
