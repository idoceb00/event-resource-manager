import { apiClient } from "$lib/api/client";
import type { User } from "$lib/types/domain";
import type { AuthService } from "../types";

interface BackendAuthResponse {
  id: number;
  username: string;
  name: string;
  role: "ADMINISTRATOR" | "EMPLOYEE";
}

function mapUser(response: BackendAuthResponse): User {
  return {
    id: String(response.id),
    username: response.username,
    name: response.name ?? response.username,
    role: response.role === "ADMINISTRATOR" ? "admin" : "employee",
    active: true,
  };
}

export const apiAuthService: AuthService = {
  async login(username: string, password: string): Promise<User> {
    const response = await apiClient.post<BackendAuthResponse>(
      "/api/auth/login",
      {
        username,
        password,
      },
    );
    return mapUser(response);
  },

  async logout(): Promise<void> {
    await apiClient.post("/api/auth/logout");
  },

  async me(): Promise<User | null> {
    try {
      const response = await apiClient.get<BackendAuthResponse>("/api/auth/me");
      return mapUser(response);
    } catch {
      return null;
    }
  },
};
