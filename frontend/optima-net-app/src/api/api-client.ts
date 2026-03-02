export const API_BASE_URL =
  (typeof import.meta !== "undefined" &&
    (import.meta as ImportMeta & { env?: Record<string, string> }).env?.VITE_API_BASE_URL) ||
  "http://localhost:8081";

export type CustomerStatus = "PENDING_KYC" | "LITE" | "ACTIVE" | "BLOCKED" | "REJECTED";
export type KycStatus = "NOT_STARTED" | "SUBMITTED" | "APPROVED" | "REJECTED";
export type KycDocumentType = "PAN" | "AADHAAR" | "VOTER_ID";

export interface RegisterCustomerRequest {
  fullName: string;
  email: string;
  mobileNumber: string;
  dateOfBirth: string;
}

export interface SubmitKycRequest {
  aadhaarNumber?: string;
  voterIdNumber?: string;
  panNumber: string;
  addressLine: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
}

export interface KycDecisionRequest {
  reason: string;
}

export interface OpenSavingsAccountRequest {
  initialDeposit: number;
  interestRate: number;
}

export interface BalanceOperationRequest {
  amount: number;
  mpin: string;
  sourceReference?: string;
  remarks?: string;
}

export interface StartRegistrationRequest {
  fullName: string;
  aadhaarNumber: string;
  mobileNumber: string;
  emailAddress: string;
}

export interface LoginRequest {
  customerCode: string;
  mpin: string;
  mfaCode?: string;
}

export interface DeviceInfo {
  deviceId: string;
  deviceFingerprint: string;
  deviceType: string;
  deviceModel: string;
  osVersion: string;
  appVersion: string;
}

export interface LoginSessionRequest {
  sessionId: number;
  otp: string;
  deviceInfo: DeviceInfo;
}

export interface AuthResult {
  authenticated: boolean;
  mfaRequired: boolean;
  customerId: number;
  customerCode: string;
  fullName: string;
  customerStatus: CustomerStatus;
}

export interface CustomerProfileRequest {
  occupation: string;
  incomeSource: string;
  yearlyIncome: string;
  maritalStatus: string;
  fatherName: string;
  motherMaidenName: string;
}

export interface CustomerProfileResponse extends CustomerProfileRequest {
  customerId: number;
}

export interface NomineeRequest {
  depositorName?: string;
  depositorAddress?: string;
  nomineeName: string;
  nomineeAddress?: string;
  relationship?: string;
  ageYears: number;
  guardianName?: string;
  guardianRelationship?: string;
}

export interface NomineeResponse extends NomineeRequest {
  id: number;
  customerId: number;
}

export interface PanVerificationResponse {
  panNumber: string;
  holderName: string;
  source: string;
}

export interface KycDocumentVerificationResponse {
  documentType: KycDocumentType;
  documentNumberMasked: string;
  holderName: string;
  source: string;
}

export interface CustomerResponse {
  id: number;
  customerCode: string;
  fullName: string;
  email: string;
  mobileNumber: string;
  dateOfBirth: string;
  customerStatus: CustomerStatus;
  mfaEnabled: boolean;
  createdAt: string;
}

export interface KycCaseResponse {
  id: number;
  customerId: number;
  kycStatus: KycStatus;
  aadhaarNumberMasked: string;
  voterIdNumberMasked: string;
  panNumberMasked: string;
  rejectionReason: string | null;
  submittedAt: string | null;
  reviewedAt: string | null;
}

export interface SavingsTransactionResponse {
  id: number;
  txnType: "DEPOSIT" | "WITHDRAWAL";
  amount: number;
  balanceAfterTxn: number;
  remarks: string | null;
  createdAt: string;
}

export interface SavingsAccountResponse {
  id: number;
  accountNumber: string;
  customerId: number;
  customerCode: string;
  holderName: string;
  customerStatus: CustomerStatus;
  bankName: string;
  branchName: string;
  ifscCode: string;
  upiHandle: string;
  accountStatus: "ACTIVE" | "FROZEN" | "CLOSED";
  availableBalance: number;
  interestRate: number;
  createdAt: string;
  recentTransactions: SavingsTransactionResponse[];
}

const inFlightRequests = new Map<string, Promise<unknown>>();

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const method = (init?.method || "GET").toUpperCase();
  const body = typeof init?.body === "string" ? init.body : "";
  const requestKey = `${method}:${path}:${body}`;
  const shouldDedupe = method !== "GET";

  if (shouldDedupe) {
    const existing = inFlightRequests.get(requestKey);
    if (existing) {
      return existing as Promise<T>;
    }
  }

  const promise = (async () => {
    const response = await fetch(`${API_BASE_URL}${path}`, {
      headers: {
        "Content-Type": "application/json",
        ...(init?.headers || {}),
      },
      ...init,
    });

    if (!response.ok) {
      let message = `Request failed with status ${response.status}`;
      try {
        const bodyResponse = (await response.json()) as { message?: string };
        message = bodyResponse.message || message;
      } catch {
        // ignore non-json response body
      }
      throw new Error(message);
    }

    if (response.status === 204) {
      return undefined as T;
    }

    return (await response.json()) as T;
  })();

  if (shouldDedupe) {
    inFlightRequests.set(requestKey, promise as Promise<unknown>);
    promise.finally(() => {
      inFlightRequests.delete(requestKey);
    });
  }

  return promise;
}

export const api = {
  startRegistration(payload: StartRegistrationRequest) {
    return request<{ userId: number }>("/api/auth/registration/start", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  verifyRegistrationOtp(payload: { userId: number; otp: string }) {
    return request<{ token: string }>("/api/auth/registration/verify", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  resendRegistrationOtp(userId: number) {
    return request<string>(`/api/auth/registration/${userId}/otp-resend`, {
      method: "POST",
    });
  },
  verifyPan(payload: { panNumber: string }) {
    return request<PanVerificationResponse>("/api/v2/kyc/pan/verify", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  verifyKycDocument(payload: { documentType?: KycDocumentType; documentNumber: string }) {
    return request<KycDocumentVerificationResponse>("/api/v2/kyc/document/verify", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  registerCustomer(payload: RegisterCustomerRequest) {
    return request<CustomerResponse>("/api/v2/accounts/register", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  submitKyc(customerId: number, payload: SubmitKycRequest) {
    return request<KycCaseResponse>(`/api/v2/kyc/${customerId}/submit`, {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  approveKyc(customerId: number) {
    return request<KycCaseResponse>(`/api/v2/kyc/${customerId}/approve`, {
      method: "PUT",
    });
  },
  rejectKyc(customerId: number, payload: KycDecisionRequest) {
    return request<KycCaseResponse>(`/api/v2/kyc/${customerId}/reject`, {
      method: "PUT",
      body: JSON.stringify(payload),
    });
  },
  getKyc(customerId: number) {
    return request<KycCaseResponse>(`/api/v2/kyc/${customerId}`);
  },
  openSavings(customerId: number, payload: OpenSavingsAccountRequest) {
    return request<SavingsAccountResponse>(`/api/v2/savings/${customerId}/open`, {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  deposit(accountNumber: string, payload: BalanceOperationRequest) {
    return request<SavingsAccountResponse>(`/api/v2/savings/${accountNumber}/deposit`, {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  withdraw(accountNumber: string, payload: BalanceOperationRequest) {
    return request<SavingsAccountResponse>(`/api/v2/savings/${accountNumber}/withdraw`, {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  getSavingsAccount(accountNumber: string) {
    return request<SavingsAccountResponse>(`/api/v2/savings/${accountNumber}`);
  },
  getSavingsByCustomer(customerId: number) {
    return request<SavingsAccountResponse[]>(`/api/v2/savings/customer/${customerId}`);
  },
  listUsers(status?: CustomerStatus) {
    const suffix = status ? `?status=${status}` : "";
    return request<CustomerResponse[]>(`/api/v2/users${suffix}`);
  },
  getUser(customerId: number) {
    return request<CustomerResponse>(`/api/v2/users/${customerId}`);
  },
  updateUserStatus(customerId: number, status: CustomerStatus) {
    return request<CustomerResponse>(`/api/v2/users/${customerId}/status/${status}`, {
      method: "PUT",
    });
  },
  loginV2(payload: LoginRequest) {
    return request<AuthResult>("/api/v2/users/login", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  getUserByCode(customerCode: string) {
    return request<CustomerResponse>(`/api/v2/users/by-code/${customerCode}`);
  },
  setupMfa(customerId: number) {
    return request<{ customerId: number; secret: string; otpauthUrl: string }>(
      `/api/v2/users/${customerId}/mfa/setup`,
      { method: "POST" }
    );
  },
  enableMfa(customerId: number, payload: { code: string }) {
    return request<CustomerResponse>(`/api/v2/users/${customerId}/mfa/enable`, {
      method: "PUT",
      body: JSON.stringify(payload),
    });
  },
  verifyMfa(customerId: number, payload: { code: string }) {
    return request<{ verified: boolean }>(`/api/v2/users/${customerId}/mfa/verify`, {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  saveProfile(customerId: number, payload: CustomerProfileRequest) {
    return request<CustomerProfileResponse>(`/api/v2/users/${customerId}/profile`, {
      method: "PUT",
      body: JSON.stringify(payload),
    });
  },
  getProfile(customerId: number) {
    return request<CustomerProfileResponse>(`/api/v2/users/${customerId}/profile`);
  },
  saveNominees(customerId: number, payload: NomineeRequest[]) {
    return request<NomineeResponse[]>(`/api/v2/users/${customerId}/nominees`, {
      method: "PUT",
      body: JSON.stringify(payload),
    });
  },
  getNominees(customerId: number) {
    return request<NomineeResponse[]>(`/api/v2/users/${customerId}/nominees`);
  },
  setMpin(customerId: number, payload: { mpin: string }) {
    return request<CustomerResponse>(`/api/v2/users/${customerId}/mpin`, {
      method: "PUT",
      body: JSON.stringify(payload),
    });
  },
};
