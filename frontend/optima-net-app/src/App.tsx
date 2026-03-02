import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import PersonalInfo from './account-open-pages/PersonalInfo'
import './App.css'
import Navbar from './components/NavBar'
import WelcomePage from './account-open-pages/WelcomPage'
import UserDetailsPage from "./account-open-pages/UserDetailsPage";
import AddressDetailsPage from './account-open-pages/AddressDetailsPage'
import NominationPage from './account-open-pages/NominationPage'
import FormalityCheckPage from './account-open-pages/FormalityCheckPage'
import SetMPINPage from './account-open-pages/SetMPINPage'
import { DebitCardConfig } from './account-open-pages/DebitCardConfig'
import { DebitCardCreatedPopUp } from './account-open-pages/DebitCardCreatedPopUp'
import { OnboardingProvider } from './account-open-pages/OnboardingContext'
import AccoutDetailsPage from './account-open-pages/AccoutDetailsPage'
import SavingsDashboard from './account-open-pages/SavingsDashboard'
import PostAccountDashboard from './account-open-pages/PostAccountDashboard'
import LoginPage from './account-open-pages/LoginPage'
import MfaSetupPage from './account-open-pages/MfaSetupPage'

function App() {
  return (
    <>
      <Navbar />
      <OnboardingProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<SavingsDashboard />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/mfa-setup" element={<MfaSetupPage />} />
            <Route path="/open-savings" element={<PersonalInfo />} />
            <Route path="/welcome" element={<WelcomePage />} />
            <Route path="/user-details" element={<UserDetailsPage />} />
            <Route path="/address-details" element={<AddressDetailsPage />} />
            <Route path="/nomination" element={<NominationPage />} />
            <Route path="/formalities" element={<FormalityCheckPage />} />
            <Route path="/vbnmx" element={<SetMPINPage />} />
            <Route path="/virtual-card" element={<DebitCardConfig />} />
            <Route path="/debit-ad" element={<DebitCardCreatedPopUp />} />
            <Route path="/account-details" element={<AccoutDetailsPage />} />
            <Route path="/dashboard" element={<PostAccountDashboard />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </BrowserRouter>
      </OnboardingProvider>
    </>
  )
}

export default App;
