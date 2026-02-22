import { useState } from "react";
import AccountOpeningLayout from "./AccountStepLayout";
import AccountStepLayout from "./AccountStepLayout";

/* =======================
   Type Definitions
======================= */

interface Nominee {
  name: string;
  address: string;
  relationship: string;
  age: number;
  guardianName?: string;
  guardianRelationship?: string;
}

interface FormData {
  depositorName: string;
  depositorAddress: string;
  nomineeName: string;
  nomineeAddress: string;
  relationship: string;
  age: string;
  guardianName: string;
  guardianRelationship: string;
}

/* =======================
   Component
======================= */

export default function NominationPage() {
  const [showForm, setShowForm] = useState<boolean>(false);
  const [confirmed, setConfirmed] = useState<boolean>(false);
  const [nominees, setNominees] = useState<Nominee[]>([]);

  const [formData, setFormData] = useState<FormData>({
    depositorName: "",
    depositorAddress: "",
    nomineeName: "",
    nomineeAddress: "",
    relationship: "",
    age: "",
    guardianName: "",
    guardianRelationship: "",
  });

  const isMinor = Number(formData.age) < 18 && formData.age !== "";

  /* =======================
     Handlers
  ======================= */

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleAddNominee = () => {
    const ageNumber = Number(formData.age);

    if (!formData.nomineeName || !formData.age) {
      alert("Nominee name and age are required.");
      return;
    }

    if (ageNumber <= 0 || ageNumber > 120) {
      alert("Enter a valid age between 1 and 120.");
      return;
    }

    if (ageNumber < 18) {
      if (!formData.guardianName || !formData.guardianRelationship) {
        alert("Guardian details are required for minor nominees.");
        return;
      }
    }

    const newNominee: Nominee = {
      name: formData.nomineeName,
      address: formData.nomineeAddress,
      relationship: formData.relationship,
      age: ageNumber,
      guardianName: ageNumber < 18 ? formData.guardianName : undefined,
      guardianRelationship:
        ageNumber < 18 ? formData.guardianRelationship : undefined,
    };

    setNominees((prev) => [...prev, newNominee]);

    // Reset nominee-related fields
    setFormData((prev) => ({
      ...prev,
      nomineeName: "",
      nomineeAddress: "",
      relationship: "",
      age: "",
      guardianName: "",
      guardianRelationship: "",
    }));

    setShowForm(false);
  };

  const handleSubmitFinal = () => {
    if (!confirmed) {
      alert("Please confirm before submitting.");
      return;
    }

    console.log("Depositor:", formData.depositorName);
    console.log("Nominees:", nominees);

    alert("Nomination Submitted Successfully!");
  };

  /* =======================
     UI
  ======================= */

  return (
    <AccountStepLayout step={7} totalSteps={7}>

        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-800">
            Nomination Details
          </h1>
          <p className="text-gray-500 mt-1 text-sm">
            Add nominee information for secure fund transfer
          </p>
        </div>

        {/* Depositor Section */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <div>
            <label className="text-sm font-medium">Depositor Name</label>
            <input
              type="text"
              name="depositorName"
              value={formData.depositorName}
              onChange={handleChange}
              className="w-full border rounded-xl px-4 py-2 mt-1"
            />
          </div>

          <div>
            <label className="text-sm font-medium">Depositor Address</label>
            <input
              type="text"
              name="depositorAddress"
              value={formData.depositorAddress}
              onChange={handleChange}
              className="w-full border rounded-xl px-4 py-2 mt-1"
            />
          </div>
        </div>

        {/* Add Nominee Button */}
        <button
          onClick={() => setShowForm(true)}
          className="bg-blue-600 text-white px-6 py-3 rounded-xl mb-6"
        >
          + Add Nominee
        </button>

        {/* Summary Table */}
        {nominees.length > 0 && (
          <div className="bg-gray-50 rounded-2xl p-6 border">
            <h2 className="text-xl font-semibold mb-4">Nominee Summary</h2>

            <table className="w-full border text-sm">
              <thead className="bg-gray-200">
                <tr>
                  <th className="p-2 text-left">Name</th>
                  <th className="p-2 text-left">Age</th>
                  <th className="p-2 text-left">Guardian</th>
                </tr>
              </thead>
              <tbody>
                {nominees.map((n, i) => (
                  <tr key={i} className="border-t">
                    <td className="p-2">{n.name}</td>
                    <td className="p-2">{n.age}</td>
                    <td className="p-2">
                      {n.age < 18
                        ? `${n.guardianName} (${n.guardianRelationship})`
                        : "N/A"}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            <div className="mt-6 flex items-center gap-2">
              <input
                type="checkbox"
                checked={confirmed}
                onChange={(e) => setConfirmed(e.target.checked)}
              />
              <label>
                I confirm the above nominee details are correct.
              </label>
            </div>

            <button
              onClick={handleSubmitFinal}
              className="mt-4 bg-green-600 text-white px-6 py-3 rounded-xl"
            >
              Submit
            </button>
          </div>
        )}
      {/* Modal */}
      {showForm && (
        <div className="fixed inset-0 backdrop-blur-sm bg-black/30 flex justify-center items-center">
          <div className="bg-white rounded-2xl shadow-xl p-6 w-full max-w-md">
            <h2 className="text-lg font-semibold mb-4">Add Nominee</h2>

            <div className="space-y-4">
              <input
                type="text"
                name="nomineeName"
                placeholder="Nominee Name"
                value={formData.nomineeName}
                onChange={handleChange}
                className="w-full border rounded-lg px-3 py-2"
              />

              <input
                type="number"
                name="age"
                placeholder="Age"
                value={formData.age}
                onChange={handleChange}
                className="w-full border rounded-lg px-3 py-2"
              />

              <input
                type="text"
                name="relationship"
                placeholder="Relationship"
                value={formData.relationship}
                onChange={handleChange}
                className="w-full border rounded-lg px-3 py-2"
              />

              {/* Guardian Fields if Minor */}
              {isMinor && (
                <>
                  <input
                    type="text"
                    name="guardianName"
                    placeholder="Guardian Name"
                    value={formData.guardianName}
                    onChange={handleChange}
                    className="w-full border rounded-lg px-3 py-2"
                  />

                  <input
                    type="text"
                    name="guardianRelationship"
                    placeholder="Guardian Relationship"
                    value={formData.guardianRelationship}
                    onChange={handleChange}
                    className="w-full border rounded-lg px-3 py-2"
                  />
                </>
              )}
            </div>

            <div className="flex justify-end gap-3 mt-6">
              <button
                onClick={() => setShowForm(false)}
                className="px-4 py-2 border rounded-lg"
              >
                Cancel
              </button>

              <button
                onClick={handleAddNominee}
                className="px-4 py-2 bg-blue-600 text-white rounded-lg"
              >
                Save
              </button>
            </div>
          </div>
        </div>
      )}
    </AccountStepLayout>
  );
}