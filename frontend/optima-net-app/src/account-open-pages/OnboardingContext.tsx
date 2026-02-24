import { createContext, useContext, useState,type ReactNode } from "react";

interface OnboardingState {
  mpinSet: boolean;
  debitConfigured: boolean;
  setMpinSet: (value: boolean) => void;
  setDebitConfigured: (value: boolean) => void;
}

const OnboardingContext = createContext<OnboardingState | undefined>(undefined);

export const OnboardingProvider = ({ children }: { children: ReactNode }) => {
  const [mpinSet, setMpinSet] = useState(false);
  const [debitConfigured, setDebitConfigured] = useState(false);

  return (
    <OnboardingContext.Provider
      value={{ mpinSet, debitConfigured, setMpinSet, setDebitConfigured }}
    >
      {children}
    </OnboardingContext.Provider>
  );
};

export const useOnboarding = () => {
  const context = useContext(OnboardingContext);
  if (!context) throw new Error("useOnboarding must be used inside Provider");
  return context;
};