export function parseDate(dateValue) {
  if (!dateValue) return new Date();
  
  // Handle backend sending Jackson LocalDateTime arrays [year, month, day, hour, minute]
  if (Array.isArray(dateValue)) {
    const [year, month, day, hour = 0, minute = 0, second = 0] = dateValue;
    return new Date(year, month - 1, day, hour, minute, second);
  }
  
  // Fallback for standard ISO strings (from mock server or properly configured backend)
  return new Date(dateValue);
}
