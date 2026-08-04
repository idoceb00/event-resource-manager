import { eventService, reservationService } from '$lib/services';
import type { Event } from '$lib/types/domain';
import type { PageLoad } from './$types';

export interface EventRow {
	event: Event;
	reservationCount: number;
	hasConflict: boolean;
}

export const load: PageLoad = async () => {
	const events = await eventService.getEvents();

	const rows: EventRow[] = await Promise.all(
		events.map(async (event) => {
			const reservations = await reservationService.getReservationsForEvent(event.id);
			return {
				event,
				reservationCount: reservations.length,
				hasConflict: reservations.some((reservation) => reservation.hasConflict)
			};
		})
	);

	return { rows };
};