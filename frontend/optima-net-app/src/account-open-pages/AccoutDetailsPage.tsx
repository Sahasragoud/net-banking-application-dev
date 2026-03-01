import React from "react";
import AccountStepLayout from "./AccountStepLayout";
import { useNavigate } from "react-router-dom";

type AccountDetails = {
  crn: string;
  accountNumber: string;
  ifsc: string;
  upi: string;
};

type CardDetails = {
  cardNumber: string;
  expiry: string;
  cvv: string;
};

const accountDetails: AccountDetails = {
  crn: "12345678",
  accountNumber: "987654321234",
  ifsc: "ABCD0123456",
  upi: "sahasra@bank",
};

const cardDetails: CardDetails = {
  cardNumber: "XXXX XXXX XXXX 1234",
  expiry: "12/28",
  cvv: "123",
};

const AccoutDetailsPage: React.FC = () => {
  const navigate = useNavigate();

  const handleCopy = async () => {
    const text = `
CRN: ${accountDetails.crn}
Account Number: ${accountDetails.accountNumber}
IFSC Code: ${accountDetails.ifsc}
UPI Handle: ${accountDetails.upi}
    `;

    await navigator.clipboard.writeText(text);
    alert("Account details copied!");
  };

  return (
    <AccountStepLayout step={8} totalSteps={8}>

        {/* Heading Section */}
        <div className="text-center space-y-2">
          <h1 className="text-3xl font-bold text-gray-900">
            Your Account is Successfully Created
          </h1>
          <p className="text-gray-600">
            Your virtual savings account is ready to use.
          </p>
          <p className="font-semibold text-red-600 ">
            Complete full KYC to unlock all banking features.
          </p>
        </div>

        {/* Account Details Card */}
        <div className="bg-white rounded-2xl shadow-md p-6 space-y-4">
          <div className="text-lg font-semibold text-gray-800">
            🏦 MyBank
          </div>

          <h2 className="text-xl font-semibold border-b pb-2">
            Account Details
          </h2>

          <DetailRow label="CRN (Login ID)" value={accountDetails.crn} />
          <DetailRow label="Account Number" value={accountDetails.accountNumber} />
          <DetailRow label="IFSC Code" value={accountDetails.ifsc} />
          <DetailRow label="UPI Handle" value={accountDetails.upi} />

          <button
            onClick={handleCopy}
            className="w-full mt-4 bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-lg transition"
          >
            Copy Details
          </button>

          <button className="w-full bg-green-600 hover:bg-green-700 text-white py-3 rounded-lg transition font-semibold">
            Complete Full KYC
          </button>

          <button
            className="w-full bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-lg transition font-semibold"
            onClick={() => navigate("/dashboard")}
          >
            Continue to Transactions
          </button>
        </div>

        {/* Virtual Debit Card Section */}
        <div className="space-y-6 text-center mt-5">

          <div className="mx-auto w-80 h-48 rounded-2xl bg-gradient-to-br from-black to-red-800 text-white p-6 flex flex-col justify-between shadow-lg">
            <div className="text-sm tracking-widest">
              {cardDetails.cardNumber}
            </div>

            <div className="flex justify-between text-sm">
              <span>EXP: {cardDetails.expiry}</span>
              <span>CVV: {cardDetails.cvv}</span>
            </div>
          </div>

          <p className="text-red-600 text-sm font-medium">
            Do not share your card details with anyone.
          </p>

          <button className="w-full bg-green-600 hover:bg-green-700 text-white py-3 rounded-lg transition font-semibold">
            Complete Full KYC
          </button>
        </div>
      </AccountStepLayout>
  );
};

type DetailRowProps = {
  label: string;
  value: string;
};

const DetailRow: React.FC<DetailRowProps> = ({ label, value }) => (
  <div className="flex justify-between text-sm text-gray-700">
    <span>{label}</span>
    <span className="font-semibold">{value}</span>
  </div>
);

export default AccoutDetailsPage;
