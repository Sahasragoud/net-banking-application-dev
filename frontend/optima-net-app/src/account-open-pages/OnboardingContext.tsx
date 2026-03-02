import { createContext, useContext, useState, type ReactNode } from "react";

interface RegistrationDraft {
  fullName: string;
  email: string;
  mobileNumber: string;
  dateOfBirth: string;
  pincode: string;
}

interface KycDraft {
  panNumber: string;
  aadhaarNumber: string;
  voterIdNumber: string;
  idType: "aadhaar" | "voter";
  addressLine: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
}

interface OnboardingState {
  mpinSet: boolean;
  debitConfigured: boolean;
  customerId: number | null;
  customerCode: string;
  accountNumber: string;
  registrationUserId: number | null;
  registrationToken: string;
  registrationDraft: RegistrationDraft;
  kycDraft: KycDraft;
  setMpinSet: (value: boolean) => void;
  setDebitConfigured: (value: boolean) => void;
  setCustomerData: (data: { customerId: number; customerCode: string }) => void;
  setAccountNumber: (accountNumber: string) => void;
  setRegistrationUserId: (userId: number | null) => void;
  setRegistrationToken: (token: string) => void;
  updateRegistrationDraft: (data: Partial<RegistrationDraft>) => void;
  updateKycDraft: (data: Partial<KycDraft>) => void;
}

const OnboardingContext = createContext<OnboardingState | undefined>(undefined);

const initialRegistrationDraft: RegistrationDraft = {
  fullName: "",
  email: "",
  mobileNumber: "",
  dateOfBirth: "",
  pincode: "",
};

const initialKycDraft: KycDraft = {
  panNumber: "",
  aadhaarNumber: "",
  voterIdNumber: "",
  idType: "aadhaar",
  addressLine: "",
  city: "",
  state: "",
  postalCode: "",
  country: "India",
};

export const OnboardingProvider = ({ children }: { children: ReactNode }) => {
  const [mpinSet, setMpinSet] = useState(false);
  const [debitConfigured, setDebitConfigured] = useState(false);
  const [customerId, setCustomerId] = useState<number | null>(null);
  const [customerCode, setCustomerCode] = useState("");
  const [accountNumber, setAccountNumber] = useState("");
  const [registrationUserId, setRegistrationUserId] = useState<number | null>(null);
  const [registrationToken, setRegistrationToken] = useState("");
  const [registrationDraft, setRegistrationDraft] = useState<RegistrationDraft>(initialRegistrationDraft);
  const [kycDraft, setKycDraft] = useState<KycDraft>(initialKycDraft);

  const setCustomerData = (data: { customerId: number; customerCode: string }) => {
    setCustomerId(data.customerId);
    setCustomerCode(data.customerCode);
  };

  const updateRegistrationDraft = (data: Partial<RegistrationDraft>) => {
    setRegistrationDraft((current) => ({ ...current, ...data }));
  };

  const updateKycDraft = (data: Partial<KycDraft>) => {
    setKycDraft((current) => ({ ...current, ...data }));
  };

  return (
    <OnboardingContext.Provider
      value={{
        mpinSet,
        debitConfigured,
        customerId,
        customerCode,
        accountNumber,
        registrationUserId,
        registrationToken,
        registrationDraft,
        kycDraft,
        setMpinSet,
        setDebitConfigured,
        setCustomerData,
        setAccountNumber,
        setRegistrationUserId,
        setRegistrationToken,
        updateRegistrationDraft,
        updateKycDraft,
      }}
    >
      {children}
    </OnboardingContext.Provider>
  );
};

export const useOnboarding = () => {
  const context = useContext(OnboardingContext);
  if (!context) {
    throw new Error("useOnboarding must be used inside Provider");
  }
  return context;
};
