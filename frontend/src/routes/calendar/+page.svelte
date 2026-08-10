<script lang="ts">
  import { AlertTriangle, ChevronLeft, ChevronRight } from "@lucide/svelte";
  import { resolve } from "$app/paths";
  import Button from "$lib/components/ui/Button.svelte";
  import { m } from "$lib/paraglide/messages";
  import { getLocale, localizeHref } from "$lib/paraglide/runtime";
  import {
    buildMonthGrid,
    getEventsForDay,
    initialCalendarMonth,
    isSameMonth,
  } from "$lib/utils/calendar";
  import { formatMonthYear } from "$lib/utils/dates";
  import type { PageData } from "./$types";

  let { data }: { data: PageData } = $props();
  const locale = getLocale();

  // svelte-ignore state_referenced_locally
  let currentDate = $state(initialCalendarMonth(data.events));
  const weeks = $derived(
    buildMonthGrid(currentDate.getFullYear(), currentDate.getMonth()),
  );

  const dayNames = Array.from({ length: 7 }, (_, i) =>
    new Date(2021, 0, 3 + i).toLocaleDateString(locale, { weekday: "short" }),
  );

  const previousMonth = () => {
    currentDate = new Date(
      currentDate.getFullYear(),
      currentDate.getMonth() - 1,
    );
  };

  const nextMonth = () => {
    currentDate = new Date(
      currentDate.getFullYear(),
      currentDate.getMonth() + 1,
    );
  };
</script>

<div class="p-6 max-w-7xl">
  <div class="flex items-center justify-between mb-6">
    <h1>{m.calendar()}</h1>
    <div class="flex items-center gap-3">
      <Button
        variant="icon"
        onclick={previousMonth}
        aria-label={m.calendar_previous_month()}
      >
        <ChevronLeft class="w-5 h-5" />
      </Button>
      <div class="text-sm font-medium min-w-[140px] text-center">
        {formatMonthYear(currentDate, locale)}
      </div>
      <Button
        variant="icon"
        onclick={nextMonth}
        aria-label={m.calendar_next_month()}
      >
        <ChevronRight class="w-5 h-5" />
      </Button>
    </div>
  </div>

  <div class="bg-white border border-neutral-200 rounded overflow-hidden">
    <div class="grid grid-cols-7 border-b border-neutral-200 bg-neutral-50">
      {#each dayNames as day (day)}
        <div class="px-3 py-2 text-xs font-medium text-neutral-600 text-center">
          {day}
        </div>
      {/each}
    </div>

    <div class="divide-y divide-neutral-200">
      {#each weeks as week, wi (wi)}
        <div class="grid grid-cols-7 divide-x divide-neutral-200">
          {#each week as day (day.getTime())}
            {@const inMonth = isSameMonth(day, currentDate)}
            {@const dayEvents = getEventsForDay(day, data.events)}
            <div class="min-h-[120px] p-2 {inMonth ? '' : 'bg-neutral-50'}">
              <div
                class="text-xs mb-2 {inMonth
                  ? 'text-neutral-600'
                  : 'text-neutral-400'}"
              >
                {day.getDate()}
              </div>
              <div class="space-y-1">
                {#each dayEvents as event (event.id)}
                  {@const conflict = data.conflicts[event.id] === true}
                  <a
                    href={resolve(
                      localizeHref(`/events/${event.id}`) as "/events/[id]",
                      { id: event.id },
                    )}
                    class="block px-2 py-1 rounded text-xs transition-colors {conflict
                      ? 'bg-amber-100 text-amber-900 hover:bg-amber-200'
                      : 'bg-neutral-900 text-white hover:bg-neutral-800'}"
                  >
                    <div class="flex items-center gap-1">
                      {#if conflict}
                        <AlertTriangle class="w-3 h-3 flex-shrink-0" />
                      {/if}
                      <span class="truncate">{event.name}</span>
                    </div>
                  </a>
                {/each}
              </div>
            </div>
          {/each}
        </div>
      {/each}
    </div>
  </div>

  <div class="mt-4 flex items-center gap-6 text-sm">
    <div class="flex items-center gap-2">
      <div class="w-4 h-4 rounded bg-neutral-900"></div>
      <span class="text-neutral-600">{m.calendar_legend_normal()}</span>
    </div>
    <div class="flex items-center gap-2">
      <div class="w-4 h-4 rounded bg-amber-100 border border-amber-300"></div>
      <span class="text-neutral-600">{m.calendar_legend_conflict()}</span>
    </div>
  </div>
</div>
