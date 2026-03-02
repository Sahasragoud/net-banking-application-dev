import React, { useState } from "react";
import NomineePopup from "./NomineePopup";
import { useNavigate } from "react-router-dom";
import AccountStepLayout from "./AccountStepLayout";
import { api } from "../api/api-client";
import { useOnboarding } from "./OnboardingContext";

const AddressDetailsPage: React.FC = () => {
  const navigate = useNavigate();
  const { customerId, kycDraft, updateKycDraft } = useOnboarding();

  const [flatNo, setFlatNo] = useState("");
  const [line1, setLine1] = useState("");
  const [line2, setLine2] = useState("");
  const [landmark, setLandmark] = useState("");
  const [city, setCity] = useState(kycDraft.city);
  const [stateValue, setStateValue] = useState(kycDraft.state);
  const [pincode, setPincode] = useState(kycDraft.postalCode);
  const [country, setCountry] = useState(kycDraft.country || "India");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  const isPincodeValid = /^\d{6}$/.test(pincode);
  const [showNomineePopup, setShowNomineePopup] = useState(false);

  const isFormValid = Boolean(
    flatNo.trim() &&
    line1.trim() &&
    city.trim() &&
    stateValue.trim() &&
    landmark.trim() &&
    country.trim() &&
    isPincodeValid
  );

    const handleSubmit = async (e: React.FormEvent) => {
      e.preventDefault();
      if (!isFormValid || submitting) return;
      if (!customerId) {
        setError("Customer details not found. Start onboarding again.");
        navigate("/open-savings");
        return;
      }
      if (!kycDraft.panNumber || (!kycDraft.aadhaarNumber && !kycDraft.voterIdNumber)) {
        setError("PAN and one identity document (Aadhaar or Voter ID) must be verified first.");
        navigate("/welcome");
        return;
      }

      setSubmitting(true);
      setError("");

      const addressLine = [flatNo.trim(), line1.trim(), line2.trim(), landmark.trim()]
        .filter(Boolean)
        .join(", ");

      try {
        await api.submitKyc(customerId, {
          aadhaarNumber: kycDraft.idType === "aadhaar" ? kycDraft.aadhaarNumber : undefined,
          voterIdNumber: kycDraft.idType === "voter" ? kycDraft.voterIdNumber : undefined,
          panNumber: kycDraft.panNumber,
          addressLine,
          city: city.trim(),
          state: stateValue.trim(),
          postalCode: pincode,
          country: country.trim(),
        });
        updateKycDraft({
          addressLine,
          city: city.trim(),
          state: stateValue.trim(),
          postalCode: pincode,
          country: country.trim(),
        });
        setShowNomineePopup(true);
      } catch (err) {
        const message = err instanceof Error ? err.message : "Unable to submit KYC.";
        setError(message);
      } finally {
        setSubmitting(false);
      }
    };

  const handleAddNominee = () => {
    setShowNomineePopup(false);
    navigate("/nomination"); // 👈 navigate to your page
  };

  const handleLater = () => {
    setShowNomineePopup(false);
    navigate("/formalities");
  };



  return (
    <AccountStepLayout step={5} totalSteps={7}>
        <h2 className="text-2xl font-semibold text-center mb-2">
          Address Details
        </h2>
        <p className="font-small text-center mb-6">
          Used for future communication with bank
        </p>
        <form onSubmit={handleSubmit} className="space-y-4">

          <input
            type="text"
            placeholder="Flat / House No"
            value={flatNo}
            onChange={(e) => setFlatNo(e.target.value)}
            className="w-full border rounded-lg px-3 py-2"
          />

          <input
            type="text"
            placeholder="Address Line 1"
            value={line1}
            onChange={(e) => setLine1(e.target.value)}
            className="w-full border rounded-lg px-3 py-2"
          />

          <input
            type="text"
            placeholder="Address Line 2 (Optional)"
            value={line2}
            onChange={(e) => setLine2(e.target.value)}
            className="w-full border rounded-lg px-3 py-2"
          />

          <input
            type="text"
            placeholder="Landmark"
            value={landmark}
            onChange={(e) => setLandmark(e.target.value)}
            className="w-full border rounded-lg px-3 py-2"
          />

          <input
            type="text"
            placeholder="City"
            value={city}
            onChange={(e) => setCity(e.target.value)}
            className="w-full border rounded-lg px-3 py-2"
          />

          <input
            type="text"
            placeholder="State"
            value={stateValue}
            onChange={(e) => setStateValue(e.target.value)}
            className="w-full border rounded-lg px-3 py-2"
          />

          <input
            type="text"
            maxLength={6}
            placeholder="Pincode"
            value={pincode}
            onChange={(e) => {
              if (/^\d*$/.test(e.target.value)) {
                setPincode(e.target.value);
              }
            }}
            className="w-full border rounded-lg px-3 py-2"
          />

          <input
            type="text"
            placeholder="Country"
            value={country}
            onChange={(e) => setCountry(e.target.value)}
            className="w-full border rounded-lg px-3 py-2"
          />

          {!isPincodeValid && pincode.length > 0 && (
            <p className="text-xs text-red-500">
              Pincode must be 6 digits
            </p>
          )}
          {error && <p className="text-sm text-red-600">{error}</p>}

          <button
            type="submit"
            disabled={!isFormValid || submitting}
            className={`w-full py-2.5 rounded-lg font-medium transition ${
              isFormValid && !submitting
                ? "bg-blue-600 text-white"
                : "bg-gray-300 text-gray-500 cursor-not-allowed"
            }`}
          >
            {submitting ? "Submitting KYC..." : "Proceed"}
          </button>
        </form>

        {showNomineePopup && (
          <NomineePopup
            onAddNominee={handleAddNominee}
            onLater={handleLater}
          />
        )}
    </AccountStepLayout>
  );
};

export default AddressDetailsPage;
