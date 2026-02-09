import './App.css'
import Navbar from './components/NavBar'
import AccountOnboardingForm from './customer-pages/AccountOnboardingForm'

function App() {

  return (
    <>
      <Navbar/>
      <div className="test p-10 text-white">CSS TEST</div>;
      <AccountOnboardingForm/>
    </>
  )
}

export default App
