import { products } from "$lib/data/mock-data";
import type { Equipment } from "$lib/types/domain";
import type { EquipmentService } from "../types";

export const mockEquipmentService: EquipmentService = {
  async getEquipment(): Promise<Equipment[]> {
    return products;
  },
};
