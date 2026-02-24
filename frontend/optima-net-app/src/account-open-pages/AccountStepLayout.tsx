import type { ReactNode } from "react";

interface Props {
  step: number;
  totalSteps: number;
  children: ReactNode;
}

export default function AccountOpeningLayout({
  step,
  totalSteps,
  children,
}: Props) {
  const safeStep = Math.min(step, totalSteps);
  const progress = (safeStep / totalSteps) * 100;

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">

      <div className="w-full max-w-4xl h-[85vh] bg-white rounded-2xl shadow-2xl flex flex-col overflow-hidden">

        {/* Header (Sticky) */}
        <div className="sticky top-0 z-10 bg-gradient-to-r from-blue-600 to-indigo-600 text-white px-6 md:px-8 py-4 flex justify-between items-center">
          <span className="text-lg font-semibold">Optima Bank</span>
          <span className="text-sm font-medium">
            Step {safeStep} of {totalSteps}
          </span>
        </div>

        {/* Progress Bar */}
        <div className="w-full h-1 bg-gray-200">
          <div
            className="h-1 bg-orange-600 transition-all duration-500 ease-in-out"
            style={{ width: `${progress}%` }}
          />
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto px-6 md:px-10 py-6 md:py-8 scroll-smooth">
          {children}
        </div>

      </div>
    </div>
  );
}