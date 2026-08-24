<script lang="ts">
  import { Pencil, Trash2, Plus } from "@lucide/svelte";
  import { goto } from "$app/navigation";
  import { resolve } from "$app/paths";
  import { invalidateAll } from "$app/navigation";
  import Button from "$lib/components/ui/Button.svelte";
  import DataTable from "$lib/components/ui/DataTable.svelte";
  import { eventService, reservationService } from "$lib/services";
  import { m } from "$lib/paraglide/messages";
  import { getLocale, localizeHref } from "$lib/paraglide/runtime";
  import type { EquipmentCategory, ReservationLine } from "$lib/types/domain";
  import { formatDate } from "$lib/utils/dates";
  import { ApiError } from "$lib/api/client";
  import { toast } from "$lib/stores/toast.svelte";
  import type { PageData } from "./$types";

  const categoryLabel: Record<EquipmentCategory, () => string> = {
    SOUND: m.category_sound,
    LIGHTING: m.category_lighting,
    MOTORS_AND_STRUCTURES: m.category_motors_and_structures,
    VIDEO: m.category_video,
  };

  let { data }: { data: PageData } = $props();
  const locale = getLocale();
  const { event, reservations, catalogue } = $derived(data);

  const reservationLines = $derived(
    reservations.flatMap((r) =>
      r.lines.map((line) => ({
        ...line,
        reservationId: r.id,
      })),
    ),
  );

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

  let showDeleteConfirm = $state(false);
  let deleteLoading = $state(false);

  async function handleDeleteEvent() {
    deleteLoading = true;
    try {
      await eventService.deleteEvent(event.id);
      goto(resolve(localizeHref("/events") as "/events"));
    } catch {
      showDeleteConfirm = false;
      deleteLoading = false;
    }
  }

  // --- Reserve form ---
  let showReserveForm = $state(false);
  let pendingLines = $state<Array<{ equipmentId: string; quantity: number }>>(
    [],
  );
  let reserveSaving = $state(false);

  function addPendingLine() {
    pendingLines = [...pendingLines, { equipmentId: "", quantity: 1 }];
  }

  function removePendingLine(index: number) {
    pendingLines = pendingLines.filter((_, i) => i !== index);
  }

  function updatePendingLine(
    index: number,
    field: "equipmentId" | "quantity",
    value: string | number,
  ) {
    pendingLines = pendingLines.map((line, i) =>
      i === index ? { ...line, [field]: value } : line,
    );
  }

  async function handleReserve() {
    const validLines = pendingLines.filter(
      (l) => l.equipmentId && l.quantity > 0,
    );
    if (validLines.length === 0) return;

    reserveSaving = true;
    try {
      const result = await reservationService.createReservation(
        event.id,
        validLines.map((l) => ({
          equipmentId: Number(l.equipmentId),
          quantity: l.quantity,
        })),
      );

      if (result.failed === 0) {
        toast.show(
          m.reserve_success({ succeeded: result.succeeded, suffix: "" }),
        );
      } else if (result.succeeded === 0) {
        toast.show(result.errors[0] ?? "Error");
      } else {
        const suffix = m.reserve_suffix_failed({ failed: result.failed });
        toast.show(m.reserve_success({ succeeded: result.succeeded, suffix }));
      }

      showReserveForm = false;
      pendingLines = [];
      await invalidateAll();
    } catch {
      // handled per-line above
    } finally {
      reserveSaving = false;
    }
  }

  // --- Delete reservation line ---
  let deleteLineId = $state<string | null>(null);

  async function handleDeleteLine(lineId: string) {
    try {
      await reservationService.deleteReservationLine(lineId);
      toast.show(m.reserve_line_delete_success());
      deleteLineId = null;
      await invalidateAll();
    } catch {
      toast.show(m.reserve_line_delete_error());
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
      <div class="flex items-center gap-2">
        {#if showDeleteConfirm}
          <span class="text-sm text-neutral-600">
            {m.event_delete_confirm_body({
              name: event.name,
              performances: String(event.performances.length),
              reservations: String(reservationLines.length),
            })}
          </span>
          <Button onclick={handleDeleteEvent} disabled={deleteLoading}>
            {deleteLoading ? "..." : m.event_delete_confirm_title()}
          </Button>
          <Button variant="icon" onclick={() => (showDeleteConfirm = false)}>
            &times;
          </Button>
        {:else}
          <a
            href={resolve(
              localizeHref(`/events/${event.id}/edit`) as "/events/[id]/edit",
              { id: event.id },
            )}
          >
            <Button variant="secondary">
              <Pencil class="w-4 h-4 mr-1" />
              {m.event_form_edit_title()}
            </Button>
          </a>
          <Button variant="delete" onclick={() => (showDeleteConfirm = true)}>
            <Trash2 class="w-4 h-4 mr-1" />
            {m.event_delete()}
          </Button>
        {/if}
      </div>
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

  <div class="mb-6">
    <div class="flex items-center justify-between mb-3">
      <h2 class="text-lg font-medium">{m.eventdetail_performances()}</h2>
      <Button
        variant="secondary"
        onclick={() => (showPerformanceForm = !showPerformanceForm)}
      >
        <Plus class="w-4 h-4 mr-1" />
        {m.performance_add()}
      </Button>
    </div>

    {#if showPerformanceForm}
      <div class="border rounded p-4 bg-gray-50 mb-4">
        {#if perfError}
          <div
            class="bg-red-50 border border-red-200 rounded p-3 mb-3 text-red-700 text-sm"
          >
            {perfError}
          </div>
        {/if}
        <form
          onsubmit={(e) => {
            e.preventDefault();
            handleAddPerformance();
          }}
          class="space-y-3"
        >
          <div class="grid grid-cols-2 gap-3">
            <div>
              <label
                for="perfName"
                class="block text-sm font-medium text-neutral-700 mb-1"
                >{m.performance_form_name()}</label
              >
              <input
                id="perfName"
                type="text"
                bind:value={perfName}
                class="w-full border border-neutral-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-neutral-900"
              />
            </div>
            <div>
              <label
                for="perfDuration"
                class="block text-sm font-medium text-neutral-700 mb-1"
                >{m.performance_form_duration()}</label
              >
              <input
                id="perfDuration"
                type="number"
                min="1"
                bind:value={perfDuration}
                class="w-full border border-neutral-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-neutral-900"
              />
            </div>
          </div>
          <div class="grid grid-cols-2 gap-3">
            <div>
              <label
                for="perfStartTime"
                class="block text-sm font-medium text-neutral-700 mb-1"
                >{m.performance_form_start_time()}</label
              >
              <input
                id="perfStartTime"
                type="time"
                bind:value={perfStartTime}
                class="w-full border border-neutral-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-neutral-900"
              />
            </div>
            <div>
              <label
                for="perfRehearsalTime"
                class="block text-sm font-medium text-neutral-700 mb-1"
                >{m.performance_form_rehearsal_time()}</label
              >
              <input
                id="perfRehearsalTime"
                type="time"
                bind:value={perfRehearsalTime}
                class="w-full border border-neutral-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-neutral-900"
              />
            </div>
          </div>
          <div class="flex justify-end gap-2">
            <Button
              variant="secondary"
              type="button"
              onclick={() => (showPerformanceForm = false)}>Cancelar</Button
            >
            <Button type="submit" disabled={perfSaving}
              >{perfSaving ? "..." : m.performance_add()}</Button
            >
          </div>
        </form>
      </div>
    {/if}

    {#if event.performances.length > 0}
      <div class="space-y-2">
        {#each event.performances as performance (performance.id)}
          <div
            class="border rounded p-3 bg-gray-50 flex items-start justify-between"
          >
            <div>
              <p class="font-medium">{performance.name}</p>
              <p class="text-sm text-gray-600">
                {m.eventdetail_performance_start()}: {performance.startTime}
                · {m.eventdetail_performance_duration({
                  minutes: performance.duration,
                })}
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
    <div class="flex items-center justify-between mb-3">
      <h2 class="text-lg font-medium">
        {m.eventdetail_reservations({ count: reservationLines.length })}
      </h2>
      <Button
        variant="secondary"
        onclick={() => {
          showReserveForm = !showReserveForm;
          if (showReserveForm && pendingLines.length === 0) addPendingLine();
        }}
      >
        <Plus class="w-4 h-4 mr-1" />
        {m.reserve_form_title()}
      </Button>
    </div>

    {#if showReserveForm}
      <div class="border rounded p-4 bg-gray-50 mb-4 space-y-3">
        {#each pendingLines as line, i (i)}
          <div class="flex items-end gap-3">
            <div class="flex-1">
              {#if i === 0}
                <label class="block text-sm font-medium text-neutral-700 mb-1"
                  >{m.reserve_form_equipment()}</label
                >
              {/if}
              <select
                value={line.equipmentId}
                onchange={(e) =>
                  updatePendingLine(
                    i,
                    "equipmentId",
                    (e.target as HTMLSelectElement).value,
                  )}
                class="w-full border border-neutral-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-neutral-900"
              >
                <option value="">{m.reserve_select_equipment()}</option>
                {#each catalogue as eq (eq.id)}
                  <option value={eq.id}
                    >{eq.name} ({categoryLabel[eq.category]()})</option
                  >
                {/each}
              </select>
            </div>
            <div class="w-24">
              {#if i === 0}
                <label class="block text-sm font-medium text-neutral-700 mb-1"
                  >{m.reserve_form_quantity()}</label
                >
              {/if}
              <input
                type="number"
                min="1"
                value={line.quantity}
                oninput={(e) =>
                  updatePendingLine(
                    i,
                    "quantity",
                    Number((e.target as HTMLInputElement).value),
                  )}
                class="w-full border border-neutral-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-neutral-900"
              />
            </div>
            <Button
              variant="icon"
              onclick={() => removePendingLine(i)}
              aria-label="Remove"
            >
              <Trash2 class="w-4 h-4" />
            </Button>
          </div>
        {/each}

        <div class="flex justify-between">
          <Button variant="secondary" onclick={addPendingLine}>
            <Plus class="w-4 h-4 mr-1" />
            {m.reserve_form_add_line()}
          </Button>
          <div class="flex gap-2">
            <Button
              variant="secondary"
              onclick={() => {
                showReserveForm = false;
                pendingLines = [];
              }}
            >
              {m.reserve_form_cancel()}
            </Button>
            <Button
              onclick={handleReserve}
              disabled={reserveSaving ||
                pendingLines.every((l) => !l.equipmentId)}
            >
              {reserveSaving ? "..." : m.reserve_form_submit()}
            </Button>
          </div>
        </div>
      </div>
    {/if}

    {#if reservationLines.length > 0}
      <DataTable
        rows={reservationLines}
        variant="gray"
        rowKey={(row: ReservationLine & { reservationId: string }) => row.id}
        columns={[
          {
            header: m.reservations_product(),
            className: "text-sm",
            cell: productCell,
          },
          {
            header: m.reservations_staff(),
            className: "text-sm",
            cell: authorCell,
          },
          {
            header: m.reservations_quantity(),
            className: "text-sm",
            cell: quantityCell,
          },
          {
            header: "",
            className: "text-sm w-10",
            cell: deleteCell,
          },
        ]}
      />
    {:else}
      <p class="text-gray-500">{m.eventdetail_no_reservations()}</p>
    {/if}
  </div>
</div>

{#snippet productCell(row: ReservationLine & { reservationId: string })}
  {row.equipment.name}
{/snippet}

{#snippet authorCell(row: ReservationLine & { reservationId: string })}
  {row.author.name}
{/snippet}

{#snippet quantityCell(row: ReservationLine & { reservationId: string })}
  {row.quantity}
{/snippet}

{#snippet deleteCell(row: ReservationLine & { reservationId: string })}
  {#if deleteLineId === row.id}
    <div class="flex items-center gap-1">
      <Button variant="delete" onclick={() => handleDeleteLine(row.id)}>
        {m.event_delete_confirm_title()}
      </Button>
      <Button variant="icon" onclick={() => (deleteLineId = null)}
        >&times;</Button
      >
    </div>
  {:else}
    <button
      onclick={() => (deleteLineId = row.id)}
      class="text-neutral-400 hover:text-red-600 p-1"
      title={m.performance_delete()}
    >
      <Trash2 class="w-4 h-4" />
    </button>
  {/if}
{/snippet}
