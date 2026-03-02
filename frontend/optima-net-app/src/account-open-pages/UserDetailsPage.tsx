import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import AccountStepLayout from "./AccountStepLayout";
import { api } from "../api/api-client";
import { useOnboarding } from "./OnboardingContext";

const UserDetailsPage: React.FC = () => {
  const [occupation, setOccupation] = useState("");
  const [incomeSource, setIncomeSource] = useState("");
  const [yearlyIncome, setYearlyIncome] = useState("");
  const [maritalStatus, setMaritalStatus] = useState("");
  const [fatherName, setFatherName] = useState("");
  const [motherName, setMotherName] = useState("");
  const [confirmMotherName, setConfirmMotherName] = useState("");

  const isFormValid =
    occupation &&
    incomeSource &&
    yearlyIncome &&
    maritalStatus &&
    fatherName.trim().length > 0 &&
    motherName.trim().length > 0 &&
    confirmMotherName.trim().length > 0 &&
    motherName === confirmMotherName;

  const navigate = useNavigate();
  const { customerId } = useOnboarding();
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFormValid || submitting) return;
    if (!customerId) {
      setError("Customer session missing. Restart onboarding.");
      navigate("/open-savings");
      return;
    }
    setSubmitting(true);
    setError("");
    try {
      await api.saveProfile(customerId, {
        occupation,
        incomeSource,
        yearlyIncome,
        maritalStatus,
        fatherName: fatherName.trim(),
        motherMaidenName: motherName.trim(),
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to save user profile.");
      setSubmitting(false);
      return;
    }

    navigate("/address-details");
  };

  return (
    <AccountStepLayout step={3} totalSteps={5}>
        <h2 className="text-2xl font-semibold text-center mb-6">
          User Details
        </h2>

        <form onSubmit={handleSubmit} className="space-y-4">

          {/* Occupation */}
          <select
            value={occupation}
            onChange={(e) => setOccupation(e.target.value)}
            className="w-full border rounded-lg px-3 py-2"
          >
            <option value="">Select Occupation</option>
            <option>Salaried</option>
            <option>Self Employed</option>
            <option>Professional</option>
            <option>Business</option>
            <option>Student</option>
            <option>Homemaker</option>
            <option>Retired</option>
            <option>Farmer</option>
          </select>

          {/* Income Source */}
          <select
            value={incomeSource}
            onChange={(e) => setIncomeSource(e.target.value)}
            className="w-full border rounded-lg px-3 py-2"
          >
            <option value="">Major Source of Income</option>
            <option>Family Income</option>
            <option>Investment Income</option>
          </select>

          {/* Yearly Income */}
          <select
            value={yearlyIncome}
            onChange={(e) => setYearlyIncome(e.target.value)}
            className="w-full border rounded-lg px-3 py-2"
          >
            <option value="">Select Yearly Income</option>
            <option>Less than 2Lakhs</option>
            <option>2Lakhs-5</option>
            <option>5Lakhs-10Lakhs</option>
            <option>10Lakhs-25Lakhs</option>
            <option>25Lakhs-50Lakhs</option>
            <option>50Lakhs-1Crore</option>
            <option> More than 1Crore</option>
          </select>

          {/* Marital Status */}
          <select
            value={maritalStatus}
            onChange={(e) => setMaritalStatus(e.target.value)}
            className="w-full border rounded-lg px-3 py-2"
          >
            <option value="">Marital Status</option>
            <option>Unmarried</option>
            <option>Married</option>
            <option>Divorced</option>
          </select>

          {/* Father's Name */}
          <input
            type="text"
            placeholder="Father's Name"
            value={fatherName}
            onChange={(e) => setFatherName(e.target.value)}
            className="w-full border rounded-lg px-3 py-2"
          />

          <hr className="my-4" />

          {/* Mother's Maiden Name */}
          <h2 className="text-xl font-semibold text-start mb-6">
            Enter Mother's maiden name          
          </h2>
          <p className="text-sm text-blue-600 font-small">
            This is for security reasons
          </p>

          <input
            type="text"
            placeholder="Enter Mother's Maiden Name"
            value={motherName}
            onChange={(e) => setMotherName(e.target.value)}
            className="w-full border rounded-lg px-3 py-2"
          />

          <input
            type="text"
            placeholder="Confirm Mother's Maiden Name"
            value={confirmMotherName}
            onChange={(e) => setConfirmMotherName(e.target.value)}
            className="w-full border rounded-lg px-3 py-2"
          />

          {motherName &&
            confirmMotherName &&
            motherName !== confirmMotherName && (
              <p className="text-xs text-red-500">
                Names do not match
              </p>
            )}

          <button
            type="submit"
            disabled={!isFormValid || submitting}
            className={`w-full py-2.5 rounded-lg font-medium transition ${
              isFormValid && !submitting
                ? "bg-blue-600 text-white"
                : "bg-gray-300 text-gray-500 cursor-not-allowed"
            }`}
          >
            {submitting ? "Saving..." : "Continue"}
          </button>
          {error && <p className="text-sm text-red-600">{error}</p>}

        </form>
      </AccountStepLayout>
  );
};

export default UserDetailsPage;
