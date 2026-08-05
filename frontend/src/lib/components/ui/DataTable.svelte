<script lang="ts" generics="T">
  import type { Snippet } from "svelte";

  type TableVariant = "neutral" | "gray";

  interface Column<T> {
    header: string;
    className?: string;
    cell: Snippet<[T]>;
  }

  interface Props {
    columns: Column<T>[];
    rows: T[];
    variant?: TableVariant;
    rowClass?: (row: T) => string;
    emptyMessage?: string;
    rowKey?: (row: T) => string;
  }

  let {
    columns,
    rows,
    variant = "neutral",
    rowClass,
    emptyMessage,
    rowKey,
  }: Props = $props();

  const wrapperClass: Record<TableVariant, string> = {
    neutral: "bg-white border border-neutral-200 rounded overflow-hidden",
    gray: "border rounded overflow-hidden",
  };

  const headClass: Record<TableVariant, string> = {
    neutral: "bg-neutral-50 border-b border-neutral-200",
    gray: "bg-gray-50",
  };

  const headerCellClass: Record<TableVariant, string> = {
    neutral: "px-4 py-3 text-left text-xs font-medium text-neutral-600",
    gray: "px-4 py-2 text-left text-sm font-medium text-gray-700",
  };

  const defaultRowClass: Record<TableVariant, string> = {
    neutral: "hover:bg-neutral-50 transition-colors",
    gray: "hover:bg-gray-50",
  };

  const bodyClass: Record<TableVariant, string> = {
    neutral: "divide-y divide-neutral-200",
    gray: "divide-y",
  };
</script>

<div class={wrapperClass[variant]}>
  <table class="w-full">
    <thead class={headClass[variant]}>
      <tr>
        {#each columns as column (column.header)}
          <th class={headerCellClass[variant]}>{column.header}</th>
        {/each}
      </tr>
    </thead>
    {#if rows.length > 0}
      <tbody class={bodyClass[variant]}>
        {#each rows as row (rowKey ? rowKey(row) : undefined)}
          <tr class={rowClass ? rowClass(row) : defaultRowClass[variant]}>
            {#each columns as column (column.header)}
              <td class={"px-4 py-3 " + (column.className ?? "")}
                >{@render column.cell(row)}</td
              >
            {/each}
          </tr>
        {/each}
      </tbody>
    {:else if emptyMessage}
      <tbody>
        <tr>
          <td colspan={columns.length} class="text-center text-gray-500 py-8">
            {emptyMessage}
          </td>
        </tr>
      </tbody>
    {/if}
  </table>
</div>
