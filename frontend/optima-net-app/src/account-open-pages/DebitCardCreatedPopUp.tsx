import React, { useState } from "react";
import AccountStepLayout from "./AccountStepLayout";
import ad from "../assets/credit-card-ad.jpg";
import { AccountCreatedPopUp } from "./AccountCreatedPopUp";

export const DebitCardCreatedPopUp: React.FC = () => {
  const [showPopup, setShowPopup] = useState(false);
  return (
    <AccountStepLayout step={8} totalSteps={8}>
      <div className="text-center">

        {/* Heading */}
        <h1 className="text-2xl font-semibold text-orange-800 flex items-center justify-center gap-2">
          101 First Credit Card
        </h1>

        {/* Subheading */}
        <p className="mt-3 text-gray-600">
          Congratulations <br />
          You are eligible for a credit card against a Fixed Deposit.
        </p>

        {/* Card Image */}
        <div className="mt-6 flex justify-center">
          <img
            src={ad}
            alt="Credit card display"
            className="w-64 h-60 object-cover rounded-xl shadow-lg"
          />
        </div>

        {/* Fees Section */}
        <div className="mt-8 flex items-center justify-center gap-12">
          <div>
            <p className="font-medium text-gray-800">Annual Fee</p>
            <p className="text-gray-500">₹0</p>
          </div>

          <div>
            <p className="font-medium text-gray-800">Joining Fee</p>
            <p className="text-gray-500">₹0</p>
          </div>
        </div>

        {/* Proceed Button */}
          <div className="mt-10">
            <button
              onClick={() => setShowPopup(true)}
              className="px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition"
            >
              Finish Setup
            </button>
          </div>
      </div>
    {showPopup && <AccountCreatedPopUp onClose={() => setShowPopup(false)} />}    </AccountStepLayout>
  );
};