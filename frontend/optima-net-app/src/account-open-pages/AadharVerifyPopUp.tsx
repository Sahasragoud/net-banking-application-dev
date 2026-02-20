import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

interface Props {
  maskedAadhar: string; // e.g., XXXXXXXX1234
  onClose: () => void;
}

const AadharVerifyPopUp: React.FC<Props> = ({
  maskedAadhar,
  onClose,
}) => {
  const [otp, setOtp] = useState("");
  const [timer, setTimer] = useState(120); // 2 minutes
  const [canResend, setCanResend] = useState(false);

  const isOtpValid = /^\d{6}$/.test(otp);
  const navigate = useNavigate();

  // Countdown Logic
  useEffect(() => {
    if (timer <= 0) {
      setCanResend(true);
      return;
    }

    const interval = setInterval(() => {
      setTimer((prev) => prev - 1);
    }, 1000);

    return () => clearInterval(interval);
  }, [timer]);

  const formatTime = (seconds: number) => {
    const min = Math.floor(seconds / 60);
    const sec = seconds % 60;
    return `${min}:${sec.toString().padStart(2, "0")}`;
  };

  const handleResend = () => {
    if (!canResend) return;
    setTimer(120);
    setCanResend(false);
    setOtp("");
    console.log("OTP Resent");
  };

const handleVerify = () => {
  if (!isOtpValid) return;

  console.log("OTP Verified:", otp);

  // Navigate to User Details Page
  navigate("/user-details");
};

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 px-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-2xl p-6 sm:p-8">

        {/* Heading */}
        <h2 className="text-xl font-semibold text-gray-800 text-center mb-2">
          Verify your Aadhaar with OTP
        </h2>

        <p className="text-sm text-gray-600 text-center mb-6">
          OTP sent to {maskedAadhar}
        </p>

        {/* OTP Input */}
        <div>
          <label className="block text-sm font-medium text-gray-600 mb-1">
            Enter OTP
          </label>

          <input
            type="text"
            maxLength={6}
            value={otp}
            onChange={(e) => {
              if (/^\d*$/.test(e.target.value))
                setOtp(e.target.value);
            }}
            placeholder="Enter 6-digit OTP"
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none text-center tracking-widest"
          />

          {!isOtpValid && otp.length > 0 && (
            <p className="text-xs text-red-500 mt-1">
              OTP must be 6 digits
            </p>
          )}
        </div>

        {/* Resend Section */}
        <div className="flex items-center justify-between mt-4 text-sm">
          <button
            type="button"
            onClick={handleResend}
            disabled={!canResend}
            className={`font-medium ${
              canResend
                ? "text-blue-600 hover:underline"
                : "text-gray-400 cursor-not-allowed"
            }`}
          >
            Resend OTP
          </button>

          <span className="text-gray-500">
            {canResend ? "00:00" : formatTime(timer)}
          </span>
        </div>

        {/* Verify Button */}
        <button
          onClick={handleVerify}
          disabled={!isOtpValid}
          className={`w-full mt-6 py-2.5 rounded-lg font-medium transition ${
            isOtpValid
              ? "bg-blue-600 hover:bg-blue-700 text-white"
              : "bg-gray-300 text-gray-500 cursor-not-allowed"
          }`}
        >
          Verify
        </button>

        {/* Trouble Section */}
        <div className="mt-5 text-center">
          <button
            type="button"
            onClick={onClose}
            className="text-sm text-blue-600 hover:underline inline-flex items-center gap-1"
          >
            Trouble verifying with OTP? Verify Later →
          </button>
        </div>

      </div>
    </div>
  );
};

export default AadharVerifyPopUp;
