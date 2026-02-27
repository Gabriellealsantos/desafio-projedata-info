import { requestBackend } from "../utils/requests";

export function getSuggestion() {
  return requestBackend({ url: "/api/production/suggestion" });
}
