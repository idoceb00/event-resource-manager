import { equipmentService, eventService, reservationService, staffService } from '$lib/services';
import {
	countActiveEquipment,
	countActiveStaff,
	countConflicts,
	getUpcomingEvents
} from '$lib/utils/selectors';
import type { PageLoad } from './$types';

export const load: PageLoad = async () => {
	const [events, equipment, employees, reservations] = await Promise.all([
		eventService.getEvents(),
		equipmentService.getEquipment(),
		staffService.getStaff(),
		reservationService.getReservations()
	]);

	return {
		stats: {
			activeProducts: countActiveEquipment(equipment),
			activeStaff: countActiveStaff(employees),
			upcomingEventsCount: getUpcomingEvents(events).length,
			conflicts: countConflicts(reservations)
		},
		upcomingEvents: getUpcomingEvents(events)
	};
};