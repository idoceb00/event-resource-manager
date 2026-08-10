/**
 * Pure date formatting helpers. Locale is passed explicitly so these stay
 * deterministic and unit-testable; components receive it from Paraglide.
 */

function newLocalDate(value: string): Date {
  return new Date(value);
}

export function formatDate(value: string, locale: string): string {
  return newLocalDate(value).toLocaleDateString(locale, {
    year: "numeric",
    month: "short",
    day: "numeric",
  });
}

export function formatShortDate(value: string, locale: string): string {
  return newLocalDate(value).toLocaleDateString(locale, {
    month: "short",
    day: "numeric",
  });
}

export function formatDateRange(
  start: string,
  end: string,
  locale: string,
): string {
  if (start === end) return formatShortDate(start, locale);
  return `${formatShortDate(start, locale)} - ${formatShortDate(end, locale)}`;
}

export function formatDateRangeWithYear(
  start: string,
  end: string,
  locale: string,
): string {
  if (start === end) return formatDate(start, locale);
  return `${formatDate(start, locale)} - ${formatDate(end, locale)}`;
}

export function formatMonthYear(date: Date, locale: string): string {
  return date.toLocaleDateString(locale, {
    month: "long",
    year: "numeric",
  });
}
