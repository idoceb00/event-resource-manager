import type { User } from "$lib/types/domain";

/**
 * Shared current-user state. Mocked until authentication is wired to the
 * backend; components never fetch or own this state themselves.
 */

const userState = $state<User>({
  id: "user-admin",
  name: "Usuario administrador",
});

export const currentUser = {
  get user(): User {
    return userState;
  },
};
