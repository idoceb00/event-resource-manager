import { eventService, reservationService } from '$lib/services';
import type { PageLoad } from './$types';

export const load: PageLoad = async () => {
	const events = await eventService.getEvents();

	const conflicts: Record<string, boolean> = {};
	for (const event of events) {
		const reservations = await reservationService.getReservationsForEvent(event.id);
		conflicts[event.id] = reservations.some((reservation) => reservation.hasConflict);
	}

	return { events, conflicts };
};