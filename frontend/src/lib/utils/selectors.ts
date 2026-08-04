import type { Employee, Equipment, Event, Reservation, ReservationWithNames } from '$lib/types/domain';

/**
 * Pure selection/aggregation helpers for the dashboard. Kept out of components
 * so the presentation layer only renders pre-computed values.
 */

export function getUpcomingEvents(events: Event[], limit = 5): Event[] {
	return events
		.filter((event) => new Date(event.startDate) >= new Date())
		.sort((a, b) => new Date(a.startDate).getTime() - new Date(b.startDate).getTime())
		.slice(0, limit);
}

export function countConflicts(reservations: Reservation[]): number {
	return reservations.filter((reservation) => reservation.hasConflict).length;
}

export function countActiveEquipment(equipment: Equipment[]): number {
	return equipment.filter((item) => item.status !== 'inactive').length;
}

export function countActiveStaff(staff: Employee[]): number {
	return staff.filter((employee) => employee.status === 'active').length;
}

export function filterEquipmentByStatus(
	equipment: Equipment[],
	status: Equipment['status'] | 'all'
): Equipment[] {
	if (status === 'all') return equipment;
	return equipment.filter((item) => item.status === status);
}

export function getConflictingReservations(
	reservations: ReservationWithNames[]
): ReservationWithNames[] {
	return reservations.filter((reservation) => reservation.hasConflict);
}