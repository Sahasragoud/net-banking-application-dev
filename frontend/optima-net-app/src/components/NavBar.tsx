import { Globe } from "lucide-react";
import logo from "../assets/OptimaBankLogo.png";
// import { useNavigate } from "react-router-dom";

const NavBar = () => {
  // const navigate = useNavigate();

  const handleNavigateToHome = () =>{
    // navigate("/");
  }

  return (
    <header className="w-full border-b border-blue-300 bg-gradient-to-r from-blue-300 via-blue-400 to-blue-300 shadow-sm">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-2">

        {/* Left: Logo */}
          <img
            src={logo}
            alt="Optima Bank"
            width={200}
            height={40}
            className="cursor-pointer object-contain"
            onClick={handleNavigateToHome}
          />


        {/* Center: Banking Context */}
        <div className="hidden md:flex">
          <div className="flex items-center gap-2 rounded-full bg-green-50 px-5 py-1.5 text-sm font-medium text-green-800 ring-1 ring-green-200">
            <span className="h-2 w-2 rounded-full bg-green-500" />
            You are in <span className="font-semibold">Personal Banking</span>
          </div>
        </div>

        {/* Right: Actions */}
        <nav className="flex items-center gap-6">

          <a
            href="/login"
            className="text-sm font-semibold text-blue-800 hover:text-blue-900 transition-colors"
          >
            Login
          </a>
          <button
            type="button"
            aria-label="Language selector"
            className="flex items-center gap-1 text-sm font-medium text-blue-800 hover:text-blue-900 transition-colors"
          >
            <Globe className="h-5 w-5" />
            English
          </button>

        </nav>
      </div>
    </header>
  );
};

export default NavBar;
