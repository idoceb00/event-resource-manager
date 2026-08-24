import { eventService } from "$lib/services";
import type { PageLoad } from "./$types";

export const load: PageLoad = async () => {
  const events = await eventService.getEvents();
  return { events };
};
