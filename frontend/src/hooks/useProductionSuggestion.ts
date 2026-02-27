import { useState, useEffect } from "react";
import * as productionSuggestionService from "../services/production-suggestion-service";
import type { ProductionSuggestionDTO } from "../models/production-suggestion";

type UseProductionSuggestionProps = {
  showToast: (message: string, type: "success" | "error") => void;
};

export function useProductionSuggestion({
  showToast,
}: UseProductionSuggestionProps) {
  const [suggestion, setSuggestion] = useState<ProductionSuggestionDTO | null>(
    null,
  );
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    productionSuggestionService
      .getSuggestion()
      .then((res) => setSuggestion(res.data))
      .catch((err) =>
        showToast(err.response?.data?.message ?? err.message, "error"),
      )
      .finally(() => setLoading(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return { suggestion, loading };
}
