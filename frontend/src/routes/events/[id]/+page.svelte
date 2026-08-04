<script lang="ts">
	import { AlertTriangle } from '@lucide/svelte';
	import DataTable from '$lib/components/ui/DataTable.svelte';
	import { m } from '$lib/paraglide/messages';
	import { getLocale, localizeHref } from '$lib/paraglide/runtime';
	import type { ReservationWithNames } from '$lib/types/domain';
	import { formatDate } from '$lib/utils/dates';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();
	const locale = getLocale();
	const { event, reservations } = $derived(data);
	const hasConflicts = $derived(data.conflictCount > 0);
</script>

<div class="p-6">
	<div class="mb-6">
		<a href={localizeHref('/events')} class="text-blue-600 hover:underline mb-2 inline-block">
			← {m.eventdetail_back()}
		</a>
		<h1 class="text-2xl mb-2">{event.name}</h1>
		<div class="text-gray-600 space-y-1">
			<p>
				{formatDate(event.startDate, locale)} - {formatDate(event.endDate, locale)}
			</p>
			{#if event.location}
				<p>{m.eventdetail_location()}: {event.location}</p>
			{/if}
			{#if event.transportInfo}
				<p>{m.eventdetail_transport()}: {event.transportInfo}</p>
			{/if}
		</div>
	</div>

	{#if hasConflicts}
		<div class="bg-red-50 border border-red-200 rounded p-4 mb-6 flex items-start gap-3">
			<AlertTriangle class="text-red-600 flex-shrink-0" size={20} />
			<div>
				<p class="font-medium text-red-900">{m.eventdetail_conflicts_title()}</p>
				<p class="text-red-700 text-sm">{m.eventdetail_conflicts_body()}</p>
			</div>
		</div>
	{/if}

	<div class="mb-6">
		<h2 class="text-lg font-medium mb-3">{m.eventdetail_performances()}</h2>
		{#if event.performances.length > 0}
			<div class="space-y-2">
				{#each event.performances as performance}
					<div class="border rounded p-3 bg-gray-50">
						<p class="font-medium">{performance.name}</p>
						<p class="text-sm text-gray-600">
							{formatDate(performance.date, locale)} a las {performance.time}
						</p>
					</div>
				{/each}
			</div>
		{:else}
			<p class="text-gray-500">{m.eventdetail_no_performances()}</p>
		{/if}
	</div>

	<div>
		<h2 class="text-lg font-medium mb-3">{m.eventdetail_reservations({ count: reservations.length })}</h2>
		{#if reservations.length > 0}
			<DataTable
				rows={reservations}
				variant="gray"
				rowClass={(row: ReservationWithNames) => (row.hasConflict ? 'bg-red-50' : '')}
				columns={[
					{ header: m.reservations_product(), className: 'text-sm', cell: productCell },
					{ header: m.reservations_staff(), className: 'text-sm', cell: staffCell },
					{ header: m.reservations_period(), className: 'text-sm', cell: periodCell },
					{ header: m.reservations_status(), cell: statusCell }
				]}
			/>
		{:else}
			<p class="text-gray-500">{m.eventdetail_no_reservations()}</p>
		{/if}
	</div>
</div>

{#snippet productCell(row: ReservationWithNames)} {row.productName} {/snippet}

{#snippet staffCell(row: ReservationWithNames)} {row.employeeName} {/snippet}

{#snippet periodCell(row: ReservationWithNames)}
	{formatDate(row.startDate, locale)} - {formatDate(row.endDate, locale)}
{/snippet}

{#snippet statusCell(row: ReservationWithNames)}
	{#if row.hasConflict}
		<span class="inline-flex items-center gap-1 text-red-600">
			<AlertTriangle size={16} />
			{m.status_conflict()}
		</span>
	{/if}
{/snippet}