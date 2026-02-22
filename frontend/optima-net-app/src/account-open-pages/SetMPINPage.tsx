import { useState } from "react";
import AccountStepLayout from "./AccountStepLayout";

export default function SetMPINPage() {
  const [mpin, setMpin] = useState("");
  const [confirmMpin, setConfirmMpin] = useState("");

  const isValid =
    mpin.length === 6 &&
    confirmMpin.length === 6 &&
    mpin === confirmMpin;

  const handleClear = () => {
    setMpin("");
    setConfirmMpin("");
  };

  const handleConfirm = () => {
    if (!isValid) return;
    console.log("MPIN Set Successfully");
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
            disabled={!isValid}
            className={`px-4 py-2 rounded text-white ${
              isValid
                ? "bg-blue-600"
                : "bg-gray-400 cursor-not-allowed"
            }`}
          >
            Confirm MPIN
          </button>
        </div>

        {/* Caption */}
        <p className="text-xs text-center text-gray-500">
          Your MPIN will be required for secure transactions.
        </p>
    </AccountStepLayout>
  );
}