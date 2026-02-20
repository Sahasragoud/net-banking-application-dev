import React, { useState } from "react";
import NomineePopup from "./NomineePopup";

const AddressDetailsPage: React.FC = () => {
  const [flatNo, setFlatNo] = useState("");
  const [line1, setLine1] = useState("");
  const [line2, setLine2] = useState("");
  const [landmark, setLandmark] = useState("");
  const [pincode, setPincode] = useState("");

  const isPincodeValid = /^\d{6}$/.test(pincode);
  const [showNomineePopup, setShowNomineePopup] = useState(false);

  const isFormValid =
    flatNo.trim() &&
    line1.trim() &&
    landmark.trim() &&
    isPincodeValid;

    const handleSubmit = (e: React.FormEvent) => {
      e.preventDefault();
      if (!isFormValid) return;

      setShowNomineePopup(true);
    };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 px-4">
      <div className="w-full max-w-lg bg-white rounded-2xl shadow-xl p-8">
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

          {!isPincodeValid && pincode.length > 0 && (
            <p className="text-xs text-red-500">
              Pincode must be 6 digits
            </p>
          )}

          <button
            type="submit"
            disabled={!isFormValid}
            className={`w-full py-2.5 rounded-lg font-medium transition ${
              isFormValid
                ? "bg-blue-600 text-white"
                : "bg-gray-300 text-gray-500 cursor-not-allowed"
            }`}
          >
            Proceed
          </button>
        </form>

        {showNomineePopup && (
          <NomineePopup
            onAddNominee={() => {
              setShowNomineePopup(false);
              console.log("Navigate to Add Nominee Page");
            }}
            onLater={() => {
              setShowNomineePopup(false);
              console.log("Continue without nominee");
            }}
          />
        )}
      </div>
    </div>
  );
};

export default AddressDetailsPage;