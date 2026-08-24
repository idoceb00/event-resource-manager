import { apiAuthService } from "./api/apiAuthService";
import { apiEquipmentService } from "./api/apiEquipmentService";
import { apiEventService } from "./api/apiEventService";
import { apiReservationService } from "./api/apiReservationService";
import { apiUserService } from "./api/apiUserService";
import type {
  AuthService,
  EquipmentService,
  EventService,
  ReservationService,
  UserService,
} from "./types";

/**
 * Public service bindings.
 *
 * Components import services only from here. When the backend API is ready,
 * replace the mock bindings below with the corresponding `api*Service`
 * implementations — no component code changes.
 */
export const eventService: EventService = apiEventService;
export const equipmentService: EquipmentService = apiEquipmentService;
export const userService: UserService = apiUserService;
export const reservationService: ReservationService = apiReservationService;
export const authService: AuthService = apiAuthService;
