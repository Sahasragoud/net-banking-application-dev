import AccountStepLayout from "./AccountStepLayout"
import virtualCard from '../assets/virtualDebit.jpg';
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useOnboarding } from "./OnboardingContext";

export const DebitCardConfig : React.FC = () => {

    const [enableOnline, setEnableOnline] = useState(true);
    const [physicalCard, setPhysicalCard] = useState(false);

    const { setDebitConfigured } = useOnboarding();

    const navigate = useNavigate();
    const handleProceed = () => {
        setDebitConfigured(true);  
        navigate("/debit-ad");
    }
    return (
        <AccountStepLayout step={8} totalSteps={8}>
            <div className="text-center mb-6">
                {/*Heading*/}
                <h1 className="text-2xl font-semibold text-gray-800 flex items-center justify-center gap-2">
                    Prepare your debit Card
                </h1>

                {/*sub-heading*/}
                <p className="text-sm text-gray-500 text-center mb-6">
                    This is a Virtual Card
                </p>

            {/*Virtual card display */}
            <div className="mb-8 flex justify-center">
                <img 
                src={virtualCard}
                alt="Visrtual Debit Card"
                className="w-96 rounded-xl shadow-lg"
                />
            </div>
        </div>

        
            {/*Toggle Section */}
            <div className="flex items-center justify-between mb-6">
                <div>
                    <p className="font-medium">Allow for online use</p>
                    <p className="text-sm text-gray-500 mb-6">Enable transactions on e-commerce platforms.
                    </p>
                </div>

            <button
            onClick={() => setEnableOnline(!enableOnline)}
            className={`w-12 h-6 flex items-center rounded-full p-1 transition ${
                enableOnline ? "bg-blue-700" : "bg-gray-300"
                }`}
            >
                <div className={`bg-white w-4 h-4 rounded-full shadow-md transform transition ${
                    enableOnline ? "translate-x-6" : "translate-x-0"
                    }`}/>
            </button>
            </div>

        <hr className="my-6" />

        {/* Physical Card Section */}
        <div className="flex items-start justify-between mb-6">
          <div>
            <p className="font-medium">Need a Physical Card?</p>
            <p className="text-sm text-gray-500 mt-1">
              Get a physical debit card delivered to your registered address.
            </p>
          </div>

          <input
            type="checkbox"
            checked={physicalCard}
            onChange={() => setPhysicalCard(!physicalCard)}
            className="w-5 h-5 mt-1"
          />
        </div>

        {/* Bottom Info Text */}
        <p className="text-sm text-center text-gray-500 mt-10">
            Complete your KYC to apply for a physical.You can manage card settings anytime from the Cards section after
          account activation.
        </p>

      {/* Fixed Bottom Button */}
      <div className="pt-6">
        <button className="w-full bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-lg font-medium transition"
        onClick={handleProceed}>
          Proceed
        </button>
    </div>

    </AccountStepLayout>
    )
}
