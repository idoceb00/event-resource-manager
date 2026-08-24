import { userService } from "$lib/services";
import type { PageLoad } from "./$types";

export const load: PageLoad = async () => {
  return { users: await userService.getUsers() };
};
