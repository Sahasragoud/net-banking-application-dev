import { BrowserRouter, Route, Routes } from 'react-router-dom'
import PersonalInfo from './account-open-pages/PersonalInfo'
import './App.css'
import Navbar from './components/NavBar'
import WelcomePage from './account-open-pages/WelcomPage'
import UserDetailsPage from "./account-open-pages/UserDetailsPage";
import AddressDetailsPage from './account-open-pages/AddressDetailsPage'

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
      </Routes>
    </BrowserRouter>
    </>
  )
}

export default App;
