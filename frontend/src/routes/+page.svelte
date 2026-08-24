<script lang="ts">
  import { Calendar, Package, ClipboardList } from "@lucide/svelte";
  import { resolve } from "$app/paths";
  import StatCard from "$lib/components/dashboard/StatCard.svelte";
  import Card from "$lib/components/ui/Card.svelte";
  import { m } from "$lib/paraglide/messages";
  import { getLocale, localizeHref } from "$lib/paraglide/runtime";
  import { formatDateRange } from "$lib/utils/dates";
  import type { PageData } from "./$types";

  let { data }: { data: PageData } = $props();
  const locale = getLocale();
</script>

<div class="p-6 max-w-7xl">
  <h1 class="mb-6">{m.dashboard()}</h1>

  <div class="grid grid-cols-3 gap-4 mb-8">
    <StatCard
      label={m.stats_active_products()}
      value={data.stats.activeProducts}
      icon={Package}
    />
    <StatCard
      label={m.stats_upcoming_events()}
      value={data.stats.upcomingEventsCount}
      icon={Calendar}
    />
    <StatCard
      label={m.stats_reservation_lines()}
      value={data.stats.reservationLines}
      icon={ClipboardList}
    />
  </div>

  <Card title={m.dashboard_upcoming_events()}>
    <div class="divide-y divide-neutral-200">
      {#each data.upcomingEvents as event (event.id)}
        <a
          href={resolve(localizeHref(`/events/${event.id}`) as "/events/[id]", {
            id: event.id,
          })}
          class="block px-4 py-3 hover:bg-neutral-50 transition-colors"
        >
          <div class="flex items-start justify-between">
            <div>
              <div class="font-medium text-neutral-900">{event.name}</div>
              <div class="text-sm text-neutral-600 mt-0.5">
                {event.address}
              </div>
            </div>
            <div class="text-sm text-neutral-600">
              {formatDateRange(event.startDate, event.endDate, locale)}
            </div>
          </div>
        </a>
      {/each}
    </div>
  </Card>
</div>
