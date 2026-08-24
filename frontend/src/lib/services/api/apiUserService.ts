import { apiClient } from "$lib/api/client";
import type { User } from "$lib/types/domain";
import type { UserService } from "../types";

interface BackendUserResponse {
  id: number;
  username: string;
  name: string;
  role: string;
  active: boolean;
}

function mapUser(r: BackendUserResponse): User {
  return {
    id: String(r.id),
    username: r.username,
    name: r.name ?? r.username,
    role: r.role === "ADMINISTRATOR" ? "admin" : "employee",
    active: r.active,
  };
}

export const apiUserService: UserService = {
  async getUsers(): Promise<User[]> {
    const response = await apiClient.get<BackendUserResponse[]>("/api/users");
    return response.map(mapUser);
  },

  async getUserById(id: string): Promise<User> {
    const response = await apiClient.get<BackendUserResponse>(
      `/api/users/${id}`,
    );
    return mapUser(response);
  },
};
