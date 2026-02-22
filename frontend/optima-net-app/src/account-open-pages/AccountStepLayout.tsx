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
  const progress = (step / totalSteps) * 100;

  return (
    <div className="h-screen overflow-hidden bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center">

      <div className="w-full max-w-4xl h-[75vh] bg-white rounded-2xl shadow-2xl flex flex-col">

        {/* Header */}
        <div className="bg-gradient-to-r from-blue-600 to-indigo-600 text-white px-8 py-5 flex justify-between">
          <span className="text-lg font-semibold">Optima Bank</span>
          <span className="text-sm">
            Step {step} of {totalSteps}
          </span>
        </div>

        {/* Progress */}
        <div className="w-full h-1 bg-gray-200">
          <div
            className="h-1 bg-orange-600 transition-all duration-500"
            style={{ width: `${progress}%` }}
          />
        </div>

        {/* Scroll Area */}
        <div className="flex-1 overflow-y-auto px-10 py-8">
          {children}
        </div>

      </div>
    </div>
  );
}