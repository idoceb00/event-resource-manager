import { apiClient } from "$lib/api/client";
import type {
  Equipment,
  EquipmentCategory,
  EquipmentStatus,
} from "$lib/types/domain";
import type { EquipmentService } from "../types";

interface BackendEquipmentResponse {
  id: number;
  name: string;
  category: EquipmentCategory;
  status: EquipmentStatus;
  stock: number;
}

interface CreateEquipmentRequest {
  name: string;
  category: EquipmentCategory;
  stock: number;
}

interface AdjustStockRequest {
  delta: number;
}

function mapEquipment(response: BackendEquipmentResponse): Equipment {
  return {
    id: String(response.id),
    name: response.name,
    category: response.category,
    status: response.status,
    stock: response.stock,
  };
}

export const apiEquipmentService: EquipmentService = {
  async getEquipment(): Promise<Equipment[]> {
    const response =
      await apiClient.get<BackendEquipmentResponse[]>("/api/equipment");
    return response.map(mapEquipment);
  },

  async getEquipmentById(id: string): Promise<Equipment> {
    const response = await apiClient.get<BackendEquipmentResponse>(
      `/api/equipment/${id}`,
    );
    return mapEquipment(response);
  },

  async getCatalogue(): Promise<Equipment[]> {
    const response = await apiClient.get<BackendEquipmentResponse[]>(
      "/api/equipment/catalogue",
    );
    return response.map(mapEquipment);
  },

  async createEquipment(data: {
    name: string;
    category: EquipmentCategory;
    stock: number;
  }): Promise<Equipment> {
    const body: CreateEquipmentRequest = {
      name: data.name,
      category: data.category,
      stock: data.stock,
    };
    const response = await apiClient.post<BackendEquipmentResponse>(
      "/api/equipment",
      body,
    );
    return mapEquipment(response);
  },

  async adjustStock(id: string, delta: number): Promise<Equipment> {
    const body: AdjustStockRequest = { delta };
    const response = await apiClient.patch<BackendEquipmentResponse>(
      `/api/equipment/${id}/stock`,
      body,
    );
    return mapEquipment(response);
  },

  async decatalogue(id: string): Promise<Equipment> {
    const response = await apiClient.post<BackendEquipmentResponse>(
      `/api/equipment/${id}/decatalogue`,
    );
    return mapEquipment(response);
  },

  async recatalogue(id: string): Promise<Equipment> {
    const response = await apiClient.post<BackendEquipmentResponse>(
      `/api/equipment/${id}/recatalogue`,
    );
    return mapEquipment(response);
  },
};
