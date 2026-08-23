import { equipmentService } from "$lib/services";
import type { PageLoad } from "./$types";

export const load: PageLoad = async () => {
  const [catalogue, allEquipment] = await Promise.all([
    equipmentService.getCatalogue(),
    equipmentService.getEquipment(),
  ]);
  return { equipment: catalogue, allEquipment };
};
