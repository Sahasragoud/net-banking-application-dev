// Drop-in API client for frontend integration with OptimaNet v2 backend.
// Usage:
//   import { api } from "./api-client";
//   const customer = await api.registerCustomer({...});

export const API_BASE_URL =
  (typeof import.meta !== "undefined" && (import.meta as any).env?.VITE_API_BASE_URL) ||
  "http://localhost:8080";

type CustomerStatus = "PENDING_KYC" | "ACTIVE" | "BLOCKED" | "REJECTED";
type KycStatus = "NOT_STARTED" | "SUBMITTED" | "APPROVED" | "REJECTED";

export interface RegisterCustomerRequest {
  fullName: string;
  email: string;
  mobileNumber: string;
  dateOfBirth: string; // yyyy-mm-dd
}

export interface SubmitKycRequest {
  aadhaarNumber: string;
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
  remarks?: string;
}

export interface CustomerResponse {
  id: number;
  customerCode: string;
  fullName: string;
  email: string;
  mobileNumber: string;
  dateOfBirth: string;
  customerStatus: CustomerStatus;
  createdAt: string;
}

export interface KycCaseResponse {
  id: number;
  customerId: number;
  kycStatus: KycStatus;
  aadhaarNumberMasked: string;
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
  accountStatus: "ACTIVE" | "FROZEN" | "CLOSED";
  availableBalance: number;
  interestRate: number;
  createdAt: string;
  recentTransactions: SavingsTransactionResponse[];
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(init?.headers || {}),
    },
    ...init,
  });

  if (!res.ok) {
    let message = `Request failed with status ${res.status}`;
    try {
      const body = await res.json();
      message = body?.message || message;
    } catch {
      // ignore JSON parse errors
    }
    throw new Error(message);
  }

  if (res.status === 204) {
    return undefined as T;
  }

  return (await res.json()) as T;
}

export const api = {
  // Account creation
  registerCustomer(payload: RegisterCustomerRequest) {
    return request<CustomerResponse>("/api/v2/accounts/register", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },

  // KYC
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

  // Savings
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

  // User management
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
};
