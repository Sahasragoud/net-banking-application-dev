import React, { useEffect, useState } from "react";

interface Props {
  maskedAadhar: string;
  email: string;
  onClose: () => void;
  onVerified: (otp: string) => Promise<void>;
  onResend: () => Promise<void>;
}

const AadharVerifyPopUp: React.FC<Props> = ({
  maskedAadhar,
  email,
  onClose,
  onVerified,
  onResend,
}) => {
  const [otp, setOtp] = useState("");
  const [timer, setTimer] = useState(120); // 2 minutes
  const [canResend, setCanResend] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const isOtpValid = /^\d{6}$/.test(otp);

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
    if (!canResend || loading) return;
    setLoading(true);
    setError("");
    onResend()
      .then(() => {
        setTimer(120);
        setCanResend(false);
        setOtp("");
      })
      .catch((err) => {
        const message = err instanceof Error ? err.message : "Failed to resend OTP.";
        setError(message);
      })
      .finally(() => setLoading(false));
  };

  const handleVerify = async () => {
    if (!isOtpValid || loading) return;
    setLoading(true);
    setError("");
    try {
      await onVerified(otp);
    } catch (err) {
      const message = err instanceof Error ? err.message : "OTP verification failed.";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 px-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-2xl p-6 sm:p-8">

        {/* Heading */}
        <h2 className="text-xl font-semibold text-gray-800 text-center mb-2">
          Verify your Aadhaar by Email OTP
        </h2>

        <p className="text-sm text-gray-600 text-center mb-6">
          OTP sent to {email} for Aadhaar {maskedAadhar}
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
            disabled={!canResend || loading}
            className={`font-medium ${
              canResend && !loading
                ? "text-blue-600 hover:underline"
                : "text-gray-400 cursor-not-allowed"
            }`}
          >
            {loading ? "Please wait..." : "Resend OTP"}
          </button>

          <span className="text-gray-500">
            {canResend ? "00:00" : formatTime(timer)}
          </span>
        </div>

        {/* Verify Button */}
        <button
          onClick={handleVerify}
          disabled={!isOtpValid || loading}
          className={`w-full mt-6 py-2.5 rounded-lg font-medium transition ${
            isOtpValid && !loading
              ? "bg-blue-600 hover:bg-blue-700 text-white"
              : "bg-gray-300 text-gray-500 cursor-not-allowed"
          }`}
        >
          {loading ? "Verifying..." : "Verify"}
        </button>
        {error && <p className="mt-2 text-center text-sm text-red-600">{error}</p>}

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
