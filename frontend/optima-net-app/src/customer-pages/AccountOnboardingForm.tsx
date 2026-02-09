import { useState } from "react";

const validators = {
  mobile: (v: string) => /^[6-9]\d{9}$/.test(v),
  email: (v: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v),
  pincode: (v: string) => /^\d{6}$/.test(v),
};

const AccountOnboardingForm = () => {
  const [form, setForm] = useState({
    mobile: "",
    email: "",
    pincode: "",
  });

const [touched, setTouched] = useState({
  mobile: false,
  email: false,
  pincode: false,
});

  const [submitting, setSubmitting] = useState(false);

  const errors = {
    mobile:
      touched.mobile && !validators.mobile(form.mobile)
        ? "Enter a valid 10-digit Indian mobile number"
        : "",
    email:
      touched.email && !validators.email(form.email)
        ? "Enter a valid email address"
        : "",
    pincode:
      touched.pincode && !validators.pincode(form.pincode)
        ? "Enter a valid 6-digit pincode"
        : "",
  };

  const isValid =
    validators.mobile(form.mobile) &&
    validators.email(form.email) &&
    validators.pincode(form.pincode);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };
   
  const handleBlur = (e) => {
    setTouched((prev) => ({
      ...prev,
      [e.target.name]: true,
    }));
    };


  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!isValid) return;

    setSubmitting(true);

    // API call placeholder
    await new Promise((r) => setTimeout(r, 1000));

    setSubmitting(false);
    alert("Details captured successfully. Verification will happen later.");
  };

  return (
    <div className="mx-auto max-w-md rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
      <h2 className="mb-1 text-xl font-semibold text-gray-900">
        Create Your Account
      </h2>
      <p className="mb-6 text-sm text-gray-600">
        Enter your basic details. Verification will be completed later.
      </p>

      <form onSubmit={handleSubmit} className="space-y-5">
        {/* Mobile */}
        <div>
          <label className="block text-sm font-medium text-gray-700">
            Mobile Number
          </label>
          <input
            name="mobile"
            maxLength={10}
            value={form.mobile}
            onChange={handleChange}
            onBlur={handleBlur}
            className="mt-1 w-full rounded-md border px-3 py-2 text-sm focus:border-blue-500 focus:outline-none"
            placeholder="xxxxxxxxxx"
          />
          {errors.mobile && (
            <p className="mt-1 text-xs text-red-600">{errors.mobile}</p>
          )}
        </div>

        {/* Email */}
        <div>
          <label className="block text-sm font-medium text-gray-700">
            Email Address
          </label>
          <input
            name="email"
            value={form.email}
            onChange={handleChange}
            onBlur={handleBlur}
            className="mt-1 w-full rounded-md border px-3 py-2 text-sm focus:border-blue-500 focus:outline-none"
            placeholder="example@gmail.com"
          />
          {errors.email && (
            <p className="mt-1 text-xs text-red-600">{errors.email}</p>
          )}
        </div>

        {/* Pincode */}
        <div>
          <label className="block text-sm font-medium text-gray-700">
            Pincode
          </label>
          <input
            name="pincode"
            maxLength={6}
            value={form.pincode}
            onChange={handleChange}
            onBlur={handleBlur}
            className="mt-1 w-full rounded-md border px-3 py-2 text-sm focus:border-blue-500 focus:outline-none"
            placeholder="XXXXXX"
          />
          {errors.pincode && (
            <p className="mt-1 text-xs text-red-600">{errors.pincode}</p>
          )}
        <p className="mb-6 text-sm text-gray-600">
          Enter your current residential pincode.
        </p>
        </div>

        {/* Submit */}
        <button
          type="submit"
          disabled={!isValid || submitting}
          className="w-full rounded-md bg-blue-600 py-2.5 text-sm font-semibold text-white transition disabled:cursor-not-allowed disabled:bg-blue-300"
        >
          {submitting ? "Submitting..." : "Continue"}
        </button>
      </form>
    </div>
  );
};

export default AccountOnboardingForm;

/**
 * “The form performs strict client-side validation for format and UX, but does not assume ownership of the data.
 * 
 *  It captures mobile, email, and pincode cleanly and submits them as an onboarding lead. 
 * 
 * Verification and trust decisions are intentionally deferred to later backend workflows.”
 */