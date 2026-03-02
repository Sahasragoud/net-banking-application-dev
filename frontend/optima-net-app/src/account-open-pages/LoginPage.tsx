import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api/api-client";
import { useOnboarding } from "./OnboardingContext";

export default function LoginPage() {
  const navigate = useNavigate();
  const { setCustomerData } = useOnboarding();

  const [customerCode, setCustomerCode] = useState("");
  const [mpin, setMpin] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [mfaCode, setMfaCode] = useState("");
  const [mfaRequired, setMfaRequired] = useState(false);
  const [pendingCustomerId, setPendingCustomerId] = useState<number | null>(null);

  const isCustomerCodeValid = /^OPT-CUST-\d{4}-\d{6}$/.test(customerCode.trim());
  const isMpinValid = /^\d{6}$/.test(mpin);
  const isMfaValid = /^\d{6}$/.test(mfaCode);

  const handleComplete = async () => {
    if (!isCustomerCodeValid || !isMpinValid || loading) return;
    setLoading(true);
    setError("");
    setMessage("");
    try {
      const result = await api.loginV2({
        customerCode: customerCode.trim().toUpperCase(),
        mpin,
        mfaCode: mfaRequired ? mfaCode : undefined,
      });
      if (result.mfaRequired) {
        setMfaRequired(true);
        setPendingCustomerId(result.customerId);
        setMessage("Enter your authenticator app code to finish login.");
        return;
      }
      setCustomerData({ customerId: result.customerId, customerCode: result.customerCode });
      navigate("/dashboard");
    } catch (err) {
      const text = err instanceof Error ? err.message : "Login failed.";
      setError(text);
    } finally {
      setLoading(false);
    }
  };

  const handleMfaVerify = async () => {
    if (!pendingCustomerId || !isMfaValid || loading) return;
    await handleComplete();
  };

  return (
    <main className="min-h-[calc(100vh-4rem)] bg-slate-50 p-6">
      <section className="mx-auto max-w-md rounded-2xl bg-white p-6 shadow">
        <h1 className="text-2xl font-semibold text-slate-800">Login</h1>
        <p className="mt-1 text-sm text-slate-500">Sign in using customer code, MPIN, and optional MFA.</p>

        <div className="mt-5 space-y-3">
          <input
            type="text"
            value={customerCode}
            onChange={(e) => setCustomerCode(e.target.value.toUpperCase())}
            placeholder="OPT-CUST-2026-000001"
            className="w-full rounded-lg border border-slate-300 px-3 py-2"
          />
          <input
            type="password"
            maxLength={6}
            value={mpin}
            onChange={(e) => setMpin(e.target.value.replace(/\D/g, ""))}
            placeholder="Enter 6-digit MPIN"
            className="w-full rounded-lg border border-slate-300 px-3 py-2"
          />
          <button
            type="button"
            onClick={() => void handleComplete()}
            disabled={!isCustomerCodeValid || !isMpinValid || loading}
            className="w-full rounded-lg bg-blue-600 px-4 py-2 font-semibold text-white disabled:bg-slate-300"
          >
            {loading ? "Signing in..." : "Login"}
          </button>

          {mfaRequired && pendingCustomerId && (
            <>
              <input
                type="text"
                maxLength={6}
                value={mfaCode}
                onChange={(e) => setMfaCode(e.target.value.replace(/\D/g, ""))}
                placeholder="Authenticator 6-digit code"
                className="w-full rounded-lg border border-slate-300 px-3 py-2"
              />
              <button
                type="button"
                onClick={() => void handleMfaVerify()}
                disabled={!isMfaValid || loading}
                className="w-full rounded-lg bg-indigo-600 px-4 py-2 font-semibold text-white disabled:bg-slate-300"
              >
                {loading ? "Checking MFA..." : "Verify MFA"}
              </button>
            </>
          )}
        </div>

        {message && <p className="mt-3 text-sm text-emerald-700">{message}</p>}
        {error && <p className="mt-3 text-sm text-red-600">{error}</p>}
      </section>
    </main>
  );
}
