import { BrowserRouter, Route, Routes } from 'react-router-dom'
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

function App() {
  return (
    <>
      <Navbar/>
      <BrowserRouter>
      <Routes>
        <Route path="/" element={<PersonalInfo/>} />
        <Route path="/welcome" element={<WelcomePage />} />
        <Route path="/user-details" element={<UserDetailsPage />} />
        <Route path="/address-details" element={<AddressDetailsPage />} />
        <Route path="/nomination" element={<NominationPage/>} />
        <Route path="/formalities" element={<FormalityCheckPage/>} />
        <Route path="/vbnmx" element={<SetMPINPage/>} />
        <Route path="/virtual-card" element={<DebitCardConfig/>} />
      </Routes>
    </BrowserRouter>
    </>
  )
}

export default App;
