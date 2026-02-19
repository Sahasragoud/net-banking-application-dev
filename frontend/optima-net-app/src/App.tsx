import { BrowserRouter, Route, Routes } from 'react-router-dom'
import PersonalInfo from './account-open-pages/PersonalInfo'
import './App.css'
import Navbar from './components/NavBar'
import WelcomePage from './account-open-pages/WelcomPage'

function App() {

  return (
    <>
      <Navbar/>
      <BrowserRouter>
      <Routes>
        <Route path="/" element={<PersonalInfo/>} />
        <Route path="/welcome" element={<WelcomePage />} />
      </Routes>
    </BrowserRouter>
    </>
  )
}

export default App
