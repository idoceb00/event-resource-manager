<script lang="ts">
  import { goto } from "$app/navigation";
  import { resolve } from "$app/paths";
  import Button from "$lib/components/ui/Button.svelte";
  import { eventService } from "$lib/services";
  import { m } from "$lib/paraglide/messages";
  import { localizeHref } from "$lib/paraglide/runtime";
  import { ApiError } from "$lib/api/client";
  import type { PageData } from "./$types";

  let { data }: { data: PageData } = $props();

  let name = $state(data.event.name);
  let startDate = $state(data.event.startDate.slice(0, 16));
  let endDate = $state(data.event.endDate.slice(0, 16));
  let address = $state(data.event.address);
  let transport = $state(data.event.transport);
  let extraInfo = $state(data.event.extraInfo ?? "");
  let error = $state("");
  let saving = $state(false);

  function toBackendDateTime(localValue: string): string {
    if (!localValue) return "";
    return localValue + ":00";
  }

  async function handleSubmit() {
    error = "";
    if (!name.trim()) {
      error = m.event_form_name_required();
      return;
    }
    if (!startDate || !endDate) {
      error = m.event_form_dates_required();
      return;
    }

    saving = true;
    try {
      await eventService.updateEvent(data.event.id, {
        name: name.trim(),
        startDate: toBackendDateTime(startDate),
        endDate: toBackendDateTime(endDate),
        address: address.trim() || undefined,
        transport,
        extraInfo: extraInfo.trim() || undefined,
      });
      goto(
        resolve(localizeHref(`/events/${data.event.id}`) as "/events/[id]", {
          id: data.event.id,
        }),
      );
    } catch (e) {
      if (e instanceof ApiError && e.status === 409) {
        error = m.event_form_date_conflict();
      } else if (e instanceof ApiError && e.status === 400) {
        error = m.event_form_date_invalid();
      } else {
        error = m.event_form_edit_error();
      }
    } finally {
      saving = false;
    }
  }
</script>

<div class="p-6 max-w-2xl">
  <div class="mb-6">
    <a
      href={resolve(
        localizeHref(`/events/${data.event.id}`) as "/events/[id]",
        { id: data.event.id },
      )}
      class="text-blue-600 hover:underline mb-2 inline-block"
    >
      ← {m.eventdetail_back()}
    </a>
    <h1>{m.event_form_edit_title()}</h1>
  </div>

  {#if error}
    <div
      class="bg-red-50 border border-red-200 rounded p-4 mb-4 text-red-700 text-sm"
    >
      {error}
    </div>
  {/if}

  <form
    onsubmit={(e) => {
      e.preventDefault();
      handleSubmit();
    }}
    class="space-y-4"
  >
    <div>
      <label for="name" class="block text-sm font-medium text-neutral-700 mb-1"
        >{m.event_form_name()}</label
      >
      <input
        id="name"
        type="text"
        bind:value={name}
        class="w-full border border-neutral-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-neutral-900"
      />
    </div>

    <div class="grid grid-cols-2 gap-4">
      <div>
        <label
          for="startDate"
          class="block text-sm font-medium text-neutral-700 mb-1"
          >{m.event_form_start_date()}</label
        >
        <input
          id="startDate"
          type="datetime-local"
          bind:value={startDate}
          class="w-full border border-neutral-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-neutral-900"
        />
      </div>
      <div>
        <label
          for="endDate"
          class="block text-sm font-medium text-neutral-700 mb-1"
          >{m.event_form_end_date()}</label
        >
        <input
          id="endDate"
          type="datetime-local"
          bind:value={endDate}
          class="w-full border border-neutral-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-neutral-900"
        />
      </div>
    </div>

    <div>
      <label
        for="address"
        class="block text-sm font-medium text-neutral-700 mb-1"
        >{m.event_form_address()}</label
      >
      <input
        id="address"
        type="text"
        bind:value={address}
        class="w-full border border-neutral-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-neutral-900"
      />
    </div>

    <div class="flex items-center gap-2">
      <input
        id="transport"
        type="checkbox"
        bind:checked={transport}
        class="rounded border-neutral-300"
      />
      <label for="transport" class="text-sm font-medium text-neutral-700"
        >{m.event_form_transport()}</label
      >
    </div>

    <div>
      <label
        for="extraInfo"
        class="block text-sm font-medium text-neutral-700 mb-1"
        >{m.event_form_extra_info()}</label
      >
      <textarea
        id="extraInfo"
        bind:value={extraInfo}
        rows="3"
        class="w-full border border-neutral-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-neutral-900"
      ></textarea>
    </div>

    <div class="flex justify-end gap-3 pt-2">
      <Button
        variant="secondary"
        type="button"
        onclick={() =>
          goto(
            resolve(
              localizeHref(`/events/${data.event.id}`) as "/events/[id]",
              { id: data.event.id },
            ),
          )}
      >
        Cancelar
      </Button>
      <Button type="submit" disabled={saving}>
        {saving ? "..." : m.event_form_edit_title()}
      </Button>
    </div>
  </form>
</div>
