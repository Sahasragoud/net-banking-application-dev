import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function FormalityCheckPage() {
  const [checks, setChecks] = useState<boolean[]>(
    new Array(5).fill(false)
  );

  const navigate = useNavigate();

  const handleChange = (index: number) => {
    const updated = [...checks];
    updated[index] = !updated[index];
    setChecks(updated);
  };

  const handleUnselectAll = () => {
    setChecks(new Array(5).fill(false));
  };

  const allChecked = checks.every(Boolean);
  const handleProceed = () => {
      navigate("/vbnmx");
  };

  return (
    <div className="min-h-screen flex justify-center items-center bg-gray-100">
      <div className="bg-white p-8 rounded-xl shadow w-full max-w-md">

        {/* Heading */}
        <h1 className="text-xl font-semibold mb-2">
          Formality Check
        </h1>

        {/* Subheading */}
        <p className="text-sm text-gray-600 mb-6">
          Please confirm the following before proceeding.
        </p>

        {/* Unselect All */}
        <button
          onClick={handleUnselectAll}
          className="text-blue-600 text-sm mb-4"
        >
          Unselect All
        </button>

        {/* 6 Checkboxes */}
        <div className="space-y-3 mb-6">
          {[
            "I am not a pollitically exposed person.",
            "I am an Indian citizen and a tax resident of India and of no other country.",
            "I hereby give my condent to issue virtual card eith my optimanet a/c.",
            "For investments, I agree and authorize OptimaNet bank to validate my KYC Registery Agency(KRA).",
            "I hereby authorize the bank to create a virtual Payment Address usable and tagged to mu OptimaNet Account an my behalf using my registered mobile number.",
          ].map((label, index) => (
            <label key={index} className="flex items-center gap-2">
              <input
                type="checkbox"
                checked={checks[index]}
                onChange={() => handleChange(index)}
              />
              <span className="text-sm">{label}</span>
            </label>
          ))}
        </div>

        {/* Proceed Button */}
        <button
          disabled={!allChecked}
          className={`w-full py-2 rounded text-white ${
            allChecked
              ? "bg-blue-600"
              : "bg-gray-400 cursor-not-allowed"
          }`}
          onClick={handleProceed}
        >
          Proceed
        </button>

      </div>
    </div>
  );
}