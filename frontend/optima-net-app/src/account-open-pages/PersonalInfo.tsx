import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import AccountStepLayout from "./AccountStepLayout";

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

  const [touched, setTouched] = useState<Record<keyof FormData, boolean>>({
    mobile: false,
    email: false,
    pincode: false,
  });

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

    setFormData((prev) => ({ ...prev, [name]: value.trimStart() }));
  };

  const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    const fieldName = name as keyof FormData;
    setTouched((prev) => ({ ...prev, [fieldName]: true }));
    if (fieldName === "email") {
      setFormData((prev) => ({ ...prev, email: value.trim() }));
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFormValid) return;
    navigate("/welcome");
    console.log("Submitted Data:", formData);
  };

  const errors = {
    mobile:
      touched.mobile && !validateMobile(formData.mobile)
        ? "Enter valid Indian mobile number"
        : "",
    email:
      touched.email && !validateEmail(formData.email)
        ? "Enter valid email address"
        : "",
    pincode:
      touched.pincode && !validatePincode(formData.pincode)
        ? "Enter valid 6-digit pincode"
        : "",
  };

  return (
    <AccountStepLayout step={1} totalSteps={5}>
      <h2 className="mb-1 text-center text-xl font-semibold text-gray-800">
        Open Zero Balance Savings Account Online
      </h2>
      <p className="mb-6 text-center text-sm text-gray-500">
        Start with your basic contact details
      </p>

      <form onSubmit={handleSubmit} className="space-y-4" noValidate>
        {/* Mobile */}
        <div>
          <label
            htmlFor="mobile"
            className="mb-1 block text-sm font-medium text-gray-600"
          >
            Mobile Number
          </label>

          <div className="flex items-center rounded-lg border border-gray-300 bg-white focus-within:ring-2 focus-within:ring-blue-500">
            {/* Country Code */}
            <div className="flex items-center gap-2 rounded-l-lg border-r border-gray-200 bg-gray-50 px-3">
              <span className="text-sm font-medium text-gray-700">IN</span>
              <span className="text-sm font-medium text-gray-700">+91</span>
            </div>

            {/* Mobile Input */}
            <input
              id="mobile"
              type="tel"
              name="mobile"
              maxLength={10}
              value={formData.mobile}
              onChange={handleChange}
              onBlur={handleBlur}
              placeholder="Enter 10-digit number"
              autoComplete="tel-national"
              className="w-full rounded-r-lg px-3 py-2 focus:outline-none"
              aria-invalid={Boolean(errors.mobile)}
              aria-describedby={errors.mobile ? "mobile-error" : undefined}
            />
          </div>

          {errors.mobile && (
            <p id="mobile-error" className="mt-1 text-xs text-red-500">
              {errors.mobile}
            </p>
          )}
        </div>

        {/* Email */}
        <div>
          <label
            htmlFor="email"
            className="mb-1 block text-sm font-medium text-gray-600"
          >
            Email ID
          </label>
          <input
            id="email"
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="Enter email address"
            autoComplete="email"
            className="w-full rounded-lg border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            aria-invalid={Boolean(errors.email)}
            aria-describedby={errors.email ? "email-error" : undefined}
          />
          {errors.email && (
            <p id="email-error" className="mt-1 text-xs text-red-500">
              {errors.email}
            </p>
          )}
        </div>

        {/* Pincode */}
        <div>
          <label
            htmlFor="pincode"
            className="mb-1 block text-sm font-medium text-gray-600"
          >
            Pincode
          </label>
          <input
            id="pincode"
            type="text"
            name="pincode"
            maxLength={6}
            value={formData.pincode}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="Enter 6-digit pincode"
            autoComplete="postal-code"
            className="w-full rounded-lg border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            aria-invalid={Boolean(errors.pincode)}
            aria-describedby={errors.pincode ? "pincode-error" : undefined}
          />
          {errors.pincode && (
            <p id="pincode-error" className="mt-1 text-xs text-red-500">
              {errors.pincode}
            </p>
          )}
        </div>

        {/* Terms */}
        <p className="mt-4 text-xs leading-relaxed text-gray-500">
          By proceeding you agree to accept all applications{" "}
          <span className="cursor-pointer text-blue-600 underline">
            Terms and Conditions
          </span>{" "}
          and{" "}
          <span className="cursor-pointer text-blue-600 underline">
            Privacy Policy
          </span>
          .
        </p>

        {/* Button */}
        <button
          type="submit"
          disabled={!isFormValid}
          className={`mt-4 w-full rounded-lg py-2.5 font-medium transition 
              ${
                isFormValid
                  ? "bg-blue-600 hover:bg-blue-700 text-white"
                  : "bg-gray-300 text-gray-500 cursor-not-allowed"
              }`}
        >
          Open Now
        </button>
      </form>
    </AccountStepLayout>
  );
};

export default PersonalInfo;
