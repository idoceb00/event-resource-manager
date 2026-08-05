<script lang="ts">
  import DataTable from "$lib/components/ui/DataTable.svelte";
  import StatusBadge from "$lib/components/ui/StatusBadge.svelte";
  import { m } from "$lib/paraglide/messages";
  import type { Employee } from "$lib/types/domain";
  import { employeeStatusVariant } from "$lib/utils/status";
  import type { PageData } from "./$types";

  let { data }: { data: PageData } = $props();
</script>

<div class="p-6">
  <h1 class="text-2xl mb-6">{m.staff()}</h1>

  <DataTable
    rows={data.staff}
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
      {
        header: m.staff_email(),
        className: "text-sm text-gray-600",
        cell: emailCell,
      },
      {
        header: m.staff_phone(),
        className: "text-sm text-gray-600",
        cell: phoneCell,
      },
    ]}
  />
</div>

{#snippet idCell(row: Employee)}
  {row.id}
{/snippet}

{#snippet nameCell(row: Employee)}
  {row.name}
{/snippet}

{#snippet roleCell(row: Employee)}
  {row.role}
{/snippet}

{#snippet statusCell(row: Employee)}
  {#if row.status === "active"}
    <StatusBadge
      label={m.status_active()}
      variant={employeeStatusVariant(row.status)}
    />
  {:else}
    <StatusBadge
      label={m.status_inactive()}
      variant={employeeStatusVariant(row.status)}
    />
  {/if}
{/snippet}

{#snippet emailCell(row: Employee)}
  {row.email}
{/snippet}

{#snippet phoneCell(row: Employee)}
  {row.phone}
{/snippet}
