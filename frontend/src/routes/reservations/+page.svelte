<script lang="ts">
  import { AlertTriangle } from "@lucide/svelte";
  import { resolve } from "$app/paths";
  import DataTable from "$lib/components/ui/DataTable.svelte";
  import { m } from "$lib/paraglide/messages";
  import { getLocale, localizeHref } from "$lib/paraglide/runtime";
  import type { ReservationWithNames } from "$lib/types/domain";
  import { formatDate } from "$lib/utils/dates";
  import type { PageData } from "./$types";

  let { data }: { data: PageData } = $props();
  const locale = getLocale();
  const conflictCount = $derived(data.conflicts.length);
</script>

<div class="p-6">
  <div class="mb-6">
    <h1 class="text-2xl mb-4">{m.reservations()}</h1>
    {#if conflictCount > 0}
      <div
        class="bg-red-50 border border-red-200 rounded p-3 flex items-start gap-3"
      >
        <AlertTriangle class="text-red-600 flex-shrink-0" size={20} />
        <p class="text-sm text-red-900">
          {conflictCount === 1
            ? m.reservations_conflict_one()
            : m.reservations_conflict_other()}
        </p>
      </div>
    {/if}
  </div>

  <DataTable
    rows={data.reservations}
    variant="gray"
    rowKey={(row: ReservationWithNames) => row.id}
    rowClass={(row: ReservationWithNames) =>
      row.hasConflict ? "bg-red-50" : "hover:bg-gray-50"}
    columns={[
      { header: m.reservations_event(), cell: eventCell },
      {
        header: m.reservations_product(),
        className: "text-sm",
        cell: productCell,
      },
      { header: m.reservations_staff(), className: "text-sm", cell: staffCell },
      { header: m.reservations_start(), className: "text-sm", cell: startCell },
      { header: m.reservations_end(), className: "text-sm", cell: endCell },
      { header: m.reservations_status(), cell: statusCell },
    ]}
  />
</div>

{#snippet eventCell(row: ReservationWithNames)}
  <a
    href={resolve(localizeHref(`/events/${row.eventId}`) as "/events/[id]", {
      id: row.eventId,
    })}
    class="text-blue-600 hover:underline"
  >
    {row.eventName}
  </a>
{/snippet}

{#snippet productCell(row: ReservationWithNames)}
  {row.productName}
{/snippet}

{#snippet staffCell(row: ReservationWithNames)}
  {row.employeeName}
{/snippet}

{#snippet startCell(row: ReservationWithNames)}
  {formatDate(row.startDate, locale)}
{/snippet}

{#snippet endCell(row: ReservationWithNames)}
  {formatDate(row.endDate, locale)}
{/snippet}

{#snippet statusCell(row: ReservationWithNames)}
  {#if row.hasConflict}
    <span class="inline-flex items-center gap-1 text-red-600">
      <AlertTriangle size={16} />
      {m.status_conflict()}
    </span>
  {/if}
{/snippet}
