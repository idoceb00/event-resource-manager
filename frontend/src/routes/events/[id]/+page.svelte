<script lang="ts">
  import { AlertTriangle, Pencil, Trash2, Plus } from "@lucide/svelte";
  import { resolve } from "$app/paths";
  import { invalidateAll } from "$app/navigation";
  import Button from "$lib/components/ui/Button.svelte";
  import DataTable from "$lib/components/ui/DataTable.svelte";
  import { eventService } from "$lib/services";
  import { m } from "$lib/paraglide/messages";
  import { getLocale, localizeHref } from "$lib/paraglide/runtime";
  import type { ReservationWithNames } from "$lib/types/domain";
  import { formatDate } from "$lib/utils/dates";
  import { ApiError } from "$lib/api/client";
  import type { PageData } from "./$types";

  let { data }: { data: PageData } = $props();
  const locale = getLocale();
  const { event, reservations } = $derived(data);
  const hasConflicts = $derived(data.conflictCount > 0);

  let showPerformanceForm = $state(false);
  let perfName = $state("");
  let perfStartTime = $state("");
  let perfDuration = $state(120);
  let perfRehearsalTime = $state("");
  let perfError = $state("");
  let perfSaving = $state(false);

  async function handleAddPerformance() {
    perfError = "";
    if (!perfName.trim() || !perfStartTime || !perfRehearsalTime) {
      perfError = "Todos los campos son obligatorios";
      return;
    }
    perfSaving = true;
    try {
      await eventService.createPerformance(event.id, {
        name: perfName.trim(),
        startTime: perfStartTime + ":00",
        duration: perfDuration,
        rehearsalTime: perfRehearsalTime + ":00",
      });
      showPerformanceForm = false;
      perfName = "";
      perfStartTime = "";
      perfDuration = 120;
      perfRehearsalTime = "";
      await invalidateAll();
    } catch (e) {
      if (e instanceof ApiError && e.status === 409) {
        perfError = m.performance_duplicate();
      } else {
        perfError = m.performance_add_error();
      }
    } finally {
      perfSaving = false;
    }
  }

  async function handleDeletePerformance(performanceId: string) {
    try {
      await eventService.deletePerformance(event.id, performanceId);
      await invalidateAll();
    } catch {
      // silent
    }
  }
</script>

<div class="p-6">
  <div class="mb-6">
    <a
      href={resolve(localizeHref("/events") as "/events")}
      class="text-blue-600 hover:underline mb-2 inline-block"
    >
      ← {m.eventdetail_back()}
    </a>
    <div class="flex items-center justify-between">
      <h1 class="text-2xl">{event.name}</h1>
      <a
        href={resolve(localizeHref(`/events/${event.id}/edit`) as "/events/[id]/edit", { id: event.id })}
      >
        <Button variant="secondary">
          <Pencil class="w-4 h-4 mr-1" />
          {m.event_form_edit_title()}
        </Button>
      </a>
    </div>
    <div class="text-gray-600 space-y-1 mt-2">
      <p>
        {formatDate(event.startDate, locale)} - {formatDate(
          event.endDate,
          locale,
        )}
      </p>
      {#if event.address}
        <p>{m.eventdetail_location()}: {event.address}</p>
      {/if}
      {#if event.transport}
        <p>{m.eventdetail_transport()}: {m.eventdetail_transport_yes()}</p>
      {/if}
      {#if event.extraInfo}
        <p>{m.eventdetail_extra_info()}: {event.extraInfo}</p>
      {/if}
    </div>
  </div>

  {#if hasConflicts}
    <div
      class="bg-red-50 border border-red-200 rounded p-4 mb-6 flex items-start gap-3"
    >
      <AlertTriangle class="text-red-600 flex-shrink-0" size={20} />
      <div>
        <p class="font-medium text-red-900">
          {m.eventdetail_conflicts_title()}
        </p>
        <p class="text-red-700 text-sm">{m.eventdetail_conflicts_body()}</p>
      </div>
    </div>
  {/if}

  <div class="mb-6">
    <div class="flex items-center justify-between mb-3">
      <h2 class="text-lg font-medium">{m.eventdetail_performances()}</h2>
      <Button variant="secondary" onclick={() => (showPerformanceForm = !showPerformanceForm)}>
        <Plus class="w-4 h-4 mr-1" />
        {m.performance_add()}
      </Button>
    </div>

    {#if showPerformanceForm}
      <div class="border rounded p-4 bg-gray-50 mb-4">
        {#if perfError}
          <div class="bg-red-50 border border-red-200 rounded p-3 mb-3 text-red-700 text-sm">
            {perfError}
          </div>
        {/if}
        <form onsubmit={(e) => { e.preventDefault(); handleAddPerformance(); }} class="space-y-3">
          <div class="grid grid-cols-2 gap-3">
            <div>
              <label for="perfName" class="block text-sm font-medium text-neutral-700 mb-1">{m.performance_form_name()}</label>
              <input id="perfName" type="text" bind:value={perfName} class="w-full border border-neutral-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-neutral-900" />
            </div>
            <div>
              <label for="perfDuration" class="block text-sm font-medium text-neutral-700 mb-1">{m.performance_form_duration()}</label>
              <input id="perfDuration" type="number" min="1" bind:value={perfDuration} class="w-full border border-neutral-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-neutral-900" />
            </div>
          </div>
          <div class="grid grid-cols-2 gap-3">
            <div>
              <label for="perfStartTime" class="block text-sm font-medium text-neutral-700 mb-1">{m.performance_form_start_time()}</label>
              <input id="perfStartTime" type="time" bind:value={perfStartTime} class="w-full border border-neutral-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-neutral-900" />
            </div>
            <div>
              <label for="perfRehearsalTime" class="block text-sm font-medium text-neutral-700 mb-1">{m.performance_form_rehearsal_time()}</label>
              <input id="perfRehearsalTime" type="time" bind:value={perfRehearsalTime} class="w-full border border-neutral-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-neutral-900" />
            </div>
          </div>
          <div class="flex justify-end gap-2">
            <Button variant="secondary" type="button" onclick={() => (showPerformanceForm = false)}>Cancelar</Button>
            <Button type="submit" disabled={perfSaving}>{perfSaving ? "..." : m.performance_add()}</Button>
          </div>
        </form>
      </div>
    {/if}

    {#if event.performances.length > 0}
      <div class="space-y-2">
        {#each event.performances as performance (performance.id)}
          <div class="border rounded p-3 bg-gray-50 flex items-start justify-between">
            <div>
              <p class="font-medium">{performance.name}</p>
              <p class="text-sm text-gray-600">
                {m.eventdetail_performance_start()}: {performance.startTime}
                · {m.eventdetail_performance_duration({ minutes: performance.duration })}
                · {m.eventdetail_performance_rehearsal()}: {performance.rehearsalTime}
              </p>
            </div>
            <button
              onclick={() => handleDeletePerformance(performance.id)}
              class="text-neutral-400 hover:text-red-600 p-1"
              title={m.performance_delete()}
            >
              <Trash2 class="w-4 h-4" />
            </button>
          </div>
        {/each}
      </div>
    {:else}
      <p class="text-gray-500">{m.eventdetail_no_performances()}</p>
    {/if}
  </div>

  <div>
    <h2 class="text-lg font-medium mb-3">
      {m.eventdetail_reservations({ count: reservations.length })}
    </h2>
    {#if reservations.length > 0}
      <DataTable
        rows={reservations}
        variant="gray"
        rowKey={(row: ReservationWithNames) => row.id}
        rowClass={(row: ReservationWithNames) =>
          row.hasConflict ? "bg-red-50" : ""}
        columns={[
          {
            header: m.reservations_product(),
            className: "text-sm",
            cell: productCell,
          },
          {
            header: m.reservations_staff(),
            className: "text-sm",
            cell: staffCell,
          },
          {
            header: m.reservations_period(),
            className: "text-sm",
            cell: periodCell,
          },
          { header: m.reservations_status(), cell: statusCell },
        ]}
      />
    {:else}
      <p class="text-gray-500">{m.eventdetail_no_reservations()}</p>
    {/if}
  </div>
</div>

{#snippet productCell(row: ReservationWithNames)}
  {row.productName}
{/snippet}

{#snippet staffCell(row: ReservationWithNames)}
  {row.employeeName}
{/snippet}

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
