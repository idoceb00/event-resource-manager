<script lang="ts">
  import Button from "$lib/components/ui/Button.svelte";
  import DataTable from "$lib/components/ui/DataTable.svelte";
  import { m } from "$lib/paraglide/messages";
  import { equipmentService } from "$lib/services";
  import { session } from "$lib/stores/currentUser.svelte";
  import { toast } from "$lib/stores/toast.svelte";
  import type {
    Equipment,
    EquipmentCategory,
  } from "$lib/types/domain";
  import { categoryLabels } from "$lib/utils/equipment";
  import type { PageData } from "./$types";

  let { data }: { data: PageData } = $props();

  let equipment = $state<Equipment[]>(data.equipment);
  let allEquipment = $state<Equipment[]>(data.allEquipment);

  const isAdmin = $derived(session.user?.role === "admin");

  // --- Filters ---
  let filterCategory = $state<EquipmentCategory | "all">("all");
  let showDecatalogued = $state(false);

  const categoryFilterOptions: { value: EquipmentCategory | "all"; label: string }[] = [
    { value: "all", label: m.inventory_all() },
    { value: "SOUND", label: m.category_sound() },
    { value: "LIGHTING", label: m.category_lighting() },
    { value: "MOTORS_AND_STRUCTURES", label: m.category_motors_and_structures() },
    { value: "VIDEO", label: m.category_video() },
  ];

  const displayRows = $derived.by(() => {
    let rows = showDecatalogued
      ? allEquipment.filter((eq) => eq.status === "DECATALOGUED")
      : equipment;
    if (filterCategory !== "all") {
      rows = rows.filter((eq) => eq.category === filterCategory);
    }
    return rows;
  });

  // --- Create form ---
  let showCreateForm = $state(false);
  let createName = $state("");
  let createCategory = $state<EquipmentCategory>("SOUND");
  let createStock = $state(0);
  let createLoading = $state(false);
  let smartCreateMatch = $state<Equipment | null>(null);

  const categoryOptions: { value: EquipmentCategory; label: () => string }[] =
    [
      { value: "SOUND", label: m.category_sound },
      { value: "LIGHTING", label: m.category_lighting },
      { value: "MOTORS_AND_STRUCTURES", label: m.category_motors_and_structures },
      { value: "VIDEO", label: m.category_video },
    ];

  function checkSmartCreate() {
    if (!createName.trim()) {
      smartCreateMatch = null;
      return;
    }
    const match = allEquipment.find(
      (eq) => eq.name.toLowerCase() === createName.trim().toLowerCase(),
    );
    smartCreateMatch = match?.status === "DECATALOGUED" ? match : null;
  }

  async function handleSmartCreateRecatalogue() {
    if (!smartCreateMatch) return;
    createLoading = true;
    try {
      const updated = await equipmentService.recatalogue(smartCreateMatch.id);
      allEquipment = allEquipment.map((eq) => (eq.id === updated.id ? updated : eq));
      equipment = [...equipment, updated];
      smartCreateMatch = null;
      showCreateForm = false;
      createName = "";
      createCategory = "SOUND";
      createStock = 0;
      toast.show(m.inventory_recatalogue_success());
    } catch (err: unknown) {
      const status = (err as { status?: number }).status;
      if (status === 409) {
        toast.show(m.inventory_recatalogue_already());
      } else if (status === 403) {
        toast.show(m.inventory_recatalogue_forbidden());
      } else {
        toast.show(m.inventory_recatalogue_already());
      }
    } finally {
      createLoading = false;
    }
  }

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
      allEquipment = [...allEquipment, created];
      showCreateForm = false;
      createName = "";
      createCategory = "SOUND";
      createStock = 0;
      smartCreateMatch = null;
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

  // --- Stock adjustment ---
  let adjustId = $state<string | null>(null);
  let adjustQty = $state(1);
  let adjustSign = $state<1 | -1>(1);
  let adjustLoading = $state(false);

  async function handleAdjustStock(id: string) {
    if (adjustQty <= 0) {
      toast.show(m.inventory_adjust_stock_zero());
      return;
    }
    const delta = adjustSign * adjustQty;
    adjustLoading = true;
    try {
      const updated = await equipmentService.adjustStock(id, delta);
      equipment = equipment.map((eq) => (eq.id === id ? updated : eq));
      allEquipment = allEquipment.map((eq) => (eq.id === id ? updated : eq));
      adjustId = null;
      adjustQty = 1;
      adjustSign = 1;
      toast.show(m.inventory_restock_success());
    } catch (err: unknown) {
      const status = (err as { status?: number }).status;
      if (status === 409) {
        toast.show(m.inventory_adjust_stock_conflict());
      } else if (status === 400) {
        toast.show(m.inventory_adjust_stock_negative());
      } else if (status === 403) {
        toast.show(m.inventory_adjust_stock_forbidden());
      } else if (status === 404) {
        toast.show(m.inventory_not_found());
      } else {
        toast.show(m.inventory_restock_error());
      }
    } finally {
      adjustLoading = false;
    }
  }

  // --- Decatalogue ---
  let decatalogueId = $state<string | null>(null);
  let decatalogueLoading = $state(false);

  async function handleDecatalogue(id: string) {
    decatalogueLoading = true;
    try {
      const updated = await equipmentService.decatalogue(id);
      equipment = equipment.filter((eq) => eq.id !== id);
      allEquipment = allEquipment.map((eq) => (eq.id === id ? updated : eq));
      decatalogueId = null;
      toast.show(m.inventory_decatalogue_success());
    } catch (err: unknown) {
      const status = (err as { status?: number }).status;
      if (status === 409) {
        toast.show(m.inventory_decatalogue_conflict());
      } else if (status === 403) {
        toast.show(m.inventory_decatalogue_forbidden());
      } else {
        toast.show(m.inventory_decatalogue_conflict());
      }
    } finally {
      decatalogueLoading = false;
    }
  }

  // --- Recatalogue ---
  let recatalogueLoading = $state(false);
  let recatalogueId = $state<string | null>(null);
  let recatalogueStock = $state(0);

  async function handleRecatalogueConfirm() {
    if (!recatalogueId) return;
    recatalogueLoading = true;
    try {
      const updated = await equipmentService.recatalogue(recatalogueId);
      equipment = [...equipment, updated];
      allEquipment = allEquipment.map((eq) => (eq.id === recatalogueId ? updated : eq));

      if (recatalogueStock > 0) {
        const restocked = await equipmentService.adjustStock(recatalogueId, recatalogueStock);
        equipment = equipment.map((eq) => (eq.id === recatalogueId ? restocked : eq));
        allEquipment = allEquipment.map((eq) => (eq.id === recatalogueId ? restocked : eq));
      }

      recatalogueId = null;
      recatalogueStock = 0;
      toast.show(m.inventory_recatalogue_success());
    } catch (err: unknown) {
      const status = (err as { status?: number }).status;
      if (status === 409) {
        toast.show(m.inventory_recatalogue_already());
      } else if (status === 403) {
        toast.show(m.inventory_recatalogue_forbidden());
      } else {
        toast.show(m.inventory_recatalogue_already());
      }
    } finally {
      recatalogueLoading = false;
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

        {#if smartCreateMatch}
          <div class="bg-amber-50 border border-amber-200 rounded p-3 mb-3">
            <p class="text-sm text-amber-800 mb-2">
              {m.inventory_smart_create_decatalogued()}
            </p>
            <div class="flex gap-2">
              <Button onclick={handleSmartCreateRecatalogue} disabled={createLoading}>
                {m.inventory_smart_create_recatalogue()}
              </Button>
              <Button
                variant="icon"
                onclick={() => { smartCreateMatch = null; }}
              >
                &times;
              </Button>
            </div>
          </div>
        {/if}

        <form onsubmit={handleCreate} class="flex items-end gap-3">
          <div class="flex-1">
            <label for="create-name" class="block text-sm text-neutral-700 mb-1">
              {m.inventory_name()}
            </label>
            <input
              id="create-name"
              type="text"
              bind:value={createName}
              oninput={checkSmartCreate}
              required
              class="w-full border border-neutral-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-neutral-900 focus:border-neutral-900"
            />
          </div>
          <div>
            <label for="create-category" class="block text-sm text-neutral-700 mb-1">
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
            <label for="create-stock" class="block text-sm text-neutral-700 mb-1">
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
          <Button type="submit" disabled={createLoading || !!smartCreateMatch}>
            {m.inventory_create_title()}
          </Button>
        </form>
      </div>
    {/if}

    <div class="flex items-center gap-4">
      <label class="flex items-center gap-2">
        <span class="text-sm">{m.inventory_category()}</span>
        <select bind:value={filterCategory} class="border rounded px-3 py-1 text-sm">
          {#each categoryFilterOptions as option (option.value)}
            <option value={option.value}>{option.label}</option>
          {/each}
        </select>
      </label>

      {#if isAdmin}
        <label class="flex items-center gap-2 cursor-pointer">
          <input
            type="checkbox"
            bind:checked={showDecatalogued}
            class="rounded border-neutral-300"
          />
          <span class="text-sm">{m.inventory_show_decatalogued()}</span>
        </label>
      {/if}
    </div>
  </div>

  <DataTable
    rows={displayRows}
    variant="gray"
    rowKey={(row: Equipment) => row.id}
    emptyMessage={m.inventory_no_products()}
    columns={[
      { header: m.inventory_name(), cell: nameCell },
      { header: m.inventory_category(), cell: categoryCell },
      { header: m.inventory_stock(), cell: stockCell },
      ...(isAdmin
        ? [{ header: "", cell: actionsCell }]
        : []),
    ]}
  />
</div>

{#snippet nameCell(row: Equipment)}
  {row.name}
{/snippet}

{#snippet categoryCell(row: Equipment)}
  {categoryLabels[row.category]()}
{/snippet}

{#snippet stockCell(row: Equipment)}
  {row.stock}
{/snippet}

{#snippet actionsCell(row: Equipment)}
  <div class="flex items-center gap-2 flex-wrap justify-end">
    {#if adjustId === row.id}
      <div class="flex items-center gap-1">
        <button
          onclick={() => { adjustSign = 1; }}
          class="px-2 py-1 text-sm rounded {adjustSign === 1 ? 'bg-green-100 text-green-800 font-medium' : 'bg-neutral-100 text-neutral-600'}"
        >
          {m.inventory_adjust_stock_add()}
        </button>
        <button
          onclick={() => { adjustSign = -1; }}
          class="px-2 py-1 text-sm rounded {adjustSign === -1 ? 'bg-red-100 text-red-800 font-medium' : 'bg-neutral-100 text-neutral-600'}"
        >
          {m.inventory_adjust_stock_remove()}
        </button>
        <input
          type="number"
          min="1"
          bind:value={adjustQty}
          class="w-16 border border-neutral-300 rounded px-2 py-1 text-sm"
        />
        <Button
          variant="icon"
          onclick={() => handleAdjustStock(row.id)}
          disabled={adjustLoading}
        >
          OK
        </Button>
        <Button
          variant="icon"
          onclick={() => { adjustId = null; adjustQty = 1; adjustSign = 1; }}
        >
          &times;
        </Button>
      </div>
    {:else if decatalogueId === row.id}
      <div class="flex items-center gap-2">
        <span class="text-xs text-neutral-600">{m.inventory_decatalogue_confirm()}</span>
        <Button
          onclick={() => handleDecatalogue(row.id)}
          disabled={decatalogueLoading}
        >
          {m.inventory_decatalogue_confirm_accept()}
        </Button>
        <Button
          variant="icon"
          onclick={() => { decatalogueId = null; }}
        >
          &times;
        </Button>
      </div>
    {:else if row.status === "CATALOGUED"}
      <button
        onclick={() => { adjustId = row.id; adjustQty = 1; adjustSign = 1; }}
        class="text-sm text-neutral-500 hover:text-neutral-900 transition-colors"
      >
        {m.inventory_adjust_stock()}
      </button>
      <button
        onclick={() => { decatalogueId = row.id; }}
        class="text-sm text-neutral-500 hover:text-red-700 transition-colors"
      >
        {m.inventory_decatalogue()}
      </button>
    {:else}
      <button
        onclick={() => { recatalogueId = row.id; recatalogueStock = 0; }}
        disabled={recatalogueLoading}
        class="text-sm text-neutral-500 hover:text-green-700 transition-colors"
      >
        {m.inventory_recatalogue()}
      </button>
    {/if}
  </div>
{/snippet}

{#if recatalogueId}
  <div class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg shadow-lg p-6 w-full max-w-sm">
      <h3 class="text-lg font-medium text-neutral-900 mb-2">
        {m.inventory_recatalogue()}
      </h3>
      <p class="text-sm text-neutral-600 mb-4">
        {m.inventory_recatalogue_stock_prompt()}
      </p>
      <label for="recatalogue-stock" class="block text-sm text-neutral-700 mb-1">
        {m.inventory_stock()}
      </label>
      <input
        id="recatalogue-stock"
        type="number"
        min="0"
        bind:value={recatalogueStock}
        class="w-full border border-neutral-300 rounded px-3 py-1.5 text-sm mb-4 focus:outline-none focus:ring-1 focus:ring-neutral-900 focus:border-neutral-900"
      />
      <div class="flex gap-2 justify-end">
        <Button
          variant="icon"
          onclick={() => { recatalogueId = null; recatalogueStock = 0; }}
        >
          &times;
        </Button>
        <Button onclick={handleRecatalogueConfirm} disabled={recatalogueLoading}>
          {m.inventory_smart_create_recatalogue()}
        </Button>
      </div>
    </div>
  </div>
{/if}
