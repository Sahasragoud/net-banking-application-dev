import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

interface FormData {
  mobile: string;
  email: string;
  pincode: string;
}

const PersonalInfo: React.FC = () => {
  const [formData, setFormData] = useState<FormData>({
    mobile: "",
    email: "",
    pincode: "",
  });

  const [touched, setTouched] = useState<Record<string, boolean>>({});

  const validateMobile = (mobile: string) => /^[6-9]\d{9}$/.test(mobile);
  const validateEmail = (email: string) =>
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  const validatePincode = (pincode: string) => /^\d{6}$/.test(pincode);

  const navigate = useNavigate();


  const isFormValid =
    validateMobile(formData.mobile) &&
    validateEmail(formData.email) &&
    validatePincode(formData.pincode);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;

    // Restrict numeric fields
    if (name === "mobile" || name === "pincode") {
      if (!/^\d*$/.test(value)) return;
    }

    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
    setTouched((prev) => ({ ...prev, [e.target.name]: true }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFormValid) return;
      navigate("/welcome");
    console.log("Submitted Data:", formData);
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 px-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-xl p-6 sm:p-8">
        <h2 className="text-xl font-semibold text-gray-800 mb-6 text-center">
          Open Zero Balance Savings Account Online
        </h2>

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Mobile */}

          {/* Mobile */}
            <div>
            <label className="block text-sm font-medium text-gray-600 mb-1">
                Mobile Number
            </label>

            <div className="flex items-center border border-gray-300 rounded-lg focus-within:ring-2 focus-within:ring-blue-500 bg-white">
                
                {/* Flag + Country Code */}
                <div className="flex items-center gap-2 px-3 border-r border-gray-200 bg-gray-50 rounded-l-lg">
                <span className="text-lg">🇮🇳</span>
                <span className="text-sm font-medium text-gray-700">+91</span>
                </div>

                {/* Mobile Input */}
                <input
                type="tel"
                name="mobile"
                maxLength={10}
                value={formData.mobile}
                onChange={handleChange}
                onBlur={handleBlur}
                placeholder="Enter 10-digit number"
                className="w-full px-3 py-2 rounded-r-lg focus:outline-none"
                />
            </div>

            {touched.mobile && !validateMobile(formData.mobile) && (
                <p className="text-xs text-red-500 mt-1">
                Enter valid Indian mobile number
                </p>
            )}
            </div>

          {/* Email */}
          <div>
            <label className="block text-sm font-medium text-gray-600 mb-1">
              Email ID
            </label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              onBlur={handleBlur}
              placeholder="Enter email address"
              className="w-full rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:outline-none px-3 py-2"
            />
            {touched.email && !validateEmail(formData.email) && (
              <p className="text-xs text-red-500 mt-1">
                Enter valid email address
              </p>
            )}
          </div>

          {/* Pincode */}
          <div>
            <label className="block text-sm font-medium text-gray-600 mb-1">
              Pincode
            </label>
            <input
              type="text"
              name="pincode"
              maxLength={6}
              value={formData.pincode}
              onChange={handleChange}
              onBlur={handleBlur}
              placeholder="Enter 6-digit pincode"
              className="w-full rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:outline-none px-3 py-2"
            />
            {touched.pincode && !validatePincode(formData.pincode) && (
              <p className="text-xs text-red-500 mt-1">
                Enter valid 6-digit pincode
              </p>
            )}
          </div>

          {/* Terms */}
          <p className="text-xs text-gray-500 mt-4 leading-relaxed">
            By proceeding you agree to accept all applications{" "}
            <span className="text-blue-600 underline cursor-pointer">
              Terms & Conditions
            </span>{" "}
            and{" "}
            <span className="text-blue-600 underline cursor-pointer">
              Privacy Policy
            </span>.
          </p>

          {/* Button */}
          <button
            type="submit"
            disabled={!isFormValid}
            className={`w-full mt-4 rounded-lg py-2.5 font-medium transition 
              ${
                isFormValid
                  ? "bg-blue-600 hover:bg-blue-700 text-white"
                  : "bg-gray-300 text-gray-500 cursor-not-allowed"
              }`}
          >
            Open Now
          </button>
        </form>
      </div>
    </div>
  );
};

export default PersonalInfo;
