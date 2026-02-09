/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: "#1D4ED8",     // Strong banking blue
        surface: "#F1F5F9",     // Slightly darker than before
        border: "#CBD5E1",
        accent: "#0EA5E9",      // Sky blue accent
      },
    },
  },
  plugins: [],
};
