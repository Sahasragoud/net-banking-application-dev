import { useMemo, useState } from "react";
import type { FormEvent, ReactNode } from "react";

type TxType = "credit" | "debit";
type Tab = "overview" | "transactions" | "payments" | "cards" | "services" | "security";
type Tx = { id: number; title: string; date: string; amount: number; type: TxType; mode: string };
type Beneficiary = { id: number; name: string; bank: string; account: string; upi: string };

const START_BAL = 25420;
const LITE_TX_LIMIT = 5000;
const tabs: Tab[] = ["overview", "transactions", "payments", "cards", "services", "security"];

const seedTx: Tx[] = [
  { id: 1, title: "Salary Credit", date: "Feb 28, 2026", amount: 42000, type: "credit", mode: "ACH" },
  { id: 2, title: "UPI - Grocery", date: "Feb 27, 2026", amount: 1240, type: "debit", mode: "UPI" },
  { id: 3, title: "Rent", date: "Feb 25, 2026", amount: 4500, type: "debit", mode: "NEFT" },
  { id: 4, title: "Cashback", date: "Feb 24, 2026", amount: 320, type: "credit", mode: "CARD" },
];

const seedBene: Beneficiary[] = [
  { id: 1, name: "Rohan Sharma", bank: "Optima Bank", account: "XXXXXX1920", upi: "rohan@optima" },
  { id: 2, name: "Ananya K", bank: "SBI", account: "XXXXXX4401", upi: "ananya@sbi" },
];

export default function PostAccountDashboard() {
  const [tab, setTab] = useState<Tab>("overview");
  const [tx, setTx] = useState<Tx[]>(seedTx);
  const [filter, setFilter] = useState<"all" | TxType>("all");
  const [search, setSearch] = useState("");
  const [message, setMessage] = useState("");

  const [beneficiaries, setBeneficiaries] = useState<Beneficiary[]>(seedBene);
  const [selectedBene, setSelectedBene] = useState(seedBene[0].id);
  const [mode, setMode] = useState("UPI");
  const [amount, setAmount] = useState("");
  const [note, setNote] = useState("");
  const [newName, setNewName] = useState("");
  const [newBank, setNewBank] = useState("");
  const [newAcct, setNewAcct] = useState("");
  const [newUpi, setNewUpi] = useState("");

  const [onlineCard, setOnlineCard] = useState(true);
  const [internationalCard, setInternationalCard] = useState(false);
  const [atmCard, setAtmCard] = useState(true);
  const [limit, setLimit] = useState(50000);

  const [ticketSubject, setTicketSubject] = useState("");
  const [ticketText, setTicketText] = useState("");
  const [isKycComplete, setIsKycComplete] = useState(false);
  const [twoFA, setTwoFA] = useState(true);
  const [loginAlerts, setLoginAlerts] = useState(true);
  const [mpin, setMpin] = useState("");
  const [confirmMpin, setConfirmMpin] = useState("");

  const balance = useMemo(
    () => tx.reduce((s, t) => (t.type === "credit" ? s + t.amount : s - t.amount), START_BAL),
    [tx]
  );
  const debitMonth = useMemo(() => tx.filter((t) => t.type === "debit").reduce((s, t) => s + t.amount, 0), [tx]);
  const creditMonth = useMemo(() => tx.filter((t) => t.type === "credit").reduce((s, t) => s + t.amount, 0), [tx]);
  const visibleTx = useMemo(
    () =>
      tx.filter((t) => {
        const okType = filter === "all" || t.type === filter;
        const q = search.trim().toLowerCase();
        const okText = !q || t.title.toLowerCase().includes(q) || t.mode.toLowerCase().includes(q);
        const okLiteRule = isKycComplete || t.type === "credit" || t.amount <= LITE_TX_LIMIT;
        return okType && okText && okLiteRule;
      }),
    [tx, filter, search, isKycComplete]
  );

  const selected = beneficiaries.find((b) => b.id === selectedBene);
  const amt = Number(amount);
  const effectiveTxLimit = isKycComplete ? Number.MAX_SAFE_INTEGER : LITE_TX_LIMIT;
  const canTransfer =
    Boolean(selected) &&
    Number.isFinite(amt) &&
    amt > 0 &&
    amt <= balance &&
    amt <= effectiveTxLimit;
  const canAddBene =
    newName.trim().length > 2 && newBank.trim().length > 2 && newAcct.trim().length >= 6 && newUpi.includes("@");
  const canSetMpin = mpin.length === 6 && confirmMpin === mpin;

  const addTx = (entry: Omit<Tx, "id" | "date">) =>
    setTx((p) => [{ id: Date.now(), date: "Mar 01, 2026", ...entry }, ...p]);

  const handleTransfer = (e: FormEvent) => {
    e.preventDefault();
    if (!selected) return;
    if (!Number.isFinite(amt) || amt <= 0) return setMessage("Enter a valid transfer amount.");
    if (amt > balance) return setMessage("Insufficient account balance.");
    if (!isKycComplete && amt > LITE_TX_LIMIT) {
      return setMessage("Lite account limit: complete full KYC for transactions above Rs 5,000.");
    }
    addTx({ title: `${mode} to ${selected.name}${note ? ` (${note.trim()})` : ""}`, amount: amt, type: "debit", mode });
    setAmount("");
    setNote("");
    setMessage(`Transferred Rs ${amt.toLocaleString("en-IN")} to ${selected.name}.`);
    setTab("overview");
  };

  const handleAddBene = (e: FormEvent) => {
    e.preventDefault();
    if (!canAddBene) return;
    const id = Date.now();
    const bene = { id, name: newName.trim(), bank: newBank.trim(), account: `XXXXXX${newAcct.slice(-4)}`, upi: newUpi.trim() };
    setBeneficiaries((p) => [bene, ...p]);
    setSelectedBene(id);
    setNewName(""); setNewBank(""); setNewAcct(""); setNewUpi("");
    setMessage("Beneficiary added.");
  };

  const handlePayBill = (name: string, due: number) => {
    if (!isKycComplete && due > LITE_TX_LIMIT) {
      return setMessage("Lite account limit: complete full KYC for transactions above Rs 5,000.");
    }
    if (due > balance) return setMessage("Insufficient balance for bill payment.");
    addTx({ title: `Bill payment - ${name}`, amount: due, type: "debit", mode: "IMPS" });
    setMessage(`Bill paid: ${name} (Rs ${due.toLocaleString("en-IN")}).`);
    setTab("overview");
  };

  return (
    <main className="min-h-[calc(100vh-4rem)] bg-gradient-to-br from-blue-50 via-sky-50 to-indigo-100 p-4 md:p-8">
      <section className="mx-auto max-w-7xl space-y-5">
        <div className="rounded-2xl bg-white p-2 shadow"><div className="flex gap-2 overflow-x-auto">
          {tabs.map((t) => <button key={t} onClick={() => setTab(t)} className={`rounded-lg px-4 py-2 text-sm font-semibold ${tab===t?"bg-blue-600 text-white":"bg-slate-100 text-slate-700 hover:bg-slate-200"}`}>{t[0].toUpperCase()+t.slice(1)}</button>)}
        </div></div>

        {message && <p className="rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">{message}</p>}
        {!isKycComplete && (
          <p className="rounded-xl border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-700">
            Lite account active: max Rs {LITE_TX_LIMIT.toLocaleString("en-IN")} per transaction. Complete full KYC to unlock unlimited transactions.
          </p>
        )}

        {tab === "overview" && (
          <div className="grid gap-6 lg:grid-cols-[1.3fr_1fr]">
            <div className="space-y-6">
              <div className="rounded-2xl bg-gradient-to-r from-blue-700 via-blue-600 to-indigo-600 p-5 sm:p-6 text-white shadow-xl">
                <p className="text-sm text-blue-100">Optima Savings Account</p>
                <h1 className="mt-1 text-2xl font-bold sm:text-3xl">Customer Dashboard</h1>
                <p className="mt-2 text-sm text-blue-100">A/C: 987654321234 | IFSC: ABCD0123456 | UPI: sahasra@bank</p>
                <div className="mt-6 grid gap-3 sm:grid-cols-3">
                  <Metric label="Available Balance" value={`Rs ${balance.toLocaleString("en-IN")}`} />
                  <Metric label="Month Debit" value={`Rs ${debitMonth.toLocaleString("en-IN")}`} />
                  <Metric label="Month Credit" value={`Rs ${creditMonth.toLocaleString("en-IN")}`} />
                </div>
              </div>
              <Card title="Recent Transactions">{tx.slice(0,5).map((t)=><TxRow key={t.id} t={t} />)}</Card>
            </div>
            <div className="space-y-6">
              <Card title="Quick Actions">
                <div className="grid grid-cols-2 gap-3">
                  {[
                    { label: "Transfer", key: "payments" as Tab },
                    { label: "Beneficiaries", key: "payments" as Tab },
                    { label: "Cards", key: "cards" as Tab },
                    { label: "Services", key: "services" as Tab },
                  ].map((q) => (
                    <button key={q.label} onClick={() => setTab(q.key)} className="rounded-lg border border-slate-200 px-3 py-3 text-sm font-medium text-slate-700 hover:border-blue-300 hover:bg-blue-50">
                      {q.label}
                    </button>
                  ))}
                </div>
              </Card>
              <Card title="Profile Status">
                <Status label="KYC" value={isKycComplete ? "Full KYC Completed" : "Min KYC Completed"} warn={!isKycComplete} />
                <Status label="Email" value="Verified" />
                <Status label="Mobile" value="Verified" />
                <Status label="Nominee" value="Pending" warn />
                {!isKycComplete && (
                  <button
                    type="button"
                    onClick={() => {
                      setIsKycComplete(true);
                      setMessage("Full KYC completed. Unlimited transactions are now enabled.");
                    }}
                    className="mt-2 w-full rounded-lg bg-emerald-600 px-3 py-2 text-sm font-semibold text-white hover:bg-emerald-700"
                  >
                    Complete Full KYC
                  </button>
                )}
              </Card>
            </div>
          </div>
        )}

        {tab === "transactions" && (
          <Card title="Transactions and Statement">
            <div className="grid gap-3 md:grid-cols-[2fr_1fr]">
              <input value={search} onChange={(e)=>setSearch(e.target.value)} className="w-full rounded-lg border border-slate-300 px-3 py-2" placeholder="Search description or mode" />
              <select value={filter} onChange={(e)=>setFilter(e.target.value as "all" | TxType)} className="w-full rounded-lg border border-slate-300 px-3 py-2">
                <option value="all">All</option><option value="credit">Credit</option><option value="debit">Debit</option>
              </select>
            </div>
            <div className="mt-4 space-y-3">{visibleTx.length ? visibleTx.map((t)=><TxRow key={t.id} t={t} />) : <p className="text-sm text-slate-500">No transactions found.</p>}</div>
          </Card>
        )}

        {tab === "payments" && (
          <div className="grid gap-6 md:grid-cols-[1.15fr_1fr]">
            <div className="rounded-2xl bg-gradient-to-br from-blue-700 via-indigo-600 to-cyan-600 p-[1px] shadow-xl">
              <Card title="Fund Transfer">
              <p className="mb-3 text-xs text-slate-500">
                {isKycComplete
                  ? "Full KYC account: no transaction ceiling."
                  : `Lite account: transactions above Rs ${LITE_TX_LIMIT.toLocaleString("en-IN")} are blocked.`}
              </p>
              <form className="space-y-3" onSubmit={handleTransfer}>
                <select value={mode} onChange={(e)=>setMode(e.target.value)} className="w-full rounded-lg border border-slate-300 px-3 py-2"><option>UPI</option><option>IMPS</option><option>NEFT</option></select>
                <select value={selectedBene} onChange={(e)=>setSelectedBene(Number(e.target.value))} className="w-full rounded-lg border border-slate-300 px-3 py-2">
                  {beneficiaries.map((b)=><option key={b.id} value={b.id}>{b.name} - {b.account}</option>)}
                </select>
                <input type="number" min={1} max={isKycComplete ? undefined : LITE_TX_LIMIT} value={amount} onChange={(e)=>setAmount(e.target.value)} className="w-full rounded-lg border border-slate-300 px-3 py-2" placeholder="Amount (Rs)" />
                <input value={note} onChange={(e)=>setNote(e.target.value)} className="w-full rounded-lg border border-slate-300 px-3 py-2" placeholder="Note (optional)" />
                <button disabled={!canTransfer} className={`w-full rounded-lg py-2.5 font-semibold text-white ${canTransfer?"bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700":"bg-slate-300"}`}>Transfer Now</button>
              </form>
              </Card>
            </div>
            <div className="rounded-2xl bg-gradient-to-br from-emerald-500 via-cyan-500 to-blue-600 p-[1px] shadow-xl">
              <Card title="Beneficiaries">
              <div className="space-y-3 mb-4">{beneficiaries.map((b)=><div key={b.id} className="rounded-xl border border-slate-100 px-3 py-2 text-sm"><div className="flex items-center justify-between"><p className="font-semibold text-slate-700">{b.name}</p><button className="text-xs text-rose-600 hover:underline" onClick={()=>{setBeneficiaries((p)=>p.filter((x)=>x.id!==b.id)); if(selectedBene===b.id){const next=beneficiaries.find((x)=>x.id!==b.id); setSelectedBene(next?.id ?? 0);} setMessage("Beneficiary removed.");}}>Remove</button></div><p className="text-xs text-slate-500">{b.bank} | {b.account} | {b.upi}</p></div>)}</div>
              <form className="space-y-2" onSubmit={handleAddBene}>
                <input value={newName} onChange={(e)=>setNewName(e.target.value)} className="w-full rounded-lg border border-slate-300 px-3 py-2" placeholder="Name" />
                <input value={newBank} onChange={(e)=>setNewBank(e.target.value)} className="w-full rounded-lg border border-slate-300 px-3 py-2" placeholder="Bank" />
                <input value={newAcct} onChange={(e)=>setNewAcct(e.target.value.replace(/\D/g,""))} className="w-full rounded-lg border border-slate-300 px-3 py-2" placeholder="Account number" />
                <input value={newUpi} onChange={(e)=>setNewUpi(e.target.value)} className="w-full rounded-lg border border-slate-300 px-3 py-2" placeholder="UPI id" />
                <button disabled={!canAddBene} className={`w-full rounded-lg py-2.5 font-semibold text-white ${canAddBene?"bg-gradient-to-r from-emerald-600 to-cyan-600 hover:from-emerald-700 hover:to-cyan-700":"bg-slate-300"}`}>Add Beneficiary</button>
              </form>
              </Card>
            </div>
          </div>
        )}

        {tab === "cards" && (
          <div className="grid gap-6 lg:grid-cols-[1.1fr_1fr]">
            <div className="rounded-3xl bg-gradient-to-br from-cyan-400 via-blue-600 to-indigo-800 p-1 shadow-2xl">
              <div className="rounded-3xl bg-gradient-to-br from-slate-900/90 via-indigo-900/90 to-blue-900/90 p-6 text-white">
                <div className="flex items-start justify-between">
                  <p className="text-xs uppercase tracking-[0.24em] text-cyan-200">Virtual Debit Card</p>
                  <span className="rounded-full bg-white/20 px-2 py-1 text-[10px] font-semibold">OPTIMA</span>
                </div>
                <div className="mt-8 h-10 w-14 rounded-lg bg-gradient-to-br from-yellow-200 to-yellow-500" />
                <p className="mt-6 text-2xl tracking-[0.24em] sm:text-3xl">XXXX XXXX XXXX 1234</p>
                <div className="mt-6 flex items-center justify-between text-sm text-blue-100">
                  <p>VALID THRU 12/28</p>
                  <p>CVV ***</p>
                </div>
              </div>
            </div>
            <Card title="Card Controls">
              <Toggle label="Online payments" on={onlineCard} setOn={setOnlineCard} />
              <Toggle label="International" on={internationalCard} setOn={setInternationalCard} />
              <Toggle label="ATM withdrawals" on={atmCard} setOn={setAtmCard} />
              <label className="text-sm text-slate-600">Daily card limit: Rs {limit.toLocaleString("en-IN")}</label>
              <input type="range" min={5000} max={200000} step={5000} value={limit} onChange={(e)=>setLimit(Number(e.target.value))} className="w-full" />
            </Card>
          </div>
        )}

        {tab === "services" && (
          <div className="grid gap-6 lg:grid-cols-[1.2fr_1fr]">
            <Card title="Bill Payments">
              {[
                ["BESCOM (Electricity)", 1560],
                ["Jio Fiber", 999],
                ["LIC Premium", 3820],
              ].map(([name, due]) => (
                <div key={String(name)} className="mb-3 flex flex-col gap-2 rounded-xl border border-slate-100 px-4 py-3 sm:flex-row sm:items-center sm:justify-between">
                  <div><p className="text-sm font-semibold text-slate-700">{name}</p></div>
                  <div className="flex items-center gap-3"><p className="text-sm font-semibold">Rs {Number(due).toLocaleString("en-IN")}</p><button disabled={!isKycComplete && Number(due) > LITE_TX_LIMIT} onClick={()=>handlePayBill(String(name), Number(due))} className={`rounded px-3 py-1 text-xs text-white ${!isKycComplete && Number(due) > LITE_TX_LIMIT ? "bg-slate-300" : "bg-blue-600 hover:bg-blue-700"}`}>Pay</button></div>
                </div>
              ))}
            </Card>
            <Card title="Support Ticket">
              <form className="space-y-3" onSubmit={(e)=>{e.preventDefault(); if(ticketSubject.trim().length<3||ticketText.trim().length<8) return; setTicketSubject(""); setTicketText(""); setMessage("Support ticket created.");}}>
                <input value={ticketSubject} onChange={(e)=>setTicketSubject(e.target.value)} className="w-full rounded-lg border border-slate-300 px-3 py-2" placeholder="Subject" />
                <textarea value={ticketText} onChange={(e)=>setTicketText(e.target.value)} className="h-28 w-full rounded-lg border border-slate-300 px-3 py-2" placeholder="Issue details" />
                <button className="w-full rounded-lg bg-blue-600 py-2.5 font-semibold text-white hover:bg-blue-700">Raise Ticket</button>
              </form>
            </Card>
          </div>
        )}

        {tab === "security" && (
          <div className="grid gap-6 lg:grid-cols-[1fr_1fr]">
            <Card title="Security Controls">
              <Toggle label="2-Factor Authentication" on={twoFA} setOn={setTwoFA} />
              <Toggle label="Login Alerts" on={loginAlerts} setOn={setLoginAlerts} />
            </Card>
            <Card title="Update MPIN">
              <input type="password" value={mpin} onChange={(e)=>setMpin(e.target.value.replace(/\D/g,"").slice(0,6))} className="w-full rounded-lg border border-slate-300 px-3 py-2" placeholder="New 6-digit MPIN" />
              <input type="password" value={confirmMpin} onChange={(e)=>setConfirmMpin(e.target.value.replace(/\D/g,"").slice(0,6))} className="w-full rounded-lg border border-slate-300 px-3 py-2 mt-3" placeholder="Confirm MPIN" />
              <button disabled={!canSetMpin} onClick={()=>{if(canSetMpin){setMpin(""); setConfirmMpin(""); setMessage("MPIN updated.");}}} className={`mt-3 w-full rounded-lg py-2.5 font-semibold text-white ${canSetMpin?"bg-blue-600 hover:bg-blue-700":"bg-slate-300"}`}>Update MPIN</button>
            </Card>
          </div>
        )}
      </section>
    </main>
  );
}

function Card({ title, children }: { title: string; children: ReactNode }) {
  return <div className="rounded-2xl bg-white p-4 shadow-lg sm:p-6"><h2 className="mb-4 text-base font-semibold text-slate-800 sm:text-lg">{title}</h2>{children}</div>;
}

function Metric({ label, value }: { label: string; value: string }) {
  return <div className="rounded-xl border border-white/20 bg-white/10 px-4 py-3"><p className="text-xs text-blue-100">{label}</p><p className="mt-1 text-lg font-semibold">{value}</p></div>;
}

function TxRow({ t }: { t: Tx }) {
  return <div className="flex flex-col gap-2 rounded-xl border border-slate-100 px-4 py-3 sm:flex-row sm:items-center sm:justify-between"><div><p className="text-sm font-semibold text-slate-700">{t.title}</p><p className="text-xs text-slate-500">{t.date} | {t.mode}</p></div><p className={`text-sm font-semibold ${t.type==="credit"?"text-emerald-600":"text-rose-600"}`}>{t.type==="credit"?"+ ":"- "}Rs {t.amount.toLocaleString("en-IN")}</p></div>;
}

function Toggle({ label, on, setOn }: { label: string; on: boolean; setOn: (v: boolean) => void }) {
  return <div className="mb-3 flex items-center justify-between rounded-xl border border-slate-100 px-4 py-3"><p className="text-sm font-medium text-slate-700">{label}</p><button onClick={()=>setOn(!on)} className={`relative h-6 w-11 rounded-full ${on?"bg-blue-600":"bg-slate-300"}`}><span className={`absolute top-0.5 h-5 w-5 rounded-full bg-white transition ${on?"left-5":"left-0.5"}`} /></button></div>;
}

function Status({ label, value, warn }: { label: string; value: string; warn?: boolean }) {
  return <div className={`mb-2 flex items-center justify-between rounded-lg border px-3 py-2 text-sm ${warn?"border-amber-200 bg-amber-50 text-amber-700":"border-emerald-200 bg-emerald-50 text-emerald-700"}`}><span>{label}</span><span className="font-semibold">{value}</span></div>;
}
