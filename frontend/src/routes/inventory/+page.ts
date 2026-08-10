import { equipmentService } from "$lib/services";
import type { PageLoad } from "./$types";

export const load: PageLoad = async () => {
  return { equipment: await equipmentService.getEquipment() };
};
