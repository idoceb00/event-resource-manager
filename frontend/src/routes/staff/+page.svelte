<script lang="ts">
  import DataTable from "$lib/components/ui/DataTable.svelte";
  import StatusBadge from "$lib/components/ui/StatusBadge.svelte";
  import { m } from "$lib/paraglide/messages";
  import type { User } from "$lib/types/domain";
  import type { PageData } from "./$types";

  let { data }: { data: PageData } = $props();
</script>

<div class="p-6">
  <h1 class="text-2xl mb-6">{m.staff()}</h1>

  <DataTable
    rows={data.users}
    variant="gray"
    columns={[
      {
        header: m.staff_id(),
        className: "text-sm text-gray-600",
        cell: idCell,
      },
      { header: m.staff_name(), cell: nameCell },
      {
        header: m.staff_role(),
        className: "text-sm text-gray-600",
        cell: roleCell,
      },
      { header: m.staff_status(), cell: statusCell },
    ]}
  />
</div>

{#snippet idCell(row: User)}
  {row.id}
{/snippet}

{#snippet nameCell(row: User)}
  {row.name}
{/snippet}

{#snippet roleCell(row: User)}
  {row.role}
{/snippet}

{#snippet statusCell(row: User)}
  {#if row.active}
    <StatusBadge label={m.status_active()} variant="green" />
  {:else}
    <StatusBadge label={m.status_inactive()} variant="gray" />
  {/if}
{/snippet}
