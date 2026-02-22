import React, { useState } from "react";
import PanVerifyPopUp from "./PanVerifyPopUp";
import AadharVerifyPopUp from "./AadharVerifyPopUp";
import AccountStepLayout from "./AccountStepLayout";

type IdType = "aadhaar" | "voter";

const WelcomePage: React.FC = () => {
  const [pan, setPan] = useState("");
  const [idType, setIdType] = useState<IdType>("aadhaar");
  const [idValue, setIdValue] = useState("");
  const [consent, setConsent] = useState(false);
  const [terms, setTerms] = useState(false);

  const [showVerify, setShowVerify] = useState(false);
  const [showAadhar, setShowAadhar] = useState(false);

  const userName = "Rohan Sharma"; // mock fetched name

  const isPanValid = /^[A-Z]{5}[0-9]{4}[A-Z]{1}$/.test(pan);
  const isAadhaarValid = /^\d{12}$/.test(idValue);
  const isVoterValid = /^[A-Z]{3}[0-9]{7}$/.test(idValue);

  const isIdValid =
    idType === "aadhaar" ? isAadhaarValid : isVoterValid;

  const isFormValid = isPanValid && isIdValid && consent && terms;


    const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFormValid) return;
    setShowVerify(true);
    };

  const handleInfoClick = (field: string) => {
    alert(`Information about ${field}`);
  };

  return (

    <AccountStepLayout step={2} totalSteps={5}>
        <div className="text-center mb-6">
          <h1 className="text-2xl font-semibold text-gray-800 flex items-center justify-center gap-2">
            Welcome 👏
          </h1>

        <p className="text-sm text-gray-500 text-center mb-6">
          Please provide your details
        </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-5">

          {/* PAN Input */}
          <div>
            <label className="block text-sm font-medium text-gray-600 mb-1">
              PAN Card Number
            </label>

            <div className="relative">
              <input
                type="text"
                value={pan}
                onChange={(e) =>
                  setPan(e.target.value.toUpperCase())
                }
                placeholder="PAN "
                className="w-full px-3 py-2 pr-10 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
              />

              {/* Info Icon */}
              <button
                type="button"
                onClick={() => handleInfoClick("PAN")}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-blue-600"
              >
                ℹ
              </button>
            </div>

            {pan.length < 0 && !isPanValid && (
              <p className="text-xs text-red-500 mt-1">
                Invalid PAN format
              </p>
            )}
          </div>

          {/* ID Type */}
          <div>
            <label className="block text-sm font-medium text-gray-600 mb-2">
              Select ID Type
            </label>

            <div className="flex gap-4">
              <label className="flex items-center gap-2 text-sm">
                <input
                  type="radio"
                  checked={idType === "aadhaar"}
                  onChange={() => {
                    setIdType("aadhaar");
                    setIdValue("");
                  }}
                />
                Aadhaar
              </label>

              <label className="flex items-center gap-2 text-sm">
                <input
                  type="radio"
                  checked={idType === "voter"}
                  onChange={() => {
                    setIdType("voter");
                    setIdValue("");
                  }}
                />
                Voter ID
              </label>
            </div>
          </div>

          {/* ID Input */}
          <div className="relative">
            <input
              type="text"
              value={idValue}
              onChange={(e) =>
                setIdValue(e.target.value.toUpperCase())
              }
              placeholder={
                idType === "aadhaar"
                  ? "Enter 12-digit Aadhaar Number"
                  : "Enter Voter ID (ABC1234567)"
              }
              className="w-full px-3 py-2 pr-10 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
            />

            {/* Info Icon */}
            <button
              type="button"
              onClick={() =>
                handleInfoClick(
                  idType === "aadhaar"
                    ? "Aadhaar"
                    : "Voter ID"
                )
              }
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-blue-600"
            >
              ℹ
            </button>
          </div>

          {idValue.length > 0 && !isIdValid && (
            <p className="text-xs text-red-500 mt-1">
              Invalid{" "}
              {idType === "aadhaar"
                ? "Aadhaar Number"
                : "Voter ID"}
            </p>
          )}

          {/* Consent */}
          <label className="flex items-start gap-2 text-xs text-gray-600">
            <input
              type="checkbox"
              checked={consent}
              onChange={(e) => setConsent(e.target.checked)}
            />
            I voluntarily give my consent to Optima Net Bank for opening an account.
          </label>

          <label className="flex items-start gap-2 text-xs text-gray-600">
            <input
              type="checkbox"
              checked={terms}
              onChange={(e) => setTerms(e.target.checked)}
            />
            I agree to Terms and Conditions and allow OptimaNet Bank to check my CIVIL score.
          </label>

          {/* Security Note */}
          <div className="flex items-center justify-center gap-2 text-green-600 text-sm">
            <span>✔</span>
            <span>Your data is secure with us</span>
          </div>


          {/* Button */}
          <button
            type="submit"
            disabled={!isFormValid}
            className={`w-full rounded-lg py-2.5 font-medium transition
              ${
                isFormValid
                  ? "bg-blue-600 hover:bg-blue-700 text-white"
                  : "bg-gray-300 text-gray-500 cursor-not-allowed"
              }`}
          >
            Proceed to Verify
          </button>

        </form>

      {showVerify && (
        <PanVerifyPopUp
            pan={pan}
            userName={userName}
            onClose={() => setShowVerify(false)}
            onProceed={() => {
            if (idType === "aadhaar") {
                setShowVerify(false);
                setShowAadhar(true);
            } else {
                alert("Voter Verification Complete");
                setShowVerify(false);
            }
            }}
        />
        )}

        {showAadhar && (
        <AadharVerifyPopUp
            maskedAadhar={`XXXXXXXX${idValue.slice(-4)}`}
            onClose={() => setShowAadhar(false)}
        />
        )}

  </AccountStepLayout>
  );
};

export default WelcomePage;
