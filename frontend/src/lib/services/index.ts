import { apiAuthService } from "./api/apiAuthService";
import { apiEquipmentService } from "./api/apiEquipmentService";
import { mockEventService } from "./mock/mockEventService";
import { mockReservationService } from "./mock/mockReservationService";
import { mockStaffService } from "./mock/mockStaffService";
import type {
  AuthService,
  EquipmentService,
  EventService,
  ReservationService,
  StaffService,
} from "./types";

/**
 * Public service bindings.
 *
 * Components import services only from here. When the backend API is ready,
 * replace the mock bindings below with the corresponding `api*Service`
 * implementations — no component code changes.
 */
export const eventService: EventService = mockEventService;
export const equipmentService: EquipmentService = apiEquipmentService;
export const staffService: StaffService = mockStaffService;
export const reservationService: ReservationService = mockReservationService;
export const authService: AuthService = apiAuthService;
