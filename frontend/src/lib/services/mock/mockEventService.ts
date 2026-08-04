import { events } from '$lib/data/mock-data';
import type { Event } from '$lib/types/domain';
import type { EventService } from '../types';

export const mockEventService: EventService = {
	async getEvents(): Promise<Event[]> {
		return events;
	},

	async getEvent(id: string): Promise<Event | undefined> {
		return events.find((event) => event.id === id);
	}
};