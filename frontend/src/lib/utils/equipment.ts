import type { EquipmentCategory, EquipmentStatus } from "$lib/types/domain";
import { m } from "$lib/paraglide/messages";

export const categoryLabels: Record<EquipmentCategory, () => string> = {
  SOUND: m.category_sound,
  LIGHTING: m.category_lighting,
  MOTORS_AND_STRUCTURES: m.category_motors_and_structures,
  VIDEO: m.category_video,
};

export const statusLabels: Record<EquipmentStatus, () => string> = {
  CATALOGUED: m.status_catalogued,
  DECATALOGUED: m.status_decatalogued,
};
