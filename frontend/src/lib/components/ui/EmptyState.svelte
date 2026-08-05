<script lang="ts">
  interface Props {
    message: string;
    class?: string;
  }

  let { message, class: className = "text-center text-gray-500 py-8" }: Props =
    $props();
</script>

<p class={className}>{message}</p>
