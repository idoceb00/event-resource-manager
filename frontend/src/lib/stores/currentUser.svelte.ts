import type { User } from "$lib/types/domain";

export type SessionStatus = "unknown" | "authenticated" | "anonymous";

let userState = $state<User | null>(null);
let statusState = $state<SessionStatus>("unknown");

export const session = {
  get user(): User | null {
    return userState;
  },
  get status(): SessionStatus {
    return statusState;
  },

  setUser(user: User): void {
    userState = user;
    statusState = "authenticated";
  },

  clear(): void {
    userState = null;
    statusState = "anonymous";
  },

  setStatus(status: SessionStatus): void {
    statusState = status;
  },
};
