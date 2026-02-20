import React from "react";

interface Props {
  onAddNominee: () => void;
  onLater: () => void;
}

const NomineePopup: React.FC<Props> = ({
  onAddNominee,
  onLater,
}) => {
  return (
      <div className="fixed inset-0 backdrop-blur-sm bg-black/30 flex items-center justify-center z-50 px-4">
        <div className="w-full max-w-md bg-white rounded-2xl shadow-2xl p-8 text-center">

        {/* Heading */}
        <div className="flex justify-center mb-4">
            <div className="w-16 h-16 bg-blue-100 text-blue-600 flex items-center justify-center rounded-full text-2xl">
                👤
            </div>
        </div>
        <h2 className="text-xl font-semibold text-gray-800 mb-3">
          Add a Nominee
        </h2>

        {/* Sub Text */}
        <p className="text-sm text-gray-600 mb-6">
          Adding a nominee ensures your funds are securely transferred
          to your chosen person in unforeseen circumstances.
        </p>

        {/* Buttons */}
        <div className="space-y-3">
          <button
            onClick={onAddNominee}
            className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2.5 rounded-lg font-medium transition"
          >
            Add Nominee
          </button>

          <button
            onClick={onLater}
            className="w-full border border-gray-300 text-gray-700 py-2.5 rounded-lg font-medium hover:bg-gray-50 transition"
          >
            I'll Do Later
          </button>
        </div>

      </div>
    </div>
  );
};

export default NomineePopup;