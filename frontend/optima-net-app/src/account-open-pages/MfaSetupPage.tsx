import { useState } from "react";
import { api } from "../api/api-client";
import { useOnboarding } from "./OnboardingContext";

export default function MfaSetupPage() {
  const { customerId } = useOnboarding();
  const [secret, setSecret] = useState("");
  const [otpauthUrl, setOtpauthUrl] = useState("");
  const [code, setCode] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleSetup = async () => {
    if (!customerId || loading) return;
    setLoading(true);
    setError("");
    setMessage("");
    try {
      const response = await api.setupMfa(customerId);
      setSecret(response.secret);
      setOtpauthUrl(response.otpauthUrl);
      setMessage("MFA secret created. Add it in Google Authenticator and enter generated code.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "MFA setup failed.");
    } finally {
      setLoading(false);
    }
  };

  const handleEnable = async () => {
    if (!customerId || !/^\d{6}$/.test(code) || loading) return;
    setLoading(true);
    setError("");
    setMessage("");
    try {
      await api.enableMfa(customerId, { code });
      setMessage("MFA enabled successfully.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to enable MFA.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="min-h-[calc(100vh-4rem)] bg-slate-50 p-6">
      <section className="mx-auto max-w-xl rounded-2xl bg-white p-6 shadow">
        <h1 className="text-2xl font-semibold text-slate-800">Enable MFA</h1>
        <p className="mt-1 text-sm text-slate-500">Use Google Authenticator or any TOTP app.</p>

        {!customerId && <p className="mt-4 text-sm text-red-600">Customer session not found. Login first.</p>}

        <div className="mt-4 space-y-3">
          <button
            type="button"
            disabled={!customerId || loading}
            onClick={() => void handleSetup()}
            className="rounded-lg bg-blue-600 px-4 py-2 font-semibold text-white disabled:bg-slate-300"
          >
            {loading ? "Preparing..." : "Generate MFA Secret"}
          </button>

          {secret && (
            <>
              <p className="text-sm text-slate-700">Secret: <span className="font-mono">{secret}</span></p>
              <p className="break-all text-xs text-slate-500">OTPAuth URL: {otpauthUrl}</p>
              <input
                type="text"
                maxLength={6}
                value={code}
                onChange={(e) => setCode(e.target.value.replace(/\D/g, ""))}
                placeholder="Enter 6-digit authenticator code"
                className="w-full rounded-lg border border-slate-300 px-3 py-2"
              />
              <button
                type="button"
                disabled={!/^\d{6}$/.test(code) || loading}
                onClick={() => void handleEnable()}
                className="rounded-lg bg-emerald-600 px-4 py-2 font-semibold text-white disabled:bg-slate-300"
              >
                {loading ? "Enabling..." : "Enable MFA"}
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
