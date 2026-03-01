import { useNavigate } from "react-router-dom";

const productHighlights = [
  "Zero balance account opening",
  "Up to 6.5% p.a. interest",
  "Instant virtual debit card",
  "100% digital onboarding",
];

const onboardingSteps = [
  { title: "Share Personal Info", detail: "Mobile, email, and pincode" },
  { title: "Complete KYC", detail: "PAN and Aadhaar verification" },
  { title: "Set Preferences", detail: "Nominee, MPIN, and debit card setup" },
];

export default function SavingsDashboard() {
  const navigate = useNavigate();

  return (
    <main className="min-h-[calc(100vh-4rem)] bg-[radial-gradient(circle_at_15%_20%,#60a5fa_0%,#e0f2fe_26%,#f8fafc_52%),radial-gradient(circle_at_85%_85%,#22d3ee_0%,#dbeafe_30%,#f8fafc_60%)] p-4 md:p-8">
      <section className="mx-auto max-w-6xl space-y-6">
        <article className="relative overflow-hidden rounded-3xl bg-gradient-to-r from-sky-700 via-blue-700 to-indigo-700 p-6 text-white shadow-2xl sm:p-8">
          <div className="absolute -right-12 -top-12 h-40 w-40 rounded-full bg-cyan-300/20" />
          <div className="absolute -bottom-12 right-24 h-48 w-48 rounded-full bg-indigo-300/20" />
          <p className="mb-3 inline-flex rounded-full bg-white/20 px-3 py-1 text-xs font-semibold uppercase tracking-[0.2em]">
            Savings Account
          </p>
          <h1 className="max-w-3xl text-3xl font-bold leading-tight sm:text-4xl">
            Open Your Zero Balance Savings Account in Minutes
          </h1>
          <p className="mt-3 max-w-2xl text-sm text-blue-100 sm:text-base">
            Start instantly with digital onboarding, instant virtual debit card,
            and secure transaction setup built for first-time and regular users.
          </p>

          <div className="mt-6 grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
            {productHighlights.map((item) => (
              <div key={item} className="rounded-xl border border-white/20 bg-white/10 px-4 py-3 text-sm backdrop-blur-sm">
                {item}
              </div>
            ))}
          </div>

          <div className="mt-7 flex flex-wrap gap-3">
            <button
              type="button"
              onClick={() => navigate("/open-savings")}
              className="rounded-lg bg-white px-5 py-2.5 font-semibold text-blue-700 transition hover:bg-blue-50"
            >
              Start Application
            </button>
            <button
              type="button"
              className="rounded-lg border border-white/40 bg-white/5 px-5 py-2.5 font-semibold text-white transition hover:bg-white/15"
            >
              Compare Benefits
            </button>
          </div>
        </article>

        <div className="grid gap-6 lg:grid-cols-[1.15fr_1fr]">
          <aside className="rounded-2xl bg-white/90 p-6 shadow-xl backdrop-blur">
            <h2 className="text-lg font-semibold text-slate-800">Application Journey</h2>
            <p className="mt-1 text-sm text-slate-500">
              Estimated completion time: 8 to 10 minutes
            </p>

            <ol className="mt-5 space-y-4">
              {onboardingSteps.map((step, index) => (
                <li key={step.title} className="flex items-start gap-3">
                  <span className="mt-0.5 flex h-7 w-7 items-center justify-center rounded-full bg-gradient-to-r from-blue-600 to-cyan-500 text-xs font-semibold text-white">
                    {index + 1}
                  </span>
                  <div>
                    <p className="text-sm font-semibold text-slate-700">{step.title}</p>
                    <p className="text-xs text-slate-500">{step.detail}</p>
                  </div>
                </li>
              ))}
            </ol>
          </aside>

          <aside className="rounded-2xl bg-gradient-to-br from-emerald-100 via-cyan-100 to-blue-100 p-6 shadow-xl">
            <h2 className="text-lg font-semibold text-slate-800">Why Optima Savings</h2>
            <div className="mt-4 space-y-3 text-sm">
              <div className="rounded-lg border border-emerald-200 bg-white/70 px-3 py-2 text-emerald-800">
                Secure onboarding with bank-grade encryption
              </div>
              <div className="rounded-lg border border-cyan-200 bg-white/70 px-3 py-2 text-cyan-800">
                Lite account starts instantly and upgrades anytime
              </div>
              <div className="rounded-lg border border-blue-200 bg-white/70 px-3 py-2 text-blue-800">
                Full KYC unlocks unlimited transaction limits
              </div>
            </div>

            <button
              type="button"
              onClick={() => navigate("/open-savings")}
              className="mt-6 w-full rounded-lg bg-gradient-to-r from-blue-600 to-indigo-600 px-4 py-2.5 text-sm font-semibold text-white hover:from-blue-700 hover:to-indigo-700"
            >
              Open Account Now
            </button>
          </aside>
        </div>
      </section>
    </main>
  );
}
