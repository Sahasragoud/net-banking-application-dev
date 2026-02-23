import type React from "react";
import accountmade from "../assets/accountmade.jpg";

interface Props {
    MPIN : boolean,
    debitConfig : boolean
}
const AccountCreatedPopUp : React.FC<Props> = ({
    MPIN, 
    debitConfig
}) => {

    return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 px-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-2xl p-6 sm:p-8 animate-fadeIn">

        <div className="flex justify-center mb-6">
          <img
            src={accountmade}
            className="w-72 rounded-lg shadow"
          />
        </div>

        <div className="text-center space-y-2">
            <p>Your 101 Account is now ready</p>
        </div>
      </div>
    </div>
    );
}