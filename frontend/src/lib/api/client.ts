import { PUBLIC_API_BASE_URL } from "$env/static/public";

export class ApiError extends Error {
  constructor(
    public readonly status: number,
    message: string,
  ) {
    super(message);
    this.name = "ApiError";
  }
}

let onUnauthorized: (() => void) | null = null;

export function setOnUnauthorized(handler: () => void): void {
  onUnauthorized = handler;
}

async function request<T>(
  path: string,
  method: string,
  options: { body?: unknown; headers?: Record<string, string> } = {},
): Promise<T> {
  const { body, headers: customHeaders } = options;

  const headers: Record<string, string> = { ...customHeaders };

  let bodyInit: string | undefined;
  if (body !== undefined) {
    headers["Content-Type"] = "application/json";
    bodyInit = JSON.stringify(body);
  }

  const response = await fetch(`${PUBLIC_API_BASE_URL}${path}`, {
    method,
    credentials: "include",
    headers,
    body: bodyInit,
  });

  if (!response.ok) {
    if (response.status === 401 && onUnauthorized) {
      onUnauthorized();
    }
    throw new ApiError(response.status, `Request failed: ${response.status}`);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

export const apiClient = {
  get: <T>(path: string) => request<T>(path, "GET"),

  post: <T>(path: string, body?: unknown) => request<T>(path, "POST", { body }),

  patch: <T>(path: string, body?: unknown) =>
    request<T>(path, "PATCH", { body }),

  delete: <T>(path: string) => request<T>(path, "DELETE"),
};
