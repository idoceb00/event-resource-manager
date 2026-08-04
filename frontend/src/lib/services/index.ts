import { mockEquipmentService } from './mock/mockEquipmentService';
import { mockEventService } from './mock/mockEventService';
import { mockReservationService } from './mock/mockReservationService';
import { mockStaffService } from './mock/mockStaffService';
import type { EquipmentService, EventService, ReservationService, StaffService } from './types';

/**
 * Public service bindings.
 *
 * Components import services only from here. When the backend API is ready,
 * replace the mock bindings below with the corresponding `api*Service`
 * implementations — no component code changes.
 */
export const eventService: EventService = mockEventService;
export const equipmentService: EquipmentService = mockEquipmentService;
export const staffService: StaffService = mockStaffService;
export const reservationService: ReservationService = mockReservationService;