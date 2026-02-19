import React from "react";

interface Props {
  pan: string;
  userName: string;
  onClose: () => void;
}

const PanVerifyPopUp: React.FC<Props> = ({
  pan,
  userName,
  onClose,
}) => {
  const handleProceed = () => {
    console.log("Verified PAN:", pan);
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 px-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-2xl p-6 sm:p-8 animate-fadeIn">

        {/* PAN Image */}
        <div className="flex justify-center mb-6">
          <img
            src="https://upload.wikimedia.org/wikipedia/commons/3/3f/Permanent_Account_Number_Card.jpg"
            alt="PAN Card"
            className="w-72 rounded-lg shadow"
          />
        </div>

        {/* Text */}
        <div className="text-center space-y-2">
          <p className="text-sm text-gray-600">
            The provided PAN number
          </p>
          <p className="text-base font-semibold text-gray-800">
            {pan}
          </p>
          <p className="text-sm text-gray-600 mt-2">
            belongs to
          </p>
          <p className="text-lg font-semibold text-gray-900">
            {userName}
          </p>

          <p className="text-xs text-blue-600 mt-3 font-medium">
            Fetched from NSDL
          </p>
        </div>

        {/* Buttons */}
        <div className="mt-6 space-y-3">
          <button
            onClick={handleProceed}
            className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2.5 rounded-lg font-medium transition"
          >
            Proceed
          </button>

          <button
            onClick={onClose}
            className="w-full border border-gray-300 text-gray-700 py-2.5 rounded-lg font-medium hover:bg-gray-50 transition"
          >
            Go Back
          </button>
        </div>

      </div>
    </div>
  );
};

export default PanVerifyPopUp;
