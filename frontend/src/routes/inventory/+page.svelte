<script lang="ts">
  import DataTable from "$lib/components/ui/DataTable.svelte";
  import StatusBadge from "$lib/components/ui/StatusBadge.svelte";
  import { m } from "$lib/paraglide/messages";
  import type { Equipment, EquipmentStatus } from "$lib/types/domain";
  import { filterEquipmentByStatus } from "$lib/utils/selectors";
  import { equipmentStatusVariant } from "$lib/utils/status";
  import type { PageData } from "./$types";

  let { data }: { data: PageData } = $props();

  let filterStatus = $state<EquipmentStatus | "all">("all");
  const filtered = $derived(
    filterEquipmentByStatus(data.equipment, filterStatus),
  );

  const statusOptions = [
    { value: "all" as const, label: m.inventory_all() },
    { value: "available" as const, label: m.status_available() },
    { value: "reserved" as const, label: m.status_reserved() },
    { value: "inactive" as const, label: m.status_inactive() },
  ];
</script>

<div class="p-6">
  <div class="mb-6">
    <h1 class="text-2xl mb-4">{m.inventory()}</h1>
    <div class="flex gap-4">
      <label class="flex items-center gap-2">
        <span class="text-sm">{m.inventory_status()}</span>
        <select
          bind:value={filterStatus}
          class="border rounded px-3 py-1 text-sm"
        >
          {#each statusOptions as option (option.value)}
            <option value={option.value}>{option.label}</option>
          {/each}
        </select>
      </label>
    </div>
  </div>

  <DataTable
    rows={filtered}
    variant="gray"
    rowKey={(row: Equipment) => row.id}
    emptyMessage={m.inventory_no_products()}
    columns={[
      {
        header: m.inventory_id(),
        className: "text-sm text-gray-600",
        cell: idCell,
      },
      { header: m.inventory_name(), cell: nameCell },
      {
        header: m.inventory_category(),
        className: "text-sm text-gray-600",
        cell: categoryCell,
      },
      {
        header: m.inventory_serial(),
        className: "text-sm text-gray-600",
        cell: serialCell,
      },
      { header: m.inventory_status(), cell: statusCell },
    ]}
  />
</div>

{#snippet idCell(row: Equipment)}
  {row.id}
{/snippet}

{#snippet nameCell(row: Equipment)}
  {row.name}
{/snippet}

{#snippet categoryCell(row: Equipment)}
  {row.category}
{/snippet}

{#snippet serialCell(row: Equipment)}
  {row.serialNumber ?? "—"}
{/snippet}

{#snippet statusCell(row: Equipment)}
  {#if row.status === "available"}
    <StatusBadge
      label={m.status_available()}
      variant={equipmentStatusVariant(row.status)}
    />
  {:else if row.status === "reserved"}
    <StatusBadge
      label={m.status_reserved()}
      variant={equipmentStatusVariant(row.status)}
    />
  {:else}
    <StatusBadge
      label={m.status_inactive()}
      variant={equipmentStatusVariant(row.status)}
    />
  {/if}
{/snippet}
