import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api, type SavingsAccountResponse } from "../api/api-client";
import { useOnboarding } from "./OnboardingContext";

export default function PostAccountDashboard() {
  const navigate = useNavigate();
  const { accountNumber, customerId, setAccountNumber } = useOnboarding();

  const [account, setAccount] = useState<SavingsAccountResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [depositAmount, setDepositAmount] = useState("");
  const [withdrawAmount, setWithdrawAmount] = useState("");
  const [mpin, setMpin] = useState("");
  const [depositSource, setDepositSource] = useState("");
  const [kycStatus, setKycStatus] = useState("UNKNOWN");
  const [nomineeCount, setNomineeCount] = useState(0);
  const [profileReady, setProfileReady] = useState(false);

  const currentAccountNumber = useMemo(() => account?.accountNumber || accountNumber, [account?.accountNumber, accountNumber]);

  useEffect(() => {
    let cancelled = false;

    const load = async () => {
      if (currentAccountNumber) {
        try {
          const response = await api.getSavingsAccount(currentAccountNumber);
          if (!cancelled) setAccount(response);
        } catch (err) {
          if (!cancelled) {
            const text = err instanceof Error ? err.message : "Unable to load savings account.";
            setError(text);
          }
        } finally {
          if (!cancelled) setLoading(false);
        }
        return;
      }

      if (customerId) {
        try {
          const accounts = await api.getSavingsByCustomer(customerId);
          const first = accounts[0];
          if (!cancelled && first) {
            setAccount(first);
            setAccountNumber(first.accountNumber);
          }
          if (!cancelled && !first) {
            setError("No savings account found for this customer.");
          }
        } catch (err) {
          if (!cancelled) {
            const text = err instanceof Error ? err.message : "Unable to load customer accounts.";
            setError(text);
          }
        } finally {
          if (!cancelled) setLoading(false);
        }
        return;
      }

      if (!cancelled) {
        setLoading(false);
        setError("No onboarding session found. Please open an account first.");
      }
    };

    void load();
    return () => {
      cancelled = true;
    };
  }, [currentAccountNumber, customerId, setAccountNumber]);

  useEffect(() => {
    let cancelled = false;
    const loadCustomerContext = async () => {
      if (!account?.customerId) return;
      const [kyc, nominees, profile] = await Promise.all([
        api.getKyc(account.customerId).catch(() => null),
        api.getNominees(account.customerId).catch(() => []),
        api.getProfile(account.customerId).catch(() => null),
      ]);
      if (cancelled) return;
      setKycStatus(kyc?.kycStatus || "UNKNOWN");
      setNomineeCount(nominees.length);
      setProfileReady(Boolean(profile));
    };
    void loadCustomerContext();
    return () => {
      cancelled = true;
    };
  }, [account?.customerId]);

  const reloadAccount = async (accNo: string) => {
    const response = await api.getSavingsAccount(accNo);
    setAccount(response);
    if (response.customerId) {
      const [kyc, nominees] = await Promise.all([
        api.getKyc(response.customerId).catch(() => null),
        api.getNominees(response.customerId).catch(() => []),
      ]);
      setKycStatus(kyc?.kycStatus || "UNKNOWN");
      setNomineeCount(nominees.length);
      const profile = await api.getProfile(response.customerId).catch(() => null);
      setProfileReady(Boolean(profile));
    }
  };

  const handleDeposit = async () => {
    if (!currentAccountNumber) return;
    const amount = Number(depositAmount);
    if (!Number.isFinite(amount) || amount <= 0 || submitting) return;
    if (!/^\d{6}$/.test(mpin)) {
      setError("Enter 6-digit MPIN to continue.");
      return;
    }
    if (!depositSource.trim()) {
      setError("Enter source reference for deposit.");
      return;
    }
    setSubmitting(true);
    setError("");
    setMessage("");
    try {
      const response = await api.deposit(currentAccountNumber, {
        amount,
        mpin,
        sourceReference: depositSource.trim(),
        remarks: "Customer initiated deposit",
      });
      setAccount(response);
      setDepositAmount("");
      setDepositSource("");
      setMessage("Deposit successful.");
    } catch (err) {
      const text = err instanceof Error ? err.message : "Deposit failed.";
      setError(text);
    } finally {
      setSubmitting(false);
    }
  };

  const handleWithdraw = async () => {
    if (!currentAccountNumber) return;
    const amount = Number(withdrawAmount);
    if (!Number.isFinite(amount) || amount <= 0 || submitting) return;
    if (!/^\d{6}$/.test(mpin)) {
      setError("Enter 6-digit MPIN to continue.");
      return;
    }
    setSubmitting(true);
    setError("");
    setMessage("");
    try {
      const response = await api.withdraw(currentAccountNumber, {
        amount,
        mpin,
        remarks: "Customer initiated withdrawal",
      });
      setAccount(response);
      setWithdrawAmount("");
      setMessage("Withdrawal successful.");
    } catch (err) {
      const text = err instanceof Error ? err.message : "Withdrawal failed.";
      setError(text);
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <main className="p-6 text-center text-slate-700">Loading account dashboard...</main>;
  }

  if (!account) {
    return (
      <main className="p-6">
        <div className="mx-auto max-w-3xl rounded-xl border border-red-200 bg-red-50 p-4 text-red-700">
          {error || "No account data available."}
        </div>
        <div className="mx-auto mt-4 max-w-3xl">
          <button
            type="button"
            onClick={() => navigate("/open-savings")}
            className="rounded-lg bg-blue-600 px-4 py-2 text-white hover:bg-blue-700"
          >
            Start Onboarding
          </button>
        </div>
      </main>
    );
  }

  return (
    <main className="min-h-[calc(100vh-4rem)] bg-gradient-to-br from-blue-50 via-sky-50 to-indigo-100 p-4 md:p-8">
      <section className="mx-auto max-w-6xl space-y-5">
        <article className="rounded-2xl bg-gradient-to-r from-blue-700 via-blue-600 to-indigo-600 p-6 text-white shadow-xl">
          <p className="text-sm text-blue-100">Savings Dashboard</p>
          <h1 className="mt-1 text-2xl font-bold">Account {account.accountNumber}</h1>
          <p className="mt-2 text-sm text-blue-100">
            {account.bankName} | {account.branchName} | IFSC {account.ifscCode}
          </p>
          <p className="mt-1 text-sm text-blue-100">
            Holder: {account.holderName} | Tier: {account.customerStatus}
          </p>
          <p className="mt-1 text-sm text-blue-100">UPI: {account.upiHandle}</p>
          <div className="mt-4 grid gap-3 sm:grid-cols-3">
            <Metric label="Available Balance" value={`Rs ${account.availableBalance.toLocaleString("en-IN")}`} />
            <Metric label="Interest Rate" value={`${account.interestRate}%`} />
            <Metric label="Customer Code" value={account.customerCode} />
          </div>
        </article>
        <div className="grid gap-3 sm:grid-cols-3">
          <div className="rounded-lg border border-slate-200 bg-white p-3">
            <p className="text-xs text-slate-500">KYC Status</p>
            <p className="mt-1 text-sm font-semibold text-slate-700">{kycStatus}</p>
          </div>
          <div className="rounded-lg border border-slate-200 bg-white p-3">
            <p className="text-xs text-slate-500">Nominees Added</p>
            <p className="mt-1 text-sm font-semibold text-slate-700">{nomineeCount}</p>
          </div>
          <div className="rounded-lg border border-slate-200 bg-white p-3">
            <p className="text-xs text-slate-500">Profile Details</p>
            <p className="mt-1 text-sm font-semibold text-slate-700">{profileReady ? "Completed" : "Pending"}</p>
          </div>
        </div>
        {account.customerStatus === "LITE" && (
          <p className="rounded-lg border border-amber-200 bg-amber-50 p-3 text-amber-800">
            Lite customer limits apply: maximum INR 5000 per transaction until full KYC approval.
          </p>
        )}

        {message && <p className="rounded-lg border border-emerald-200 bg-emerald-50 p-3 text-emerald-700">{message}</p>}
        {error && <p className="rounded-lg border border-red-200 bg-red-50 p-3 text-red-700">{error}</p>}

        <div className="grid gap-5 lg:grid-cols-2">
          <div className="rounded-2xl bg-white p-5 shadow">
            <h2 className="mb-3 text-lg font-semibold text-slate-800">Quick Actions</h2>
            <div className="space-y-3">
              <input
                type="password"
                inputMode="numeric"
                maxLength={6}
                value={mpin}
                onChange={(e) => setMpin(e.target.value.replace(/\D/g, "").slice(0, 6))}
                className="w-full rounded-lg border border-slate-300 px-3 py-2"
                placeholder="Enter 6-digit MPIN"
              />
              <div className="flex gap-2">
                <input
                  type="number"
                  min={1}
                  value={depositAmount}
                  onChange={(e) => setDepositAmount(e.target.value)}
                  className="w-full rounded-lg border border-slate-300 px-3 py-2"
                  placeholder="Deposit amount"
                />
                <input
                  type="text"
                  value={depositSource}
                  onChange={(e) => setDepositSource(e.target.value)}
                  className="w-full rounded-lg border border-slate-300 px-3 py-2"
                  placeholder="Source reference (UPI/IMPS)"
                />
                <button
                  type="button"
                  onClick={() => void handleDeposit()}
                  disabled={submitting}
                  className="rounded-lg bg-emerald-600 px-4 py-2 font-semibold text-white hover:bg-emerald-700 disabled:bg-slate-300"
                >
                  Deposit
                </button>
              </div>
              <div className="flex gap-2">
                <input
                  type="number"
                  min={1}
                  value={withdrawAmount}
                  onChange={(e) => setWithdrawAmount(e.target.value)}
                  className="w-full rounded-lg border border-slate-300 px-3 py-2"
                  placeholder="Withdraw amount"
                />
                <button
                  type="button"
                  onClick={() => void handleWithdraw()}
                  disabled={submitting}
                  className="rounded-lg bg-rose-600 px-4 py-2 font-semibold text-white hover:bg-rose-700 disabled:bg-slate-300"
                >
                  Withdraw
                </button>
              </div>
              <button
                type="button"
                onClick={() => void reloadAccount(account.accountNumber)}
                className="w-full rounded-lg border border-slate-300 px-4 py-2 font-semibold text-slate-700 hover:bg-slate-100"
              >
                Refresh Account
              </button>
            </div>
          </div>

          <div className="rounded-2xl bg-white p-5 shadow">
            <h2 className="mb-3 text-lg font-semibold text-slate-800">Recent Transactions</h2>
            <div className="space-y-2">
              {account.recentTransactions.length === 0 && (
                <p className="text-sm text-slate-500">No transactions available.</p>
              )}
              {account.recentTransactions.map((txn) => (
                <div key={txn.id} className="rounded-lg border border-slate-100 p-3">
                  <p className="text-sm font-semibold text-slate-700">{txn.txnType}</p>
                  <p className="text-xs text-slate-500">{new Date(txn.createdAt).toLocaleString("en-IN")}</p>
                  <p className="mt-1 text-sm">Amount: Rs {txn.amount.toLocaleString("en-IN")}</p>
                  <p className="text-xs text-slate-500">Balance after txn: Rs {txn.balanceAfterTxn.toLocaleString("en-IN")}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>
    </main>
  );
}

function Metric({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-xl border border-white/20 bg-white/10 px-4 py-3">
      <p className="text-xs text-blue-100">{label}</p>
      <p className="mt-1 text-lg font-semibold">{value}</p>
    </div>
  );
}
