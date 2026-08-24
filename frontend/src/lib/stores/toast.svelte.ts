let messageState = $state<string | null>(null);
let timeoutState = $state<ReturnType<typeof setTimeout> | null>(null);

function show(message: string, durationMs = 4000): void {
  if (timeoutState) clearTimeout(timeoutState);
  messageState = message;
  timeoutState = setTimeout(() => {
    messageState = null;
    timeoutState = null;
  }, durationMs);
}

function dismiss(): void {
  if (timeoutState) clearTimeout(timeoutState);
  messageState = null;
  timeoutState = null;
}

export const toast = {
  get message(): string | null {
    return messageState;
  },
  show,
  dismiss,
};
