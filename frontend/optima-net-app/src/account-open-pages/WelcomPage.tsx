import React, { useState } from "react";
import PanVerifyPopUp from "./PanVerifyPopUp";
import AadharVerifyPopUp from "./AadharVerifyPopUp";
import AccountStepLayout from "./AccountStepLayout";
import { useNavigate } from "react-router-dom";
import { useOnboarding } from "./OnboardingContext";
import { api } from "../api/api-client";

type IdType = "aadhaar" | "voter";

const WelcomePage: React.FC = () => {
  const navigate = useNavigate();
  const {
    registrationDraft,
    customerId,
    kycDraft,
    updateKycDraft,
    registrationUserId,
    setRegistrationUserId,
    setRegistrationToken,
  } = useOnboarding();

  const [pan, setPan] = useState(kycDraft.panNumber);
  const [idType, setIdType] = useState<IdType>(kycDraft.idType || "aadhaar");
  const [idValue, setIdValue] = useState(
    (kycDraft.idType || "aadhaar") === "aadhaar" ? kycDraft.aadhaarNumber : kycDraft.voterIdNumber
  );
  const [consent, setConsent] = useState(false);
  const [terms, setTerms] = useState(false);
  const [error, setError] = useState("");

  const [showVerify, setShowVerify] = useState(false);
  const [showAadhar, setShowAadhar] = useState(false);
  const [startingOtp, setStartingOtp] = useState(false);
  const [verifyingPan, setVerifyingPan] = useState(false);
  const [panHolderName, setPanHolderName] = useState("");
  const [idHolderName, setIdHolderName] = useState("");

  const userName = registrationDraft.fullName || "Customer";

  const isPanValid = /^[A-Z]{5}[0-9]{4}[A-Z]{1}$/.test(pan);
  const isAadhaarValid = /^\d{12}$/.test(idValue);
  const isVoterValid = /^[A-Z]{3}[0-9]{7}$/.test(idValue);

  const isIdValid =
    idType === "aadhaar" ? isAadhaarValid : isVoterValid;

  const isFormValid = isPanValid && isIdValid && consent && terms;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!customerId) {
      setError("Customer registration is required before KYC.");
      navigate("/open-savings");
      return;
    }
    if (!isFormValid) return;
    setError("");
    const selectedDocumentType = idType === "aadhaar" ? "AADHAAR" : "VOTER_ID";
    setVerifyingPan(true);
    Promise.all([
      api.verifyKycDocument({ documentType: "PAN", documentNumber: pan }),
      api.verifyKycDocument({ documentType: selectedDocumentType, documentNumber: idValue }),
    ])
      .then(([panResult, idResult]) => {
        const panName = panResult.holderName.trim();
        const idName = idResult.holderName.trim();
        if (panName.toUpperCase() !== idName.toUpperCase()) {
          throw new Error("PAN and selected identity document belong to different holders.");
        }
        setPanHolderName(panName);
        setIdHolderName(idName);
        updateKycDraft({
          panNumber: pan,
          idType,
          aadhaarNumber: idType === "aadhaar" ? idValue : "",
          voterIdNumber: idType === "voter" ? idValue : "",
        });
        setShowVerify(true);
      })
      .catch((err) => {
        const message = err instanceof Error ? err.message : "Unable to verify KYC details.";
        setError(message);
      })
      .finally(() => setVerifyingPan(false));
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

            {pan.length > 0 && !isPanValid && (
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
            disabled={!isFormValid || verifyingPan}
            className={`w-full rounded-lg py-2.5 font-medium transition
              ${
                isFormValid && !verifyingPan
                  ? "bg-blue-600 hover:bg-blue-700 text-white"
                  : "bg-gray-300 text-gray-500 cursor-not-allowed"
              }`}
          >
            {verifyingPan ? "Verifying KYC..." : "Proceed to Verify"}
          </button>
          {error && <p className="text-sm text-red-600">{error}</p>}

        </form>

      {showVerify && (
        <PanVerifyPopUp
            pan={pan}
            userName={panHolderName || idHolderName || userName}
            onClose={() => setShowVerify(false)}
            onProceed={async () => {
            if (idType === "aadhaar") {
                try {
                  setStartingOtp(true);
                  if (!registrationUserId) {
                    const start = await api.startRegistration({
                      fullName: registrationDraft.fullName,
                      aadhaarNumber: idValue,
                      mobileNumber: registrationDraft.mobileNumber,
                      emailAddress: registrationDraft.email,
                    });
                    setRegistrationUserId(start.userId);
                  }
                  setShowVerify(false);
                  setShowAadhar(true);
                } catch (err) {
                  const message = err instanceof Error ? err.message : "Unable to send OTP to email.";
                  setError(message);
                  setShowVerify(false);
                } finally {
                  setStartingOtp(false);
                }
            } else {
                alert("Voter Verification Complete");
                setShowVerify(false);
                navigate("/user-details");
            }
            }}
        />
        )}

        {showAadhar && (
        <AadharVerifyPopUp
            maskedAadhar={`XXXXXXXX${idValue.slice(-4)}`}
            onClose={() => setShowAadhar(false)}
            email={registrationDraft.email}
            onResend={async () => {
              if (!registrationUserId) {
                throw new Error("Registration session missing. Please restart verification.");
              }
              await api.resendRegistrationOtp(registrationUserId);
            }}
            onVerified={async (otp) => {
              if (!registrationUserId) {
                throw new Error("Registration session missing. Please restart verification.");
              }
              const result = await api.verifyRegistrationOtp({
                userId: registrationUserId,
                otp,
              });
              setRegistrationToken(result.token);
              navigate("/user-details");
            }}
        />
        )}
        {startingOtp && <p className="mt-3 text-center text-sm text-gray-600">Sending OTP to your email...</p>}

  </AccountStepLayout>
  );
};

export default WelcomePage;
