import { useState } from "react";
import AccountStepLayout from "./AccountStepLayout";
import { useNavigate } from "react-router-dom";
import { useOnboarding } from "./OnboardingContext";
import { api } from "../api/api-client";

export default function SetMPINPage() {
  const [mpin, setMpin] = useState("");
  const [confirmMpin, setConfirmMpin] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  const navigate = useNavigate();
  const { customerId, setMpinSet } = useOnboarding();


  const isValid =
    mpin.length === 6 &&
    confirmMpin.length === 6 &&
    mpin === confirmMpin;

  const handleClear = () => {
    setMpin("");
    setConfirmMpin("");
  };

  const handleConfirm = async () => {
    if (!isValid || submitting) return;
    if (!customerId) {
      setError("Customer session missing. Restart onboarding.");
      return;
    }
    setSubmitting(true);
    setError("");
    try {
      await api.setMpin(customerId, { mpin });
      setMpinSet(true);
      navigate("/virtual-card");
    } catch (err) {
      const message = err instanceof Error ? err.message : "Unable to set MPIN.";
      setError(message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleChange =
    (setter: React.Dispatch<React.SetStateAction<string>>) =>
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const value = e.target.value.replace(/\D/g, ""); // only digits
      if (value.length <= 6) setter(value);
    };

  return (
    <AccountStepLayout step={4} totalSteps={13}>
        {/* Heading */}
        <h1 className="text-xl font-semibold mb-2">
          Set MPIN
        </h1>

        <p className="text-sm text-gray-600 mb-6">
          Create a secure 6-digit MPIN for your account.
        </p>

        {/* MPIN Input */}
        <div className="space-y-4 mb-6">
          <input
            type="password"
            value={mpin}
            onChange={handleChange(setMpin)}
            placeholder="Enter 6-digit MPIN"
            className="w-full border rounded px-3 py-2"
          />

          <input
            type="password"
            value={confirmMpin}
            onChange={handleChange(setConfirmMpin)}
            placeholder="Re-enter MPIN"
            className="w-full border rounded px-3 py-2"
          />
        </div>

        {/* Buttons */}
        <div className="flex justify-center gap-4 mb-6">
          <button
            onClick={handleClear}
            className="px-4 py-2 rounded bg-gray-300"
          >
            Clear MPIN
          </button>

          <button
            onClick={handleConfirm}
            disabled={!isValid || submitting}
            className={`px-4 py-2 rounded text-white ${
              isValid && !submitting
                ? "bg-blue-600"
                : "bg-gray-400 cursor-not-allowed"
            }`}
          >
            {submitting ? "Saving..." : "Confirm MPIN"}
          </button>
        </div>
        {error && <p className="mb-4 text-sm text-red-600">{error}</p>}

        {/* Caption */}
        <p className="text-xs text-center text-gray-500">
          Your MPIN will be required for secure transactions.
        </p>
    </AccountStepLayout>
  );
}
