import type { EquipmentStatus } from "$lib/types/domain";

/**
 * Maps domain statuses to badge color variants. Labels are handled by the
 * i18n layer in the components; this only decides the visual treatment.
 */

export type BadgeVariant = "green" | "blue" | "gray" | "amber" | "red";

export function equipmentStatusVariant(status: EquipmentStatus): BadgeVariant {
  switch (status) {
    case "CATALOGUED":
      return "green";
    case "DECATALOGUED":
      return "gray";
  }
}
