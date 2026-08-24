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

  async createUser(data: {
    username: string;
    name: string;
    password: string;
    role: "admin" | "employee";
    active: boolean;
  }): Promise<User> {
    const response = await apiClient.post<BackendUserResponse>("/api/users", {
      username: data.username,
      name: data.name,
      password: data.password,
      role: data.role === "admin" ? "ADMINISTRATOR" : "EMPLOYEE",
      active: data.active,
    });
    return mapUser(response);
  },

  async updateUser(
    id: string,
    data: { name?: string; role?: "admin" | "employee" },
  ): Promise<User> {
    const body: Record<string, unknown> = {};
    if (data.name !== undefined) body.name = data.name;
    if (data.role !== undefined)
      body.role = data.role === "admin" ? "ADMINISTRATOR" : "EMPLOYEE";
    const response = await apiClient.put<BackendUserResponse>(
      `/api/users/${id}`,
      body,
    );
    return mapUser(response);
  },

  async changePassword(id: string, newPassword: string): Promise<void> {
    await apiClient.patch(`/api/users/${id}/password`, {
      newPassword,
    });
  },

  async setActivation(
    id: string,
    active: boolean,
    confirmPassword?: string,
  ): Promise<void> {
    await apiClient.patch(`/api/users/${id}/activation`, {
      active,
      confirmPassword: confirmPassword ?? null,
    });
  },
};
