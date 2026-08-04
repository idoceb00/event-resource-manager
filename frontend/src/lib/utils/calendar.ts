import type { Event } from '$lib/types/domain';

/**
 * Pure calendar logic: builds the month grid (weeks of days, including leading
 * and trailing days from adjacent months) and filters events that fall on a
 * given day. Components only render the result.
 */

export function buildMonthGrid(year: number, month: number): Date[][] {
	const monthStart = new Date(year, month, 1);
	const monthEnd = new Date(year, month + 1, 0);

	const startDate = new Date(monthStart);
	startDate.setDate(startDate.getDate() - startDate.getDay());

	const endDate = new Date(monthEnd);
	endDate.setDate(endDate.getDate() + (6 - endDate.getDay()));

	const days: Date[] = [];
	const current = new Date(startDate);
	while (current <= endDate) {
		days.push(new Date(current));
		current.setDate(current.getDate() + 1);
	}

	const weeks: Date[][] = [];
	for (let i = 0; i < days.length; i += 7) {
		weeks.push(days.slice(i, i + 7));
	}
	return weeks;
}

export function getEventsForDay(day: Date, events: Event[]): Event[] {
	const dayStart = new Date(day.getFullYear(), day.getMonth(), day.getDate()).getTime();
	return events.filter((event) => {
		const start = new Date(event.startDate).getTime();
		const end = new Date(event.endDate).getTime();
		return dayStart >= start && dayStart <= end;
	});
}

export function isSameMonth(day: Date, referenceMonth: Date): boolean {
	return day.getMonth() === referenceMonth.getMonth();
}

/**
 * Returns the month (first of month) that contains the earliest event, so the
 * calendar always opens on a month with content. Falls back to the current
 * month when there are no events.
 */
export function initialCalendarMonth(events: Event[]): Date {
	if (events.length === 0) return new Date();
	const earliest = [...events].sort(
		(a, b) => new Date(a.startDate).getTime() - new Date(b.startDate).getTime()
	)[0];
	const date = new Date(earliest.startDate);
	return new Date(date.getFullYear(), date.getMonth(), 1);
}