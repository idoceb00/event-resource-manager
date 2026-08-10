<script lang="ts">
  import { AlertTriangle } from "@lucide/svelte";
  import { resolve } from "$app/paths";
  import Button from "$lib/components/ui/Button.svelte";
  import DataTable from "$lib/components/ui/DataTable.svelte";
  import { m } from "$lib/paraglide/messages";
  import { getLocale, localizeHref } from "$lib/paraglide/runtime";
  import { formatDateRangeWithYear } from "$lib/utils/dates";
  import type { EventRow } from "./+page.ts";
  import type { PageData } from "./$types";

  let { data }: { data: PageData } = $props();
  const locale = getLocale();
</script>

<div class="p-6 max-w-7xl">
  <div class="flex items-center justify-between mb-6">
    <h1>{m.events()}</h1>
    <Button>{m.events_new()}</Button>
  </div>

  <DataTable
    rows={data.rows}
    variant="neutral"
    rowKey={(row: EventRow) => row.event.id}
    columns={[
      { header: m.events_event_name(), cell: eventNameCell },
      {
        header: m.events_date_range(),
        className: "text-sm text-neutral-600",
        cell: dateRangeCell,
      },
      {
        header: m.events_location(),
        className: "text-sm text-neutral-600",
        cell: locationCell,
      },
      {
        header: m.events_performances(),
        className: "text-sm text-neutral-600",
        cell: performancesCell,
      },
      {
        header: m.events_reservations(),
        className: "text-sm text-neutral-600",
        cell: reservationsCell,
      },
      { header: m.events_status(), cell: statusCell },
    ]}
  />
</div>

{#snippet eventNameCell(row: EventRow)}
  <a
    href={resolve(localizeHref(`/events/${row.event.id}`) as "/events/[id]", {
      id: row.event.id,
    })}
    class="font-medium text-neutral-900 hover:underline"
  >
    {row.event.name}
  </a>
{/snippet}

{#snippet dateRangeCell(row: EventRow)}
  {formatDateRangeWithYear(row.event.startDate, row.event.endDate, locale)}
{/snippet}

{#snippet locationCell(row: EventRow)}
  {row.event.location}
{/snippet}

{#snippet performancesCell(row: EventRow)}
  {row.event.performances.length}
{/snippet}

{#snippet reservationsCell(row: EventRow)}
  {row.reservationCount}
{/snippet}

{#snippet statusCell(row: EventRow)}
  {#if row.hasConflict}
    <span
      class="inline-flex items-center gap-1 px-2 py-1 bg-amber-100 text-amber-900 text-xs rounded"
    >
      <AlertTriangle class="w-3 h-3" />
      {m.status_conflict()}
    </span>
  {:else}
    <span
      class="inline-flex items-center px-2 py-1 bg-green-100 text-green-900 text-xs rounded"
    >
      {m.status_ok()}
    </span>
  {/if}
{/snippet}
