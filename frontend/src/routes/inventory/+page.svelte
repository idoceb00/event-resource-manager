<script lang="ts">
  import Button from "$lib/components/ui/Button.svelte";
  import DataTable from "$lib/components/ui/DataTable.svelte";
  import StatusBadge from "$lib/components/ui/StatusBadge.svelte";
  import { m } from "$lib/paraglide/messages";
  import { equipmentService } from "$lib/services";
  import { session } from "$lib/stores/currentUser.svelte";
  import { toast } from "$lib/stores/toast.svelte";
  import type {
    Equipment,
    EquipmentCategory,
    EquipmentStatus,
  } from "$lib/types/domain";
  import { categoryLabels, statusLabels } from "$lib/utils/equipment";
  import { filterEquipmentByStatus } from "$lib/utils/selectors";
  import { equipmentStatusVariant } from "$lib/utils/status";
  import type { PageData } from "./$types";

  let { data }: { data: PageData } = $props();

  let equipment = $state<Equipment[]>(data.equipment);
  let filterStatus = $state<EquipmentStatus | "all">("all");
  const filtered = $derived(filterEquipmentByStatus(equipment, filterStatus));

  const isAdmin = $derived(session.user?.role === "admin");

  // --- Create form ---
  let showCreateForm = $state(false);
  let createName = $state("");
  let createCategory = $state<EquipmentCategory>("SOUND");
  let createStock = $state(0);
  let createLoading = $state(false);

  const categoryOptions: { value: EquipmentCategory; label: () => string }[] =
    [
      { value: "SOUND", label: m.category_sound },
      { value: "LIGHTING", label: m.category_lighting },
      { value: "MOTORS_AND_STRUCTURES", label: m.category_motors_and_structures },
      { value: "VIDEO", label: m.category_video },
    ];

  const statusFilterOptions: { value: EquipmentStatus | "all"; label: string }[] = [
    { value: "all", label: m.inventory_all() },
    { value: "CATALOGUED", label: m.status_catalogued() },
    { value: "DECATALOGUED", label: m.status_decatalogued() },
  ];

  async function handleCreate(e: Event) {
    e.preventDefault();
    createLoading = true;
    try {
      const created = await equipmentService.createEquipment({
        name: createName,
        category: createCategory,
        stock: createStock,
      });
      equipment = [...equipment, created];
      showCreateForm = false;
      createName = "";
      createCategory = "SOUND";
      createStock = 0;
      toast.show(m.inventory_create_success());
    } catch (err: unknown) {
      const status = (err as { status?: number }).status;
      if (status === 409) {
        toast.show(m.inventory_create_duplicate());
      } else if (status === 400) {
        toast.show(m.inventory_create_validation());
      } else {
        toast.show(m.inventory_create_validation());
      }
    } finally {
      createLoading = false;
    }
  }

  // --- Restock ---
  let restockId = $state<string | null>(null);
  let restockQty = $state(1);
  let restockLoading = $state(false);

  async function handleRestock(id: string) {
    if (restockQty <= 0) {
      toast.show(m.inventory_restock_zero());
      return;
    }
    restockLoading = true;
    try {
      const updated = await equipmentService.addStock(id, restockQty);
      equipment = equipment.map((eq) => (eq.id === id ? updated : eq));
      restockId = null;
      restockQty = 1;
      toast.show(m.inventory_restock_success());
    } catch (err: unknown) {
      const status = (err as { status?: number }).status;
      if (status === 404) {
        toast.show(m.inventory_not_found());
      } else if (status === 400) {
        toast.show(m.inventory_restock_zero());
      } else {
        toast.show(m.inventory_restock_error());
      }
    } finally {
      restockLoading = false;
    }
  }
</script>

<div class="p-6">
  <div class="mb-6">
    <div class="flex items-center justify-between mb-4">
      <h1 class="text-2xl">{m.inventory()}</h1>
      {#if isAdmin}
        <Button onclick={() => (showCreateForm = !showCreateForm)}>
          {m.inventory_new()}
        </Button>
      {/if}
    </div>

    {#if showCreateForm}
      <div class="bg-white border border-neutral-200 rounded p-4 mb-4">
        <h2 class="text-sm font-medium text-neutral-900 mb-3">
          {m.inventory_create_title()}
        </h2>
        <form onsubmit={handleCreate} class="flex items-end gap-3">
          <div class="flex-1">
            <label
              for="create-name"
              class="block text-sm text-neutral-700 mb-1"
            >
              {m.inventory_name()}
            </label>
            <input
              id="create-name"
              type="text"
              bind:value={createName}
              required
              class="w-full border border-neutral-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-neutral-900 focus:border-neutral-900"
            />
          </div>
          <div>
            <label
              for="create-category"
              class="block text-sm text-neutral-700 mb-1"
            >
              {m.inventory_category()}
            </label>
            <select
              id="create-category"
              bind:value={createCategory}
              class="border rounded px-3 py-1.5 text-sm"
            >
              {#each categoryOptions as option (option.value)}
                <option value={option.value}>{option.label()}</option>
              {/each}
            </select>
          </div>
          <div>
            <label
              for="create-stock"
              class="block text-sm text-neutral-700 mb-1"
            >
              {m.inventory_stock()}
            </label>
            <input
              id="create-stock"
              type="number"
              min="0"
              bind:value={createStock}
              required
              class="w-20 border border-neutral-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-neutral-900 focus:border-neutral-900"
            />
          </div>
          <Button type="submit" disabled={createLoading}>
            {m.inventory_create_title()}
          </Button>
        </form>
      </div>
    {/if}

    <div class="flex gap-4">
      <label class="flex items-center gap-2">
        <span class="text-sm">{m.inventory_status()}</span>
        <select
          bind:value={filterStatus}
          class="border rounded px-3 py-1 text-sm"
        >
          {#each statusFilterOptions as option (option.value)}
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
      { header: m.inventory_category(), cell: categoryCell },
      { header: m.inventory_stock(), cell: stockCell },
      { header: m.inventory_status(), cell: statusCell },
      ...(isAdmin
        ? [{ header: "", cell: actionsCell }]
        : []),
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
  {categoryLabels[row.category]()}
{/snippet}

{#snippet stockCell(row: Equipment)}
  {row.stock}
{/snippet}

{#snippet statusCell(row: Equipment)}
  <StatusBadge
    label={statusLabels[row.status]()}
    variant={equipmentStatusVariant(row.status)}
  />
{/snippet}

{#snippet actionsCell(row: Equipment)}
  {#if restockId === row.id}
    <div class="flex items-center gap-2">
      <input
        type="number"
        min="1"
        bind:value={restockQty}
        class="w-16 border border-neutral-300 rounded px-2 py-1 text-sm"
      />
      <Button
        variant="icon"
        onclick={() => handleRestock(row.id)}
        disabled={restockLoading}
      >
        OK
      </Button>
      <Button
        variant="icon"
        onclick={() => {
          restockId = null;
          restockQty = 1;
        }}
      >
        &times;
      </Button>
    </div>
  {:else}
    <button
      onclick={() => {
        restockId = row.id;
        restockQty = 1;
      }}
      class="text-sm text-neutral-500 hover:text-neutral-900 transition-colors"
    >
      {m.inventory_add_stock()}
    </button>
  {/if}
{/snippet}
