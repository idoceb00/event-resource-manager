<script lang="ts">
  import { resolve } from "$app/paths";
  import DataTable from "$lib/components/ui/DataTable.svelte";
  import { m } from "$lib/paraglide/messages";
  import { localizeHref } from "$lib/paraglide/runtime";
  import type { Reservation, ReservationLine } from "$lib/types/domain";
  import type { PageData } from "./$types";

  let { data }: { data: PageData } = $props();

  interface ReservationLineRow {
    reservationId: string;
    event: Reservation["event"];
    line: ReservationLine;
  }

  const rows: ReservationLineRow[] = $derived(
    data.reservations.flatMap((r) =>
      r.lines.map((line) => ({
        reservationId: r.id,
        event: r.event,
        line,
      })),
    ),
  );
</script>

<div class="p-6">
  <div class="mb-6">
    <h1 class="text-2xl mb-4">{m.reservations()}</h1>
  </div>

  <DataTable
    {rows}
    variant="gray"
    rowKey={(row: ReservationLineRow) => row.line.id}
    columns={[
      { header: m.reservations_event(), cell: eventCell },
      {
        header: m.reservations_product(),
        className: "text-sm",
        cell: productCell,
      },
      { header: m.reservations_staff(), className: "text-sm", cell: staffCell },
      {
        header: m.reservations_quantity(),
        className: "text-sm",
        cell: quantityCell,
      },
    ]}
  />
</div>

{#snippet eventCell(row: ReservationLineRow)}
  <a
    href={resolve(localizeHref(`/events/${row.event.id}`) as "/events/[id]", {
      id: row.event.id,
    })}
    class="text-blue-600 hover:underline"
  >
    {row.event.name}
  </a>
{/snippet}

{#snippet productCell(row: ReservationLineRow)}
  {row.line.equipment.name}
{/snippet}

{#snippet staffCell(row: ReservationLineRow)}
  {row.line.author.name}
{/snippet}

{#snippet quantityCell(row: ReservationLineRow)}
  {row.line.quantity}
{/snippet}
